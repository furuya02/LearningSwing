package bjd.sock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.Iterator;

import bjd.ThreadBase;
import bjd.net.Ip;
import bjd.net.OperateCrlf;
import bjd.net.Ssl;
import bjd.net.TcpQueue;
import bjd.server.OneServer;
import bjd.util.Bytes;

public class TcpObj extends SockObj {
	
	private SockKind sockKind;

	//ALL
	private Selector selector = null;
	//SERVER
	private ServerSocketChannel serverChannel = null;
	//ACCEPT・CLIENT
	private SocketChannel channel = null;  //ACCEPTの場合は、コンストラクタでコピーされる
	private Thread t = null; //select(read)で待機するスレッド
	private Object oneSsl;
	private TcpQueue tcpQueue = new TcpQueue();
	private ByteBuffer recvBuf = ByteBuffer.allocate(TcpQueue.MAX());

	//SERVER
	public TcpObj() {
		sockKind = sockKind.SERVER;

		//************************************************
		//selector/channel生成
		//************************************************
		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
		} catch (Exception ex) {
			setException(ex);
			return;
		}
	}
	//CLIENT
	public TcpObj(Ip ip, int port, int timeout, Ssl ssl) {
		//SSL通信を使用する場合は、このオブジェクトがセットされる 通常の場合は、null
		//this.ssl = ssl;

		sockKind = SockKind.CLIENT;
		tcpQueue = new TcpQueue();

		//************************************************
		//selector/channel生成
		//************************************************
		try {
			selector = Selector.open();

			channel = SocketChannel.open();
			channel.configureBlocking(false);
			
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		//************************************************
		//connect
		//************************************************
		InetSocketAddress address = new InetSocketAddress(ip.getInetAddress(), port);
		try {
			channel.connect(address);
			int msec = timeout;
			while (!channel.finishConnect()) {
				Thread.sleep(10);
				msec -= 10;
				if (msec < 0) {
					setError("timeout");
					return;
				}
			}
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		//************************************************
		//ここまでくると接続が完了している
		//************************************************
		set(SockState.Connect, (InetSocketAddress) channel.socket().getLocalSocketAddress(), (InetSocketAddress) channel.socket().getRemoteSocketAddress());

		//************************************************
		//read待機
		//************************************************
		try {
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				selectLoop();
			}
		});
		t.start();
	}

	//ACCEPT
	public TcpObj(SocketChannel channel) {
		
		sockKind = SockKind.ACCEPT;
		tcpQueue = new TcpQueue();

		//************************************************
		//selector/channel生成
		//************************************************
		try {
			this.channel = channel;
			this.channel.configureBlocking(false);
			selector = Selector.open();
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		//************************************************
		//ここまでくると接続が完了している
		//************************************************
		set(SockState.Connect, (InetSocketAddress) channel.socket().getLocalSocketAddress(), (InetSocketAddress) channel.socket().getRemoteSocketAddress());

		//************************************************
		//read待機
		//************************************************
		try {
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				selectLoop();
			}
		});
		t.start();
	}

	//ACCEPT・CLIENT
	private void selectLoop() {

		//Acceptの場合は、Connectの間だけループする
		while (getSockState() == SockState.Connect) {
			try {
				if (selector.select() <= 0) {
					break;
				}
			} catch (IOException ex) {
				setException(ex);
				return;
			}
			for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
				SelectionKey key = (SelectionKey) it.next();
				it.remove();
				if (key.isReadable()) {
					doRead(channel);
				}
			}
		}
	}
	//ACCEPT・CLIENT
	private void doRead(SocketChannel channel) {
		recvBuf.limit(tcpQueue.getSpace()); //受信できるのは、TcpQueueの空きサイズ分だけ
		try {
			recvBuf.clear();
			if (channel.read(recvBuf) < 0) {
				//切断されている
				setError("channel.read()<0");
				return;
			}
			
			byte[] buf = new byte[recvBuf.position()];
			recvBuf.flip();
			recvBuf.get(buf);

			tcpQueue.enqueue(buf, buf.length);

		} catch (Exception ex) {
			setException(ex);
		}
	}
	//ACCEPT・CLIENT
	public int length() {
		return tcpQueue.length();
	}
	//ACCEPT・CLIENT
	public byte[] recv(int len, int timeout) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, timeout);

		byte[] buffer = new byte[0];
		try {
			if (len <= tcpQueue.length()) {
				// キューから取得する
				buffer = tcpQueue.dequeue(len);
			} else {
				while (true) {
					Thread.sleep(0);
					if (0 < tcpQueue.length()) {
						//size=受信が必要なバイト数
						int size = len - buffer.length;
						//受信に必要なバイト数がバッファにない場合
						if (size > tcpQueue.length()) {
							size = tcpQueue.length(); //とりあえずバッファサイズ分だけ受信する
						}
						byte[] tmp = tcpQueue.dequeue(size);
						buffer = Bytes.create(buffer, tmp);
						if (len <= buffer.length) {
							break;
						}
					} else {
						if (getSockState() != SockState.Connect) {
							return null;
						}
						Thread.sleep(10); 
					}
					if (c.compareTo(Calendar.getInstance()) < 0) {
						buffer = tcpQueue.dequeue(len); //タイムアウト
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		//trace(TraceKind.Recv, buffer, false);//noEncode = false;テキストかバイナリかは不明
		return buffer;
	}
	//ACCEPT・CLIENT
	public int send(byte[] buf) {
		try {
			if (oneSsl != null) {
				//return oneSsl.Write(buffer, buffer.length);
			}
			if (getSockState() == SockState.Connect){
				ByteBuffer byteBuffer = ByteBuffer.allocate(buf.length);
				byteBuffer.put(buf);
				byteBuffer.flip();
				int len = channel.write(byteBuffer);
				Thread.sleep(1); //次の動作が実行されるようにsleepを置く
				return len;
			}
		} catch (Exception ex) {
			setException(ex);
			//logger.set(LogKind.Error, this, 9000046, String.format("Length=%d %s", buffer.length, ex.getMessage()));
		}
		return -1;
	}

	//ALL
	@Override
	public void close() {
		//ACCEPT・CLIENT
		if (channel != null && channel.isOpen()) {
			try {
				selector.wakeup();
				channel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
		//SERVER
		if (serverChannel != null && serverChannel.isOpen()) {
			try {
				selector.wakeup();
				selector.close();
				serverChannel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
		setError("close()");
	}

	//SERVER
	public boolean bind(Ip bindIp, int port, int listenMax) {
		
		try {
			serverChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port), listenMax);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception ex) {
			setException(ex);
			return false;
		}
		set(SockState.Bind, (InetSocketAddress) serverChannel.socket().getLocalSocketAddress(), null);
		return true;
	}
	
	//SERVER
	public TcpObj select(ThreadBase threadBase) {
		while (threadBase.isLife()) {

			int n;
			try {
				n = selector.select(1);
				if (n < 0) {
					setError(String.format("selector.select(1)=%d",n));
					break;
				}
			} catch (Exception ex) {
				setException(ex);
				break;
			}
			if (n > 0) {
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (key.isAcceptable()) {
						try {
							return new TcpObj(serverChannel.accept()); //ACCEPT
						} catch (IOException ex) {
							//accept()が失敗した場合は処理を継続する
							ex.printStackTrace();
						}
					}
				}
			}
		}
		setError("isLife()==false");
		return null;
	}
	//TODO 未実装
	public byte[] lineRecv(int timeout, OperateCrlf yes, OneServer oneServer) {
		return null;
	}
}
