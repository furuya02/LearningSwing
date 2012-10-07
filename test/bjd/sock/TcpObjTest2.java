package bjd.sock;

import junit.framework.Assert;

import org.junit.Test;

import bjd.Kernel;
import bjd.ThreadBase;
import bjd.net.Ip;
import bjd.net.Ssl;
import bjd.util.TestUtil;

//**************************************************
// Echoサーバを使用したテスト
//**************************************************
public class TcpObjTest2 {
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
				//System.out.println(String.format("EchoServer bind"));
				while (isLife()) {
					final TcpObj child = tcpObj.select(this);
					if (child == null) {
						break;
					}
					//System.out.println(String.format("EchoServer child"));
					while (isLife() && child.getSockState() == SockState.Connect) {
						int len = child.length();
						if (len > 0) {
							//System.out.println(String.format("EchoServer len=%d", len));
							byte[] buf = child.recv(len, 100);
							child.send(buf);
						}
					}
				}
			}
		}
	}
	@Test
	public void a001() {

		TestUtil.dispHeader("a001 Echoサーバに送信して、たまったデータサイズ（length）を確認する");

		String addr = "127.0.0.1";
		int port = 9999;

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();

		int timeout = 100;
		Ssl ssl = null;
		TcpObj tcpObj = new TcpObj(new Ip(addr), port, timeout, ssl);
		TestUtil.dispPrompt(this,"tcpObj = new TcpObj()");

		int max = 1000;
		byte[] tmp = new byte[max];

		for (int i = 0; i < 10; i++) {
			tcpObj.send(tmp);
			TestUtil.dispPrompt(this,String.format("tcpObj.send(%dbyte)",tmp.length));

			//送信データが到着するまで、少し待機する
			int sleep=100; //あまり短いと、Testを全部一緒にまわしたときにエラーとなる
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TestUtil.dispPrompt(this,String.format("Thread.sleep(%d)",sleep));

			TestUtil.dispPrompt(this,String.format("tcpObj.length()=%d",tcpObj.length()));
			Assert.assertEquals((i + 1) * max, tcpObj.length());
		}
		TestUtil.dispPrompt(this,String.format("tcpObj.close()"));
		tcpObj.close();
		echoServer.stop();
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
		TestUtil.dispPrompt(this,"tcpObj = new TcpObj()");

		int max = 10000;
		int loop = 10;
		byte[] tmp = new byte[max];
		for (int i = 0; i < max; i++) {
			tmp[i] = (byte) i;
		}

		int recvCount = 0;
		for (int i = 0; i < loop; i++) {
			TestUtil.dispPrompt(this,String.format("tcpObj.send(%dbyte)", tmp.length));
			tcpObj.send(tmp);
			int len = 0;
			while (len == 0) {
				len = tcpObj.length();
			}
			byte[] b = tcpObj.recv(len, timeout);
			recvCount += b.length;
			TestUtil.dispPrompt(this,String.format("len=%d  recv()=%d", len, b.length));
			for (int m = 0; m < max; m += 10) {
				Assert.assertEquals(b[m], tmp[m]); //送信したデータと受信したデータが同一かどうかのテスト
			}
		}
		TestUtil.dispPrompt(this,String.format("loop*max=%dbyte  recvCount:%d", loop * max, recvCount));
		Assert.assertEquals(loop * max, recvCount); //送信したデータ数と受信したデータ数が一致するかどうかのテスト

		TestUtil.dispPrompt(this,String.format("tcpObj.close()"));
		tcpObj.close();
		echoServer.stop();
	}

}