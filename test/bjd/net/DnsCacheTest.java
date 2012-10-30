package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import bjd.log.Logger;
import bjd.util.TestUtil;

public class DnsCacheTest {

	@Test
	public void a001() {
		
		TestUtil.dispHeader("a001 アドレスからホスト名を取得する");
		
		DnsCache dnsCache = new DnsCache();
		String addr = "59.106.27.208";
		String expected = "www1968.sakura.ne.jp";
		
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(addr);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String hostName = dnsCache.getHostName(inetAddress, new Logger(null, "", false, null));
		TestUtil.dispPrompt(this, String.format("getHostName(%s) = %s", addr, expected));
		assertThat(hostName, is(expected));		
	}

	@Test
	public void a002() {
		
		TestUtil.dispHeader("a002 ホスト名からアドレスを取得する");
		
		DnsCache dnsCache = new DnsCache();
		String hostName = "www.sapporoworks.ne.jp";
		String expected = "59.106.27.208";
		
		Ip[] ipList = dnsCache.getAddress(hostName);
		assertThat(ipList.length, is(1));		
		TestUtil.dispPrompt(this, String.format("getAddress(%s) = %s", hostName, expected));
		assertThat(ipList[0].toString(), is(expected));		
	}

}
