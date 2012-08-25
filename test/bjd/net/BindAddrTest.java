package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.util.TestUtil;

@RunWith(Enclosed.class)
public class BindAddrTest {
	
	@RunWith(Theories.class)
	public static class A001 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("new BindAddr()で生成してtotoString()で確認する");
		}

		@DataPoints
		public static Fixture[] datas = {
			new Fixture("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"), 
		
		};
		static class Fixture {
			private String expected;
			public Fixture(String expected) {
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("new BindAddr() toString()=\"%s\"\n", fx.expected);

			BindAddr bindAddr = new BindAddr();
			assertThat(bindAddr.toString(), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static class A002 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("new BindAddr(BindStyle,ipv4,ipv6)で生成してtotoString()で確認する");
		}

		@DataPoints
		public static Fixture[] datas = {
			new Fixture(BindStyle.V4ONLY, new Ip("192.168.0.1"), new Ip("::1"), "V4ONLY,192.168.0.1,::1"),
			new Fixture(BindStyle.V46DUAL, new Ip("0.0.0.0"), new Ip("ffe0::1"), "V46DUAL,0.0.0.0,ffe0::1"),
		};
		static class Fixture {
			private BindStyle bindStyle;
			private Ip ipV4;
			private Ip ipV6;
			private String expected;

			public Fixture(BindStyle bindStyle, Ip ipV4, Ip ipV6, String expected) {
				this.bindStyle = bindStyle;
				this.ipV4 = ipV4;
				this.ipV6 = ipV6;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("new BindAddr(%s,%s,%s) toString()=\"%s\"\n", fx.bindStyle, fx.ipV4, fx.ipV6, fx.expected);

			BindAddr bindAddr = new BindAddr(fx.bindStyle, fx.ipV4, fx.ipV6);
			assertThat(bindAddr.toString(), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static class A003 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("new BindAddr(str)で生成してtotoString()で確認する");
		}

		@DataPoints
		public static Fixture[] datas = {
			new Fixture("V4ONLY,192.168.0.1,::1", "V4ONLY,192.168.0.1,::1"),
		};
		static class Fixture {
			private String actual;
			private String expected;

			public Fixture(String actual, String expected) {
				this.actual = actual;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("new BindAddr(%s) toString()=\"%s\"\n", fx.actual, fx.expected);

			BindAddr bindAddr = new BindAddr(fx.actual);
			assertThat(bindAddr.toString(), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static class A004 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("new BindAddr(str)に無粉文字列を設定すると例外が発生する");
		}

		@DataPoints
		public static Fixture[] datas = {
			new Fixture(null),
			new Fixture("XXX,INADDR_ANY,IN6ADDR_ANY_INIT"), // 無効な列挙名
			new Fixture("V4ONLY,INADDR_ANY,192.168.0.1"), // IpV6にV4のアドレスを指定
			new Fixture("V4ONLY,::1,IN6ADDR_ANY_INIT"), // IpV4にV6のアドレスを指定
		};
		static class Fixture {
			private String actual;

			public Fixture(String actual) {
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("new BindAddr(%s) =>  throw IllegalArgumentException\n", fx.actual);
			
			try {
				BindAddr bindAddr = new BindAddr(fx.actual);
				Assert.fail("この行が実行されたらエラー");
			} catch (IllegalArgumentException ex) {
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}
	
	@RunWith(Theories.class)
	public static class A005 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("equals()の確認");
		}

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(new BindAddr("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"), new BindAddr("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"), true),
				new Fixture(new BindAddr("V4ONLY,INADDR_ANY,::1"), new BindAddr("V4ONLY,INADDR_ANY,::2"), false),			
				new Fixture(new BindAddr("V4ONLY,INADDR_ANY,::1"), null, false),
				new Fixture(new BindAddr("V4ONLY,INADDR_ANY,::1"), new BindAddr("V4ONLY,0.0.0.1,::1"), false),
				new Fixture(new BindAddr("V6ONLY,0.0.0.1,::1"), new BindAddr("V4ONLY,0.0.0.1,::1"), false),
		};
		static class Fixture {
			private BindAddr b1;
			private BindAddr b2;
			private boolean expected;

			public Fixture(BindAddr b1, BindAddr b2, boolean expected) {
				this.b1 = b1;
				this.b2 = b2;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("(\"%s\").equals(\"%s\")=%s\n", fx.b1, fx.b2, fx.expected);
			assertThat(fx.b1.equals(fx.b2), is(fx.expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class A006 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader(" 競合があるかどうかの確認");
		}

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(new BindAddr("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"), new BindAddr("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"), true),
				new Fixture(new BindAddr("V4ONLY,INADDR_ANY,::1"), new BindAddr("V4ONLY,INADDR_ANY,::2"), true),			
				new Fixture(new BindAddr("V4ONLY,INADDR_ANY,::1"), new BindAddr("V4ONLY,0.0.0.1,::1"), true),
				new Fixture(new BindAddr("V6ONLY,0.0.0.1,::1"), new BindAddr("V4ONLY,0.0.0.1,::1"), false),
				new Fixture(new BindAddr("V46DUAL,0.0.0.1,::1"), new BindAddr("V4ONLY,0.0.0.1,::1"), true),
		};
		static class Fixture {
			private BindAddr b1;
			private BindAddr b2;
			private boolean expected;

			public Fixture(BindAddr b1, BindAddr b2, boolean expected) {
				this.b1 = b1;
				this.b2 = b2;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("(\"%s\").checkCompetition(\"%s\")=%s\n", fx.b1, fx.b2, fx.expected);
			assertThat(fx.b1.checkCompetition(fx.b2), is(fx.expected));
		}
	}
}
