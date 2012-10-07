package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.util.TestUtil;

public final class LocalAddressTest {

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 remoteStr()で取得したテキストで、改めてLocalAddressを生成して、同じかどうかを確認"); //TESTヘッダ
		
		LocalAddress localAddress = new LocalAddress();
		String remoteStr = localAddress.remoteStr();

		localAddress = new LocalAddress(remoteStr);

		TestUtil.dispPrompt(this); //TESTプロンプト
		System.out.println(String.format("%s", remoteStr));
		TestUtil.dispPrompt(this); //TESTプロンプト
		System.out.println(String.format("%s", localAddress.remoteStr()));

		assertThat(remoteStr, is(localAddress.remoteStr()));
	}

}
