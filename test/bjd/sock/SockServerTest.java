package bjd.sock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetSocketAddress;

import junit.framework.Assert;

import org.junit.Test;

import bjd.net.Ip;
import bjd.net.ProtocolKind;
import bjd.util.TestUtil;

public class SockServerTest {

	@Test
	public void Test() {
		Execute execute = new Execute();
		execute.StartStop("a001 TCPサーバの 起動・停止時のSockState()の確認",ProtocolKind.Tcp);
		execute.StartStop("a002 UDPサーバの 起動・停止時のSockState()の確認",ProtocolKind.Udp);
		execute.GetLocalAddress("a003 TCPサーバのgetLocalAddress()の確認",ProtocolKind.Tcp);
		execute.GetLocalAddress("a004 UDPサーバのgetLocalAddress()の確認",ProtocolKind.Udp);
	}
	
	class Execute{
		public void StartStop(String title,final ProtocolKind protocolKind){
			
			TestUtil.dispHeader(title);

			final Ip bindIp = new Ip("127.0.0.1");
			final int port = 8881;
			final int listenMax = 10;

			final SockServer sockServer = new SockServer(protocolKind);
			TestUtil.dispPrompt(this, String.format("s = new SockServer()"));

			assertThat(sockServer.getSockState(), is(SockState.Idle));
			TestUtil.dispPrompt(this, String.format("s.getSockState()=%s", sockServer.getSockState()));
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					if(protocolKind == ProtocolKind.Tcp){
						sockServer.bind(bindIp,port,listenMax);
					}else{
						sockServer.bind(bindIp,port);
					}
				}
			});
			t.start();

			TestUtil.dispPrompt(this, String.format("s.bind()"));

			while (sockServer.getSockState() == SockState.Idle) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			assertThat(sockServer.getSockState(), is(SockState.Bind));
			TestUtil.dispPrompt(this, String.format("s.getSockState()=%s", sockServer.getSockState()));

			TestUtil.dispPrompt(this, String.format("s.close()"));
			sockServer.close(); //bind()にThreadBaseのポインタを送っていないため、isLifeでブレイクできないので、selectで例外を発生させて終了する

			assertThat(sockServer.getSockState(), is(SockState.Error));
			TestUtil.dispPrompt(this, String.format("getSockState()=%s", sockServer.getSockState()));
		}
		
		public void GetLocalAddress(String title,final ProtocolKind protocolKind){
			TestUtil.dispHeader(title);

			final Ip bindIp = new Ip("127.0.0.1");
//			Ip bindIp = new Ip("INADDR_ANY");
//			Ip bindIp = new Ip("0.0.0.0");
//			Ip bindIp = new Ip("::1");
			final int port = 9999;
			final int listenMax = 10;

			final SockServer sockServer = new SockServer(protocolKind);
			TestUtil.dispPrompt(this, String.format("s = new SockServer(%s)",protocolKind));

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					if(protocolKind == ProtocolKind.Tcp){
						sockServer.bind(bindIp,port,listenMax);
					}else{
						sockServer.bind(bindIp,port);
					}
				}
			});
			t.start();
			
			while (sockServer.getSockState() == SockState.Idle) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			InetSocketAddress localAddress = sockServer.getLocalAddress();
			assertThat(localAddress.toString(), is("/127.0.0.1:9999"));
			TestUtil.dispPrompt(this, String.format("s.getLocalAddress() = %s bind()後 localAddressの取得が可能になる", localAddress.toString()));
			
			InetSocketAddress remoteAddress = sockServer.getRemoteAddress();
			Assert.assertNull(remoteAddress);
			TestUtil.dispPrompt(this, String.format("s.getRemoteAddress() = %s SockServerでは、remoteＡｄｄｒｅｓｓは常にnullになる", remoteAddress));

			sockServer.close(); 
			
		}
	}
}