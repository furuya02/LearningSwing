package bjd.sock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.Iterator;

import bjd.net.Ip;
import bjd.net.Ssl;
import bjd.net.TcpQueue;
import bjd.util.Bytes;
import bjd.util.Debug;

public final class SockClient extends SockBase {
	private SocketChannel channel;
	private Ssl ssl = null;
	private TcpQueue tcpQueue = new TcpQueue();
	private Object oneSsl = null;
	//最大TcpQueueのMAXまでは樹脂可能
	private ByteBuffer recvBuf = ByteBuffer.allocate(TcpQueue.MAX());

	//【コンストラクタ（クライアント用）】
	public SockClient(Ip ip, int port, int timeout, Ssl ssl) {
		super(null); //SockClientの場合はISockは必要ない?

		//SSL通信を使用する場合は、このオブジェクトがセットされる 通常の場合は、null
		this.ssl = ssl;

		try {
			InetSocketAddress address = new InetSocketAddress(ip.getInetAddress(), port);

			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(address);

			int msec = timeout;
			while (!channel.finishConnect()) {
				Thread.sleep(100);
				msec -= 100;
				if (msec < 0) {
					//タイムアウト
					set(SockState.Error, null, null);
					lastError = "timeout";
				}
			}

			set(SockState.Connect, (InetSocketAddress) channel.socket().getLocalSocketAddress(), (InetSocketAddress) channel.socket().getRemoteSocketAddress());

			channel.register(selector, SelectionKey.OP_READ);

			//ここまでくると接続が完了している  BeginReceive();//接続完了処理（受信待機開始）
		} catch (Exception ex) {
			ex.printStackTrace();
			lastError = ex.getMessage();
			set(SockState.Error, null, null);
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

	private void selectLoop() {

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
		Debug.print(this, String.format("selectloop() end"));
	}

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
			recvBuf.get(buf);
			tcpQueue.enqueue(buf, buf.length);

		} catch (IOException e) {
			set(SockState.Error, null, null);
			e.printStackTrace();
		}
	}

	public int length() {
		return tcpQueue.length();
	}
	
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
	public int send(byte[] buf) {
		try {
			if (oneSsl != null) {
				//return oneSsl.Write(buffer, buffer.length);
			}
			if (getSockState() == SockState.Connect){
				ByteBuffer byteBuffer = ByteBuffer.allocate(buf.length);
				byteBuffer.put(buf);
				byteBuffer.flip();
				return channel.write(byteBuffer);
				//return socket.Send(buffer, 0, buffer.length, SocketFlags.None);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			lastError = ex.getMessage(); 
			//logger.set(LogKind.Error, this, 9000046, String.format("Length=%d %s", buffer.length, ex.getMessage()));
		}
		return -1;
	}

	public void close() {
		if (channel != null && channel.isOpen()) {
			try {
				selector.wakeup();
				channel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
	}

	/*
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    try{
	        while (socketChannel.isConnected()) {
	            System.out.print(">");
	            String inputText = reader.readLine();
	            if (inputText.equalsIgnoreCase("q")) {
	                break;
	            }
	            socketChannel.write(encoder.encode(CharBuffer.wrap(inputText)));
	            buffer.clear();
	            socketChannel.read(buffer);
	            buffer.flip();
	            System.out.println(socketChannel.socket().getInetAddress()
	                               + " : " + decoder.decode(buffer));
	        }
	    } finally {
	        if (socketChannel != null) {
	            System.out.println(socketChannel.socket().getInetAddress()
	                               + " cloesed.");
	            socketChannel.close();
	        }
	    }
	 * */
}
