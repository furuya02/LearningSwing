package bjd.sock;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import bjd.ILife;
import bjd.net.Ip;

//サーバソケット
public final class SockUdpServer extends SockBase {
	
	//private ServerSocketChannel serverChannel = null;
	private DatagramChannel datagramChannel = null;
	
	private Ip bindIp;
	private int port;
	private int multiple;
	private boolean isBusy = true;

	public SockUdpServer(ISock iSock, Ip bindIp, int port, int multiple) {
		super(iSock);
		this.bindIp = bindIp;
		this.port = port;
		this.multiple = multiple;

		try {
//			serverChannel = ServerSocketChannel.open();
//			serverChannel.configureBlocking(false);
			datagramChannel = DatagramChannel.open();
			datagramChannel.configureBlocking(false);
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
	public boolean bind(ILife iLife) {
//		try {
//			serverChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port), multiple);
//			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
//		} catch (Exception e) {
//			//cannelの初期化に失敗
//			lastError = e.getMessage();
//			set(SockState.Error, null, null);
//			return false;
//		}

		try {
			datagramChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port));
			datagramChannel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			//TODO Debug Print
			System.out.println(e.getMessage());
			
			lastError = e.getMessage();
			set(SockState.Error, null, null);
			return false;
		}
		
		
//		set(SockState.Bind, (InetSocketAddress) serverChannel.socket().getLocalSocketAddress(), null);
		
		clearBusy();

		while(isLife(iLife)){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}
		
		//サーバの場合は、Errorで無い限りループする
//		while (getSockState() != SockState.Error && isLife(iLife)) {
//			if (isBusy) {
//				continue; //iThread.accept()でclearBusy()が呼ばれるまで、次のselectを処理しない
//			}
//			try {
//				while (isLife(iLife)) {
//					int n = selector.select(1);
//					if (n != 0) {
//						break;
//					}
//				}
//			} catch (IOException ex) {
//				//ex.printStackTrace();
//				lastError = ex.getMessage();
//				set(SockState.Error, null, null);
//				return false;
//			}
//			setBusy(); //次にこのフラグがクリアされるのは、iThread.accept()側でclearBusy()を呼んだとき
//			for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
//				SelectionKey key = (SelectionKey) it.next();
//				it.remove();
//				if (key.isAcceptable()) {
//					try {
//						SocketChannel accept = serverChannel.accept();
//						accept.configureBlocking(false);
//						iSock.accept(accept, this);
//					} catch (IOException ex) {
//						//accept()が失敗した場合は処理を継続する
//						ex.printStackTrace();
//					}
//				}
//			}
//		}
//		set(SockState.Error, null, null);
		return true; //bind()を終了する

	}

	public void close() {
//		if (serverChannel != null && serverChannel.isOpen()) {
//			try {
//				selector.wakeup();
//				selector.close();
//				serverChannel.close();
//			} catch (IOException ex) {
//				ex.printStackTrace(); //エラーは無視する
//			}
//		}
		set(SockState.Error, null, null);
	}

}
