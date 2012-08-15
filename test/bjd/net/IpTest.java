package bjd.net;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.TestUtil;

@RunWith(Enclosed.class)
public class IpTest {

	@RunWith(Theories.class)
	public static class A001 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("文字列のコンストラクタで生成してtoString()で確認する");
		}

		@DataPoints
		public static Fixture[] datas = {
			// コンストラクタ文字列,toString()出力
			new Fixture("192.168.0.1", "192.168.0.1"), 
			new Fixture("255.255.0.254", "255.255.0.254"), 
			new Fixture("IN_ADDR_ANY", "0.0.0.0"), 
			new Fixture("0.0.0.0", "0.0.0.0"), 
			new Fixture("", "0.0.0.0"), 
			new Fixture("IN6ADDR_ANY_INIT", "IN6ADDR_ANY_INIT"), 
			new Fixture("::", "::0"), 
			new Fixture("::1", "::1"), 
			new Fixture("::809f", "::809f"), 
			new Fixture("ff34::809f", "ff34::809f"), 
			new Fixture("1234:56::1234:5678:90ab", "1234:56::1234:5678:90ab"), 
			new Fixture("fe80::7090:40f5:96f7:17db%13", "fe80::7090:40f5:96f7:17db%13"), 
			new Fixture("12::78:90ab", "12::78:90ab"), 
			new Fixture("[12::78:90ab]", "12::78:90ab"),  //[括弧付きで指定された場合]
			
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
			System.out.printf("new Ip(\"%s\") toString()=\"%s\"\n", fx.actual, fx.expected);

			Ip ip = new Ip(fx.actual);
			assertThat(ip.toString(), is(fx.expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class A002 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("文字列のコンストラクタで生成してIP.getInetAddress().toString()で確認する");
		}

		@DataPoints
		public static Fixture[] datas = {
			// コンストラクタ文字列,toString()出力
			new Fixture("192.168.0.1", "/192.168.0.1"), 
			new Fixture("255.255.0.254", "/255.255.0.254"), 
			new Fixture("IN_ADDR_ANY", "/0.0.0.0"), 
			new Fixture("0.0.0.0", "/0.0.0.0"), 
			new Fixture("", "/0.0.0.0"), 
			new Fixture("IN6ADDR_ANY_INIT", "/0:0:0:0:0:0:0:0"), 
			new Fixture("::", "/0:0:0:0:0:0:0:0%0"), 
			new Fixture("::1", "/0:0:0:0:0:0:0:1%0"), 
			new Fixture("::809f", "/0:0:0:0:0:0:0:809f%0"), 
			new Fixture("ff34::809f", "/ff34:0:0:0:0:0:0:809f%0"), 
			new Fixture("1234:56::1234:5678:90ab", "/1234:56:0:0:0:1234:5678:90ab%0"), 
			new Fixture("fe80::7090:40f5:96f7:17db%13", "/fe80:0:0:0:7090:40f5:96f7:17db%13"), 
			new Fixture("12::78:90ab", "/12:0:0:0:0:0:78:90ab%0"), 
			new Fixture("[12::78:90ab]", "/12:0:0:0:0:0:78:90ab%0"),  //[括弧付きで指定された場合]
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
			System.out.printf("new Ip(\"%s\") IP.getInetAddress().toString()=\"%s\"\n", fx.actual, fx.expected);

			Ip ip = new Ip(fx.actual);
			assertThat(ip.getInetAddress().toString(), is(fx.expected));
		}
	}

}
