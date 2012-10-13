package bjd.acl;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.Kernel;
import bjd.ctrl.CtrlType;
import bjd.log.Logger;
import bjd.net.Ip;
import bjd.option.Dat;
import bjd.util.TestUtil;


@RunWith(Enclosed.class)
public class AclListTest {
	@RunWith(Theories.class)
	public static final class A001 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("「許可する・許可しない」の動作確認"); //TESTヘッダ
		}

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,デフォルト値,toRegの出力
				new Fixture("192.168.0.1", "192.168.0.1", true),
				new Fixture("192.168.0.300", "192.168.0.1", false), //無効リスト
				new Fixture("192.168.0.0/24", "192.168.0.1", true),
				new Fixture("192.168.1.0/24", "192.168.0.1", false),
				new Fixture("192.168.1.0/200", "192.168.1.0", false), //無効リスト
				new Fixture("192.168.0.0-192.168.0.100", "192.168.0.1", true),
				new Fixture("192.168.0.2-192.168.0.100", "192.168.0.1", false),
				new Fixture("192.168.0.0-192.168.2.100", "192.168.0.1", true),
				new Fixture("192.168.0.1-5", "192.168.0.1", true),
				new Fixture("192.168.0.2-5", "192.168.0.1", false),
				new Fixture("192.168.0.*", "192.168.0.1", true),
				new Fixture("192.168.1.*", "192.168.0.1", false),
				new Fixture("192.168.*.*", "192.168.0.1", true),
				new Fixture("192.*.*.*", "192.168.0.1", true),
				new Fixture("*.*.*.*", "192.168.0.1", true),
				new Fixture("*", "192.168.0.1", true),
				new Fixture("xxx", "192.168.0.1", false), //無効リスト
				new Fixture("172.*.*.*", "192.168.0.1", false),
		};
		static class Fixture {
			private String aclStr;
			private String ip;
			private boolean expected;

			public Fixture(String aclStr, String ip, boolean expected) {
				this.aclStr = aclStr;
				this.ip = ip;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this); //TESTプロンプト
			
			Logger logger = new Logger(new Kernel(), "TEST", true, null);

			Ip ip = new Ip(fx.ip);
			Dat dat = new Dat(new CtrlType[] { CtrlType.TEXTBOX, CtrlType.ADDRESSV4 });
			dat.add(true, String.format("NAME\t%s", fx.aclStr));

			int enableNum = 0; //enableNum=0 のみを許可する
			AclList o = new AclList(dat, enableNum, logger);
			Assert.assertEquals(o.check(ip), fx.expected);

			enableNum = 1; //enableNum=1 のみを禁止する
			o = new AclList(dat, enableNum, logger);
			Assert.assertEquals(o.check(ip), !(fx.expected));

			System.out.printf("new AclV4(%s) => isHit(%s)=%s\n", fx.aclStr, fx.ip, fx.expected);			

		}
	}
}

