package bjd.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import javax.mail.internet.NewsAddress;

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
import bjd.sock.SockAccept;
import bjd.sock.SockState;
import bjd.sock.SockUdpServer;
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
			while (isLife()) {
				try {
					Thread.sleep(0);//これが無いと、別スレッドでlifeをfalseにできない
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(sockAccept.getSockState()!=SockState.Connect){
					System.out.println(">>>>>sockAccept.getSockState()!=SockState.Connect");
					break;
				}
			}
		}

		@Override
		public void read(DatagramChannel channel, SockUdpServer sockUdpServer) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

	}

	class MyClient{
		private Socket s = null;
		private String addr;
		private int port;
		private Thread t;
		private boolean life;

		public MyClient(String addr, int port) {
			this.addr = addr;
			this.port = port;
		}

		public void connet() {
			life = true;
			t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						s = new Socket(addr, port);
					} catch (IOException e) {
						e.printStackTrace();
					}
					while (life) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			t.start();
			//接続完了まで少し時間が必要
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void dispose() {
			try {
				s.shutdownInput();
				s.shutdownOutput();
				s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			life = false;
			
			while (t.isAlive()) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Test
	public final void a001() {

		TestUtil.dispHeader("a001 start() stop()　の繰り返し(負荷テスト)"); //TESTヘッダ

		OneBind oneBind = new OneBind(new Ip("127.0.0.1"), ProtocolKind.Tcp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("port", 9999);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);
		MyServer myServer = new MyServer(conf, oneBind);

		for (int i = 0; i < 5; i++) {
			TestUtil.dispPrompt(this, String.format("[i=%d]", i));
			myServer.start();
			TestUtil.dispPrompt(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(true));

			myServer.stop();
			TestUtil.dispPrompt(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(false));
		}

		myServer.dispose();
	}

	@Test
	public final void a002() {

		TestUtil.dispHeader("a002 new start() stop()　dispose の繰り返し(負荷テスト)"); //TESTヘッダ

		OneBind oneBind = new OneBind(new Ip("127.0.0.1"), ProtocolKind.Tcp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("port", 80);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);

		for (int i = 0; i < 5; i++) {
			TestUtil.dispPrompt(this, String.format("[i=%d]", i));
			MyServer myServer = new MyServer(conf, oneBind);

			myServer.start();
			TestUtil.dispPrompt(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(true));

			myServer.stop();
			TestUtil.dispPrompt(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(false));

			myServer.dispose();
		}
	}

	@Test
	public final void a003() {

		TestUtil.dispHeader("a003 count() multipleを超えたリクエストは破棄される"); //TESTヘッダ
		int multiple = 2;
		final int port = 8888;
		final String address = "127.0.0.1";

		OneBind oneBind = new OneBind(new Ip(address), ProtocolKind.Tcp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("port", port);
		conf.set("multiple", multiple);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);

		TestUtil.dispPrompt(this, String.format("s = new OneServer() %s:%d multiple=%d", address, port, multiple));
		MyServer myServer = new MyServer(conf, oneBind);
		myServer.start();

		ArrayList<MyClient> ar = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			System.out.println(String.format("[%d] client.connet()", i) );
			MyClient myClient = new MyClient(address, port);
			myClient.connet();
			ar.add(myClient);
		}
		
		System.out.println(String.format("s.count()=%d multiple以上は接続できない", multiple));
		assertThat(myServer.count(), is(multiple));

		myServer.stop(); 
		myServer.dispose();
		
		for (MyClient c : ar) {
			c.dispose();
		}
	}

}
