package bjd.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import bjd.ThreadBase;

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
		isBusy = false;
	}

	public void setBusy() {
		isBusy = true;
	}

	//このメソッドが呼ばれると、延々と接続を待ち受ける
	//止めるには、selector.close()する
	//接続が有った場合は、ISocket(OneServer)のaccept()を呼び出す
	public boolean bind(ThreadBase threadBase) {
		try {
			serverChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port), multiple);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			//cannelの初期化に失敗
			lastError = e.getMessage();
			sockState = SockState.Error;
			return false;
		}
		
		localAddress = (InetSocketAddress) serverChannel.socket().getLocalSocketAddress();
		
		sockState = SockState.Bind;
		clearBusy();

		//サーバの場合は、Errorで無い限りループする
		while (sockState != SockState.Error && isLife(threadBase)) {
			if (isBusy) {
				continue; //iThread.accept()でclearBusy()が呼ばれるまで、次のselectを処理しない
			}
			try {
				while (isLife(threadBase)) {
					int n = selector.select(1);
					if (n != 0) {
						break;
					}
				}
			} catch (IOException ex) {
				//ex.printStackTrace();
				lastError = ex.getMessage();
				sockState = SockState.Error;
				return false;
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
		sockState = SockState.Error;
		return true; //bind()を終了する

	}

	public void close() {
		if (serverChannel != null && serverChannel.isOpen()) {
			try {
				selector.wakeup();
				selector.close();
				serverChannel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
		sockState = SockState.Error; 
	}

}
