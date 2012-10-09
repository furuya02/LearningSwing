package bjd.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.Kernel;
import bjd.ctrl.CtrlType;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OptionSample;
import bjd.sock.SockObj;
import bjd.sock.SockState;
import bjd.sock.SockTcp;
import bjd.util.TestUtil;

public class OneServerTest2 {
	class EchoServer extends OneServer {
		public EchoServer(Conf conf, OneBind oneBind) {
			super(new Kernel(), "EchoServer", conf, oneBind);
		}

		@Override
		public String getMsg(int messageNo) {
			return null;
		}

		@Override
		protected void onStopServer() {
		}

		@Override
		protected boolean onStartServer() {
			return true;
		}

		@Override
		protected void onSubThread(SockObj sockObj) {
			SockTcp sockTcp = (SockTcp) sockObj;
			while (isLife() && sockObj.getSockState() == SockState.Connect) {
				try {
					Thread.sleep(0); //これが無いと、別スレッドでlifeをfalseにできない
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int len = sockTcp.length();
				if (0 < len) {
					int timeout = 10;
					byte[] buf = sockTcp.recv(len, timeout);
					sockTcp.send(buf);
					break; //echoしたらセッションを閉じる
				}
			}
		}
	}

	@Test
	public final void a001() {

		TestUtil.dispHeader("a001 EchoServer(TCP)");

		String addr = "127.0.0.1";
		int port = 9999;
		int timeout = 300;

		OneBind oneBind = new OneBind(new Ip(addr), ProtocolKind.Tcp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("port", port);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", timeout);

		EchoServer echoServer = new EchoServer(conf, oneBind);
		echoServer.start();

		//TCPクライアント

		int max = 10000;
		byte[] buf = new byte[max];
		buf[8] = 100; //CheckData

		for (int i = 0; i < 5; i++) {
			SockTcp sockTcp = new SockTcp(new Ip(addr), port, timeout, null);
			TestUtil.dispPrompt(this, String.format("[%d] sockTcp = new SockTcp(%s,%d)",i,addr,port));

			int len = sockTcp.send(buf);
			TestUtil.dispPrompt(this, String.format("sockTcp.send(%dbyte)", len));

			int sleep = 100;
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TestUtil.dispPrompt(this, String.format("Thread.sleep(%d)", sleep));

			len = sockTcp.length();
			if (0 < len) {
				byte[] b = sockTcp.recv(len, timeout);
				TestUtil.dispPrompt(this, String.format("sockTcp.recv()=%dbyte", b.length));
				assertThat(b[8], is(buf[8])); //CheckData
			}
			assertThat(max, is(len));
			
			sockTcp.close();

		}

		echoServer.dispose();

	}
}
