package bjd.server;

import static org.junit.Assert.*;


import org.junit.Test;

import bjd.Kernel;
import bjd.ctrl.CtrlType;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.net.SockAccept;
import bjd.net.SockObj;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OptionSample;
import bjd.util.Debug;

public class OneServerTest {

	class MyServer extends OneServer {
		public MyServer(Conf conf, OneBind oneBind) {
			super(new Kernel(),"TEST-SERVER", conf, oneBind);
		}
		
		@Override
		protected void onStopServer() {
            Debug.print(this,"onStopServer()");
		}

		@Override
		protected boolean onStartServer() {
            Debug.print(this,"onStartServer()");
			return true;
		}

		
		@Override
		public String getMsg(int messageNo) {
			String s = super.getMsg(messageNo);
			if (!s.equals("")) {
				return s;
			}
			return "";
		}

		@Override
		protected void onSubThread(SockAccept sockAccept) {
			Debug.print(this, "onSubThread() start");
            for(int i=0;i<20 && isLife();i++){
    			Debug.print(this, "接続中....");
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
	public void test() {
		OneBind oneBind = new OneBind(new Ip("127.0.0.1"), ProtocolKind.Tcp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("port", 8881);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);
		
		MyServer myServer = new MyServer(conf, oneBind);
		myServer.start();
		
		for(int i=0;i<5;i++){
			Debug.print(this,String.format("test() loop.. Count()=%d",myServer.Count()));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		
		myServer.dispose();
	}

}
