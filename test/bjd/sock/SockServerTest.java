package bjd.sock;
/*
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetSocketAddress;
import junit.framework.Assert;

import org.junit.Test;

import bjd.net.Ip;
import bjd.sock.SockServer;
import bjd.sock.SockState;
import bjd.util.TestUtil;


public final class SockServerTest {

	@Test
	public void a001() {
		
		TestUtil.dispHeader("a001 起動・停止時のSockStateの確認");

		Ip bindIp = new Ip("127.0.0.1");
		int port = 8881;
		int multiple = 10;

		final SockServer sockServer = new SockServer(null, bindIp, port, multiple);
		TestUtil.dispPrompt(this, String.format("s = new SockServer"));

		assertThat(sockServer.getSockState(), is(SockState.Idle));
		TestUtil.dispPrompt(this, String.format("s.getSockState()=%s", sockServer.getSockState()));
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				sockServer.bind(null);
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
	
	@Test
	public void a002() {
		
		TestUtil.dispHeader("a002 getLocalAddress()の確認");

		Ip bindIp = new Ip("127.0.0.1");
//		Ip bindIp = new Ip("INADDR_ANY");
//		Ip bindIp = new Ip("0.0.0.0");
//		Ip bindIp = new Ip("::1");
		int port = 9999;
		int multiple = 10;

		final SockServer sockServer = new SockServer(null, bindIp, port, multiple);
		TestUtil.dispPrompt(this, String.format("s = new SockServer"));

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				sockServer.bind(null);
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

		sockServer.close(); //bind()にThreadBaseのポインタを送っていないため、isLifeでブレイクできないので、selectで例外を発生させて終了する
	}

}
*/