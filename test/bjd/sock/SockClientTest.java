package bjd.sock;

import junit.framework.Assert;

import org.junit.Test;

import bjd.Kernel;
import bjd.ThreadBase;
import bjd.net.Ip;
import bjd.net.Ssl;
import bjd.util.TestUtil;

public final class SockClientTest {

	class EchoServer extends ThreadBase {
		private TcpObj tcpObj;
		private String addr;
		private int port;

		public EchoServer(String addr, int port) {
			super(new Kernel(), "NAME");
			tcpObj = new TcpObj();
			this.addr = addr;
			this.port = port;
		}

		@Override
		public String getMsg(int no) {
			return null;
		}

		@Override
		protected boolean onStartThread() {
			return true;
		}

		@Override
		protected void onStopThread() {
			tcpObj.close();
		}

		@Override
		protected void onRunThread() {
			if (tcpObj.bind(new Ip(addr), port, 1)) {

				System.out.println(String.format("bind"));

				while (isLife()) {
					final TcpObj child = tcpObj.select(this);
					System.out.println(String.format("shild"));
					if (child == null) {
						break;
					}
					while (isLife() && child.getSockState() == SockState.Connect) {
						int len = child.length();
						if (len > 0) {
							System.out.println(String.format("len=%d", len));
							byte[] buf = child.recv(len, 100);
							child.send(buf);
						}
					}
				}
			}
		}

		//		@Override
		//		public void accept(SocketChannel channel, SockServer sockServer) {
		//			//sockServer.clearBusy();
		//			System.out.println(String.format("accept"));
		//			
		//			ByteBuffer buf = ByteBuffer.allocate(4000);
		//
		//			while (isLife()) {
		//				try {
		//					buf.clear();
		//					int len = channel.read(buf);
		//					if(len==0){
		//						continue;
		//					}
		//					if (len < 0) {
		//						System.out.println(String.format("read()<0 => close"));
		//						channel.close();
		//						break;
		//					}
		//					//Debug.print(this, String.format("recv=%d", len));
		//					buf.flip();
		//					channel.write(buf);
		//
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}
		//
		//		@Override
		//		public void read(DatagramChannel channel, SockUdpServer sockUdpServer) {
		//			// TODO 自動生成されたメソッド・スタブ
		//			
		//		}

	}

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 Echoサーバに送信して、tcpQueueにたまったデータ（length）を確認する");

		String addr = "127.0.0.1";
		int port = 9999;

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();

		int timeout = 100;
		Ssl ssl = null;
		TcpObj tcpObj = new TcpObj(new Ip(addr), port, timeout, ssl);

		int max = 1000;
		byte[] tmp = new byte[max];

		for (int i = 0; i < 10; i++) {
			tcpObj.send(tmp);

			//送信データが到着するまで、少し待機する
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(String.format("send(%dbyte) sockClient.length=%d", max, tcpObj.length()));
			Assert.assertEquals((i + 1) * max, tcpObj.length());
		}
		tcpObj.close();
		echoServer.stop();
		System.out.println(String.format("finich"));
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 Echoサーバにsend(送信)して、tcpQueueのlength分ずつRecv()する");

		String addr = "127.0.0.1";
		int port = 9997;

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();

		int timeout = 100;
		Ssl ssl = null;
		TcpObj tcpObj = new TcpObj(new Ip(addr), port, timeout, ssl);

		int max = 10000;
		int loop = 10;
		byte[] tmp = new byte[max];
		for (int i = 0; i < max; i++) {
			tmp[i] = (byte) i;
		}

		int recvCount = 0;
		for (int i = 0; i < loop; i++) {
			System.out.println(String.format("send(%dbyte)", tmp.length));
			tcpObj.send(tmp);
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}

			int len = 0;
			while (len == 0) {
				len = tcpObj.length();

			}
			System.out.println(String.format("len=%d", len));
			byte[] b = tcpObj.recv(len, timeout);
			recvCount += b.length;
			System.out.println(String.format("len=%d  recv()=%d", len, b.length));
			for (int m = 0; m < max; m += 10) {
				Assert.assertEquals(b[m], tmp[m]); //送信したデータと受信したデータが同一かどうかのテスト
			}
		}
		System.out.println(String.format("send:%dbyte  recv:%d", loop * max, recvCount));
		Assert.assertEquals(loop * max, recvCount); //送信したデータ数と受信したデータ数が一致するかどうかのテスト

		tcpObj.close();
		echoServer.stop();
		System.out.println(String.format("finich"));
	}

}
