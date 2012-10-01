package bjd.sock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import bjd.ILife;
import bjd.net.OperateCrlf;

//サーバからacceptされたソケット
public final class SockAccept extends SockBase {

	private SocketChannel channel = null;
	private Thread t; //select(read)で待機するスレッド

	public SockAccept(SocketChannel channel, ISock iSock) {
		super(iSock);


		this.channel = channel;

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

	private void doRead(SocketChannel channel) {
		ByteBuffer buf = ByteBuffer.allocate(3000);
		Charset charset = Charset.forName("UTF-8");
		try {
			if (channel.read(buf) < 0) {
				//切断されている
				set(SockState.Disconnect, null, null);
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

	public byte[] lineRecv(int timeout, OperateCrlf yes, ILife iLife) {
		return new byte[0];
	}

}
