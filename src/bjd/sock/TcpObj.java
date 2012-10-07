package bjd.sock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Iterator;

import bjd.ILife;
import bjd.Kernel;
import bjd.ThreadBase;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.net.Ssl;
import bjd.net.TcpQueue;
import bjd.server.OneServer;
import bjd.util.Bytes;
import bjd.util.Debug;

public class TcpObj extends SockObj {
	
	private SockKind sockKind;

	//SERVER
	private ServerSocketChannel serverChannel = null;
	
	//ALL
	private Selector selector = null;

	//ACCEPT・CLIENT
	private SocketChannel channel = null;  //ACCEPTの場合は、コンストラクタでコピーされる
	private Thread t = null; //select(read)で待機するスレッド
	private Object oneSsl;
	private TcpQueue tcpQueue = new TcpQueue();
	private ByteBuffer recvBuf = ByteBuffer.allocate(TcpQueue.MAX());

	//SERVER
	public TcpObj() {
		sockKind = sockKind.SERVER;

		try {
			selector = Selector.open();
		} catch (Exception ex) {
			setError(ex.getMessage());
		}

		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
		} catch (Exception ex) {
			setError(ex.getMessage());
		}

	}
	//ACCEPT
	public TcpObj(SocketChannel channel) {
		
		sockKind = SockKind.ACCEPT;
		tcpQueue = new TcpQueue();

		this.channel = channel;

		try {
			selector = Selector.open();
		} catch (Exception ex) {
			setError(ex.getMessage());
		}

		if (getSockState() == SockState.Error) {
			return;
		}
		set(SockState.Connect, (InetSocketAddress) channel.socket().getLocalSocketAddress(), (InetSocketAddress) channel.socket().getRemoteSocketAddress());

		try {
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception ex) {
			setError(ex.getMessage());
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
	private void selectLoop() {

		//Acceptの場合は、Connectの間だけループする
		while (getSockState() == SockState.Connect) {
			try {
				if (selector.select() <= 0) {
					break;
				}
			} catch (IOException ex) {

				ex.printStackTrace();
				lastError = ex.getMessage();
				set(SockState.Error, null, null);
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
	//ACCEPT
	private void doRead(SocketChannel channel) {
		recvBuf.limit(tcpQueue.getSpace()); //受信できるのは、TcpQueueの空きサイズ分だけ
		try {
			recvBuf.clear();
			if (channel.read(recvBuf) < 0) {
				//切断されている
				set(SockState.Disconnect, null, null);
				return;
			}
			
			byte[] buf = new byte[recvBuf.position()];
			recvBuf.flip();
			recvBuf.get(buf);

			tcpQueue.enqueue(buf, buf.length);

		} catch (Exception ex) {
			set(SockState.Error, null, null);
			//e.printStackTrace();
		}
	}
	//ACCEPT
	public int length() {
		return tcpQueue.length();
	}
	//ACCEPT
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
	//ACCEPT
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
			ex.printStackTrace();
			lastError = ex.getMessage(); 
			//logger.set(LogKind.Error, this, 9000046, String.format("Length=%d %s", buffer.length, ex.getMessage()));
		}
		return -1;
	}

	

	public void close() {
		//ACCEPT
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
		set(SockState.Error, null, null);
	}

	//SERVER
	public boolean bind(Ip bindIp, int port, int listenMax) {
		
		try {
			serverChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port), listenMax);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			lastError = e.getMessage();
			set(SockState.Error, null, null);
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
					lastError = "select(1)<0";
					break;
				}
			} catch (IOException e) {
				lastError = e.getMessage();
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
		set(SockState.Error, null, null);
		return null;
	}
}
