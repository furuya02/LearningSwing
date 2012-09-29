package bjd.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Test;

import bjd.Kernel;
import bjd.ctrl.CtrlBindAddr;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlType;
import bjd.net.BindAddr;
import bjd.net.BindStyle;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.net.SockAccept;
import bjd.net.SockState;
import bjd.option.Conf;
import bjd.option.Crlf;
import bjd.option.Dat;
import bjd.option.OneVal;
import bjd.option.OptionSample;
import bjd.util.Debug;
import bjd.util.TestUtil;

public final class OneServerTest {

	class MyServer extends OneServer {
		public MyServer(Conf conf, OneBind oneBind) {
			super(new Kernel(), "TEST-SERVER", conf, oneBind);
		}
		
		@Override
		protected boolean onStartServer() {
			return true;
		}

		@Override
		protected void onStopServer() {
		}
		
		@Override
		public String getMsg(int messageNo) {
			return "";
		}

		@Override
		protected void onSubThread(SockAccept sockAccept) {
			Debug.print(this, "onSubThread() start");
			for (int i = 3; i >= 0 && isLife(); i--) {
				if (sockAccept.getSockState() != SockState.Connect) {
					Debug.print(this, String.format("接続中...sockAccept.getSockState!=Connect"));
					break;
				}

				Debug.print(this, String.format("接続中...あと%d回待機", i));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Debug.print(this, "onSubThread() end");
		}
	}
	
	@Test
	public void a001() {
		OneBind oneBind = new OneBind(new Ip("127.0.0.1"), ProtocolKind.Tcp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("protocolKind", 0); //TCP=0 UDP=1
		conf.set("port", 8888);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);
		
		MyServer myServer = new MyServer(conf, oneBind);
		myServer.start();
		for (int i = 3; i > 0; i--) {
			Debug.print(this, String.format("test() loop..あと%d回 isRunning()=%s Count()=%d", i, myServer.isRunnig(), myServer.count()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		myServer.dispose();
	}

	@Test
	public final void a002() {

		TestUtil.dispHeader("a002 start() stop()　の繰り返し(負荷テスト)"); //TESTヘッダ

		OneBind oneBind = new OneBind(new Ip("127.0.0.1"), ProtocolKind.Tcp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("port", 8888);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);
		MyServer myServer = new MyServer(conf, oneBind);

		for (int i = 0; i < 20; i++) {
			TestUtil.dispPrompt(this, String.format("[i=%d]", i));
			myServer.start();
			Debug.print(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(true));

			myServer.stop();
			Debug.print(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(false));
		}

		myServer.dispose();
	}

	@Test
	public final void a003() {

		TestUtil.dispHeader("a003 new start() stop()　dispose の繰り返し(負荷テスト)"); //TESTヘッダ

		OneBind oneBind = new OneBind(new Ip("127.0.0.1"), ProtocolKind.Tcp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("port", 80);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);

		for (int i = 0; i < 20; i++) {
			TestUtil.dispPrompt(this, String.format("[i=%d]", i));
			MyServer myServer = new MyServer(conf, oneBind);

			myServer.start();
			Debug.print(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(true));

			myServer.stop();
			Debug.print(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(false));

			myServer.dispose();
		}
	}
}
