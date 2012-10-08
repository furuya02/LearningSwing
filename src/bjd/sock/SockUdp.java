package bjd.sock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Calendar;
import java.util.Iterator;

import bjd.net.Ip;
import bjd.net.Ssl;
import bjd.net.TcpQueue;
import bjd.util.Bytes;
import bjd.util.Debug;

public final class SockUdp extends SockObj {

	private SockKind sockKind;

	//ALL
	private Selector selector = null;
	//ACCEPT・CLIENT
	//private SocketChannel channel = null;  //ACCEPTの場合は、コンストラクタでコピーされる
	private DatagramChannel channel = null;
	private Thread t = null; //select(read)で待機するスレッド
	private Object oneSsl;
	//private TcpQueue tcpQueue = new TcpQueue();
	private ByteBuffer recvBuf = ByteBuffer.allocate(1600);

	private SockUdp() {
		//隠蔽する
	}

	//ACCEPT
	public SockUdp(DatagramChannel channel) {

		sockKind = SockKind.ACCEPT;

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

		//UDP-ACCEPTの場合は、もう受信しているときに、ここに来るので、これ以上待機する必要はない
		doRead(channel);

		set(SockState.Connect, (InetSocketAddress) channel.socket().getLocalSocketAddress(), (InetSocketAddress) channel.socket().getRemoteSocketAddress());

		//あとは、クローズされるまで待機
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (getSockState() == SockState.Connect) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
	}
	//CLIENT
	public SockUdp(Ip ip, int port, int timeout, Ssl ssl) {
		//SSL通信を使用する場合は、このオブジェクトがセットされる 通常の場合は、null
		//this.ssl = ssl;

		sockKind = SockKind.CLIENT;

		//************************************************
		//selector/channel生成
		//************************************************
		try {
			selector = Selector.open();

			channel = DatagramChannel.open();
			channel.configureBlocking(false);

		} catch (Exception ex) {
			setException(ex);
			return;
		}

		InetSocketAddress address = new InetSocketAddress(ip.getInetAddress(), port);

		//ここで送信処理
		
		
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
				close();
			}
		});
		t.start();
	}
	//CLIENT
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
					break; // UDPの場合は、１度受信したら、もう待機しない
				}
			}
		}
	}

	//ACCEPT・CLIENT
	private void doRead(DatagramChannel channel) {
		recvBuf.clear();
		try {
			SocketAddress remoteAddress = channel.receive(recvBuf);
			Debug.print(this, String.format("addr= %s recvBuf.position()=%d", remoteAddress, recvBuf.position()));
			recvBuf.limit(recvBuf.position()); //受信したサイズをバッファのlimitに設定する
			recvBuf.flip(); //ポインタを最初に移動する
		} catch (IOException ex) {
			setException(ex);
		}
	}

	//ACCEPT・CLIENT
	public int length() {
		return recvBuf.limit() - recvBuf.position();
	}
	
	public byte[] recv(int len) {
		byte[] buf = new byte[length()];
		recvBuf.get(buf);
		return buf;
	}	
	
	//ACCEPT・CLIENT
	
	????これもUDP用に書き換える必要ある
	public int send(byte[] buf) {
		try {
			if (oneSsl != null) {
				//return oneSsl.Write(buffer, buffer.length);
			}
			if (getSockState() == SockState.Connect) {
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
		//ACCEPT
		if (sockKind == SockKind.ACCEPT) {
			return;
		}
		//CLIENT
		if (channel != null && channel.isOpen()) {
			try {
				selector.wakeup();
				channel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
		setError("close()");
	}
}
