package bjd.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import bjd.ThreadBase;
import bjd.util.Debug;

//サーバソケット
public final class SockServer extends SockBase {
	//    private static final int BUF_SIZE = 3000;
	private ServerSocketChannel serverChannel = null;

	private Ip bindIp;
	private int port;
	private int multiple;
	private boolean isBusy = true;

	public SockServer(Ip bindIp, int port, int multiple, ISocket iSocket) {
		super(iSocket);
		this.bindIp = bindIp;
		this.port = port;
		this.multiple = multiple;

		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
		} catch (Exception ex) {
			setError(ex.getMessage());
		}
	}

	//必要な処理が完了したら、このメソッドでbusyフラグをクリアする
	public void clearBusy() {
		Debug.print(this, "clearBusy()");
		isBusy = false;
	}

	public void setBusy() {
		Debug.print(this, "setBusy()");
		isBusy = true;
	}

	//このメソッドが呼ばれると、延々と接続を待ち受ける
	//止めるには、selector.close()する
	//接続が有った場合は、ISocket(OneServer)のaccept()を呼び出す
	public void bind() {
		Debug.print(this, "bind() start");
		try {
			serverChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port), multiple);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			//cannelの初期化に失敗
			lastError = e.getMessage();
			sockState = SockState.Error;
			return;
		}
		Debug.print(this, String.format("NonBlockingChannelEchoServerが起動しました(port=%d)", serverChannel.socket().getLocalPort()));

		clearBusy();

		while (true) {
			if (isBusy) {
				Debug.print(this, "◆isBusy==true");
				continue; //iThread.accept()でclearBusy()が呼ばれるまで、次のselectを処理しない
			}
			try {
				Debug.print(this, "select() start");
				int n = selector.select();
				if (n < 0) {
					Debug.print(this, "■select()<0");
					break;
				}
				Debug.print(this, String.format("select() end n=%d", n));
				if (n == 0) {
					Debug.print(this, "■selector.selectedKyes().size()==0 selector.close()されると、ここへ来る");
					break;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				lastError = ex.getMessage();
				sockState = SockState.Error;
				return;
			}

			setBusy(); //次にこのフラグがクリアされるのは、iThread.accept()側でclearBusy()を呼んだとき
			for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
				SelectionKey key = (SelectionKey) it.next();
				it.remove();
				if (key.isAcceptable()) {
					try {
						iSocket.accept(serverChannel.accept(), this);
					} catch (IOException ex) {
						//accept()が失敗した場合は処理を継続する
						ex.printStackTrace();
					}
				}
			}
		}
		sockState = sockState.Error;
		return; //bind()を終了する

	}

	private void doRead(SocketChannel channel) {
		ByteBuffer buf = ByteBuffer.allocate(3000);
		Charset charset = Charset.forName("UTF-8");
		String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
		try {
			if (channel.read(buf) < 0) {
				return;
			}
			buf.flip();
			System.out.print(remoteAddress + ":" + charset.decode(buf).toString());
			buf.flip();
			channel.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Debug.print(this, String.format("切断しました : %s", remoteAddress));
			try {
				channel.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				sockState = SockState.Error;
				lastError = ex.getMessage();
			}
		}
	}

	public void close() {
		Debug.print(this, "close() start");
		if (serverChannel != null && serverChannel.isOpen()) {
			try {
				Debug.print(this, "NonBlockingChannelEchoServerを停止します");
				selector.wakeup();
				selector.close();
				serverChannel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
		Debug.print(this, "close() end");
	}

}
