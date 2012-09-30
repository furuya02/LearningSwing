package bjd.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import bjd.util.Debug;

//サーバからacceptされたソケット
public final class SockAccept extends SockBase {

	private SocketChannel channel = null;
	private Thread t; //select(read)で待機するスレッド

	public SockAccept(SocketChannel channel, ISocket iSocket) {
		super(iSocket);


		this.channel = channel;

		if (sockState == SockState.Error) {
			return;
		}
		sockState = SockState.Connect;
		remoteAddress = (InetSocketAddress) channel.socket().getRemoteSocketAddress();
		localAddress = (InetSocketAddress) channel.socket().getLocalSocketAddress();

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

	private void selectLoop() {

		//Acceptの場合は、Connectの間だけループする
		while (sockState == SockState.Connect) {
			try {
				if (selector.select() <= 0) {
					break;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				lastError = ex.getMessage();
				sockState = SockState.Error;
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

	private void doRead(SocketChannel channel) {
		ByteBuffer buf = ByteBuffer.allocate(3000);
		Charset charset = Charset.forName("UTF-8");
		try {
			if (channel.read(buf) < 0) {
				//切断されている
				sockState = SockState.Disconnect;
				return;
			}
			buf.flip();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

}
