package bjd.log;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.ValidObjException;
import bjd.util.TestUtil;

public final class OneLogTest {

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 無効な文字列で初期化すると例外が発生する");

		TestUtil.dispPrompt(this, String.format("new OneLog(\"xxx\") => IllegalArgumentException"));

		try {
			new OneLog("xxx");
			Assert.fail("この行が実行されたらエラー");
		} catch (ValidObjException ex) {
			return;
		}
		Assert.fail("この行が実行されたらエラー");
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 初期化とtoString()");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(0)); // 1970.1.1で初期化
		LogKind logKind = LogKind.Debug;
		String nameTag = "NAME";
		long threadId = 100;
		String remoteHostname = "127.0.0.1";
		int messageId = 200;
		String message = "MSG";
		String detailInfomation = "DETAIL";
		OneLog oneLog = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);

		String expected = "1970/01/01 09:00:00\tDebug\t100\tNAME\t127.0.0.1\t    200\tMSG\tDETAIL";

		TestUtil.dispPrompt(this, String.format("new OneLog() => toString()=%s", expected));
		assertThat(oneLog.toString(), is(expected));

	}

	@Test
	public void a003() {

		TestUtil.dispHeader("a003 セキュアログの確認");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(0)); // 1970.1.1で初期化
		String nameTag = "NAME";
		long threadId = 100;
		String remoteHostname = "127.0.0.1";
		int messageId = 200;
		String message = "MSG";
		String detailInfomation = "DETAIL";

		LogKind logKind = LogKind.Debug;
		boolean expected = false;
		TestUtil.dispPrompt(this, String.format("new OneLog(LogKind=%s) => isSecure()=%s", logKind, expected));
		OneLog oneLog = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		assertThat(oneLog.isSecure(), is(expected));

		logKind = LogKind.Secure;
		expected = true;
		TestUtil.dispPrompt(this, String.format("new OneLog(LogKind=%s) => isSecure()=%s", logKind, expected));
		oneLog = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		assertThat(oneLog.isSecure(), is(expected));

	}

}
