package bjd.sock;

import org.junit.Test;

import junit.framework.Assert;
import bjd.Kernel;
import bjd.ThreadBase;
import bjd.net.Ip;
import bjd.net.ProtocolKind;
import bjd.net.Ssl;
import bjd.util.TestUtil;

//**************************************************
// Echoサーバを使用したテスト
//**************************************************
public final class UdpObjTest {
	class EchoServer extends ThreadBase {
		private SockServer sockServer;
		private String addr;
		private int port;

		public EchoServer(String addr, int port) {
			super(new Kernel(), "NAME");
			sockServer = new SockServer(ProtocolKind.Udp);
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
			sockServer.close();
		}

		@Override
		protected void onRunThread() {
			if (sockServer.bind(new Ip(addr), port)) {
				System.out.println(String.format("EchoServer bind"));
				while (isLife()) {
					final SockUdp child = (SockUdp) sockServer.select(this);
					if (child == null) {
						break;
					}
					System.out.println(String.format("EchoServer child"));
					while (isLife() && child.getSockState() == SockState.Connect) {
						int len = child.length();
						if (len > 0) {
							System.out.println(String.format("EchoServer len=%d", len));
							byte[] buf = child.recv();
							child.send(buf);
							//送信が完了したら、この処理は終了
							break;
						}
					}
				}
			}
		}
	}

	//@Test
	public void a001() {

		TestUtil.dispHeader("a001 Echoサーバに送信して、たまったデータサイズ（length）を確認する");

		String addr = "127.0.0.1";
		int port = 53; //TOCO DEBUG 9999^>53

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int max = 1000;
		byte[] tmp = new byte[max];

		int timeout = 100;
		Ssl ssl = null;
		SockUdp sock = new SockUdp(new Ip(addr), port, timeout, ssl, tmp);
		TestUtil.dispPrompt(this, "sock = new SockUdp()");

		
		//sock.send(tmp);
		//TestUtil.dispPrompt(this, String.format("sock.send(%dbyte)", tmp.length));
		
//		for (int i = 0; i < 100; i++) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

		for (int i = 0; i < 10; i++) {
			sock.send(tmp);
			TestUtil.dispPrompt(this, String.format("sock.send(%dbyte)", tmp.length));

			//送信データが到着するまで、少し待機する
			int sleep = 100; //あまり短いと、Testを全部一緒にまわしたときにエラーとなる
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TestUtil.dispPrompt(this, String.format("Thread.sleep(%d)", sleep));

			TestUtil.dispPrompt(this, String.format("sock.length()=%d", sock.length()));
			Assert.assertEquals((i + 1) * max, sock.length());
		}
		TestUtil.dispPrompt(this, String.format("sock.close()"));
		sock.close();
		echoServer.stop();
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 Echoサーバにsend(送信)して、length分ずつRecv()する");

		String addr = "127.0.0.1";
		int port = 53;

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();

		int timeout = 100;
		Ssl ssl = null;

		int max = 1500;
		int loop = 10;
		byte[] tmp = new byte[max];
		for (int i = 0; i < max; i++) {
			tmp[i] = (byte) i;
		}

		for (int i = 0; i < loop; i++) {

			SockUdp sockUdp = new SockUdp(new Ip(addr), port, timeout, ssl, tmp);
			TestUtil.dispPrompt(this, String.format("sockUdp = new SockUdp(%dbyte)", tmp.length));
			int len = 0;
			while (len == 0) {
				len = sockUdp.length();
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			TestUtil.dispPrompt(this, String.format("len=%d", len));
			byte[] b = sockUdp.recv();
			TestUtil.dispPrompt(this, String.format("b=recv()  b.length=%d", b.length));
			for (int m = 0; m < max; m += 10) {
				Assert.assertEquals(b[m], tmp[m]); //送信したデータと受信したデータが同一かどうかのテスト
			}
			sockUdp.close();
		}
		echoServer.stop();
	}
}
