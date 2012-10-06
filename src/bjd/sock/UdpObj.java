package bjd.sock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import bjd.ILife;
import bjd.Kernel;
import bjd.ThreadBase;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;

public final class UdpObj extends SockObj {
	
	private SockKind sockKind;

	//SERVER
	private Selector selector = null;
	private DatagramChannel datagramChannel = null;

	//ACCEPT
	private DatagramChannel channel = null;

	//ACCEPT
	public UdpObj(DatagramChannel channel){
		sockKind = SockKind.ACCEPT;

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

//		Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				selectLoop();
//			}
//		});
//		t.start();

	}

	
	//SERVER
	public UdpObj(InetKind inetKind) {
		sockKind = SockKind.SERVER;
		try {
			selector = Selector.open();
		} catch (Exception ex) {
			setError(ex.getMessage());
		}

		try {
			if (inetKind == InetKind.V4) {
				datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			} else {
				datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET6);
			}
			datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			datagramChannel.configureBlocking(false);
		} catch (Exception ex) {
			setError(ex.getMessage());
		}

	}



	public boolean bind(Ip bindIp,int port) {
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
		return true;
	}
	//SERVER
	public UdpObj select(ThreadBase threadBase) {
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
			if (n >= 0) {
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (key.isReadable()) {
						return new UdpObj((DatagramChannel) key.channel());
					}
				}
			}
		}
		set(SockState.Error, null, null);
		return null;
	}
}
