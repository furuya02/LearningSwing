package bjd.sock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetSocketAddress;

import junit.framework.Assert;

import org.junit.Test;

import bjd.net.Ip;
import bjd.util.TestUtil;


public class UdpObjTest {
	@Test
	public void a001() {
		TestUtil.dispHeader("a001 起動・停止時のSockState()の確認");

		final Ip bindIp = new Ip("127.0.0.1");
		final int port = 8881;
		final int listenMax = 10;

		final UdpObj sock = new UdpObj(); //SERVER
		TestUtil.dispPrompt(this, String.format("s = new UdpObj()"));

		assertThat(sock.getSockState(), is(SockState.Idle));
		TestUtil.dispPrompt(this, String.format("s.getSockState()=%s", sock.getSockState()));
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				sock.bind(bindIp,port);
			}
		});
		t.start();

		TestUtil.dispPrompt(this, String.format("s.bind()"));

		while (sock.getSockState() == SockState.Idle) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		assertThat(sock.getSockState(), is(SockState.Bind));
		TestUtil.dispPrompt(this, String.format("s.getSockState()=%s", sock.getSockState()));

		TestUtil.dispPrompt(this, String.format("s.close()"));
		sock.close(); //bind()にThreadBaseのポインタを送っていないため、isLifeでブレイクできないので、selectで例外を発生させて終了する

		assertThat(sock.getSockState(), is(SockState.Error));
		TestUtil.dispPrompt(this, String.format("getSockState()=%s", sock.getSockState()));
	}

	@Test
	public void a002() {
		
		TestUtil.dispHeader("a002 getLocalAddress()の確認");

		final Ip bindIp = new Ip("127.0.0.1");
//		final Ip bindIp = new Ip("INADDR_ANY");
//		final Ip bindIp = new Ip("0.0.0.0");
//		final Ip bindIp = new Ip("::1");
		final int port = 9999;
		final int listenMax = 10;

		final UdpObj sock = new UdpObj(); //SERVER
		TestUtil.dispPrompt(this, String.format("s = new UdpObj()"));

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				sock.bind(bindIp,port);
			}
		});
		t.start();
		
		while (sock.getSockState() == SockState.Idle) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		InetSocketAddress localAddress = sock.getLocalAddress();
		assertThat(localAddress.toString(), is("/127.0.0.1:9999"));
		TestUtil.dispPrompt(this, String.format("s.getLocalAddress() = %s bind()後 localAddressの取得が可能になる", localAddress.toString()));
		
		InetSocketAddress remoteAddress = sock.getRemoteAddress();
		Assert.assertNull(remoteAddress);
		TestUtil.dispPrompt(this, String.format("s.getRemoteAddress() = %s SockServerでは、remoteＡｄｄｒｅｓｓは常にnullになる", remoteAddress));

		sock.close(); 
	}
}
