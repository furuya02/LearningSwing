package bjd.net;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;

import junit.framework.Assert;

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
			new Fixture("INADDR_ANY", "INADDR_ANY"), 
			new Fixture("0.0.0.0", "0.0.0.0"), 
			new Fixture("IN6ADDR_ANY_INIT", "IN6ADDR_ANY_INIT"), 
			new Fixture("::", "::0"), 
			new Fixture("::1", "::1"), 
			new Fixture("::809f", "::809f"), 
			new Fixture("ff34::809f", "ff34::809f"), 
			new Fixture("1234:56::1234:5678:90ab", "1234:56::1234:5678:90ab"), 
			new Fixture("fe80::7090:40f5:96f7:17db%13", "fe80::7090:40f5:96f7:17db%13"), 
			new Fixture("12::78:90ab", "12::78:90ab"), 
			new Fixture("[12::78:90ab]", "12::78:90ab"),  //[括弧付きで指定された場合]
			new Fixture("fff::", "fff::"),
		
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
			new Fixture("INADDR_ANY", "/0.0.0.0"), 
			new Fixture("0.0.0.0", "/0.0.0.0"), 
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
	
	@RunWith(Theories.class)
	public static class A003 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("getIpV4()テスト");
		}
		@DataPoints
		public static Fixture[] datas = {
			// コンストラクタ文字列,toString()出力
			new Fixture(192, 168, 0, 1),
			new Fixture(127, 0, 0, 1),
			new Fixture(0, 0, 0, 0), 
			new Fixture(255, 255, 255, 255), 
			new Fixture(255, 255, 0, 254),
			
		};
		static class Fixture {
			private int n1;
			private int n2;
			private int n3;
			private int n4;

			public Fixture(int n1, int n2, int n3, int n4) {
				this.n1 = n1;
				this.n2 = n2;
				this.n3 = n3;
				this.n4 = n4;
			}
		}

		@Theory
		public void test(Fixture fx) {

			String ipStr = String.format("%d.%d.%d.%d", fx.n1, fx.n2, fx.n3, fx.n4);
			Ip ip = new Ip(ipStr);
			byte[] ipV4 = ip.getIpV4();
			
			TestUtil.dispPrompt(this);
			System.out.printf("new Ip(\"%s\") ipV4[0]=%d ipV4[1]=%d ipV4[2]=%d ipV4[3]=%d\n", ipStr, ipV4[0], ipV4[1], ipV4[2], ipV4[3]);
			
			assertSame(ipV4[0], (byte) fx.n1);
			assertSame(ipV4[1], (byte) fx.n2);
			assertSame(ipV4[2], (byte) fx.n3);
			assertSame(ipV4[3], (byte) fx.n4);
		}
	}
	
	@RunWith(Theories.class)
	public static class A004 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("getIpV6()テスト");
		}
		@DataPoints
		public static Fixture[] datas = {
			// コンストラクタ文字列,toString()出力
			new Fixture("1234:56::1234:5678:90ab", 0x12, 0x34, 0x00, 0x56, 0, 0, 0, 0, 0, 0, 0x12, 0x34, 0x56, 0x78, 0x90, 0xab),
			new Fixture("1::1", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
			new Fixture("ff04::f234", 0xff, 0xff04, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xf2, 0x34),
			new Fixture("1::1%16", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
			new Fixture("[1::1]", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
			
		};
		static class Fixture {
			private String ipStr;
			private int n1;
			private int n2;
			private int n3;
			private int n4;
			private int n5;
			private int n6;
			private int n7;
			private int n8;
			private int n9;
			private int n10;
			private int n11;
			private int n12;
			private int n13;
			private int n14;
			private int n15;
			private int n16;

			public Fixture(String ipStr, int n1, int n2, int n3, int n4, int n5, int n6, int n7, int n8,
					int n9, int n10, int n11, int n12, int n13, int n14, int n15, int n16) {
				this.ipStr = ipStr;
				this.n1 = n1;
				this.n2 = n2;
				this.n3 = n3;
				this.n4 = n4;
				this.n5 = n5;
				this.n6 = n6;
				this.n7 = n7;
				this.n8 = n8;
				this.n9 = n9;
				this.n10 = n10;
				this.n11 = n11;
				this.n12 = n12;
				this.n13 = n13;
				this.n14 = n14;
				this.n15 = n15;
				this.n16 = n16;
			}
		}

		@Theory
		public void test(Fixture fx) {

			Ip ip = new Ip(fx.ipStr);
			byte[] ipV6 = ip.getIpV6();
			
			TestUtil.dispPrompt(this);
			System.out.printf("new Ip(\"%s\")", fx.ipStr);
			for (int i = 0; i < 16; i++) {
				System.out.printf("%d:", ipV6[i]);
			}
			System.out.printf("\n");

			assertSame(ipV6[0], (byte) fx.n1);
			assertSame(ipV6[1], (byte) fx.n2);
			assertSame(ipV6[2], (byte) fx.n3);
			assertSame(ipV6[3], (byte) fx.n4);
			assertSame(ipV6[4], (byte) fx.n5);
			assertSame(ipV6[5], (byte) fx.n6);
			assertSame(ipV6[6], (byte) fx.n7);
			assertSame(ipV6[7], (byte) fx.n8);
			assertSame(ipV6[8], (byte) fx.n9);
			assertSame(ipV6[9], (byte) fx.n10);
			assertSame(ipV6[10], (byte) fx.n11);
			assertSame(ipV6[11], (byte) fx.n12);
			assertSame(ipV6[12], (byte) fx.n13);
			assertSame(ipV6[13], (byte) fx.n14);
			assertSame(ipV6[14], (byte) fx.n15);
			assertSame(ipV6[15], (byte) fx.n16);
			
		}
	}

	@RunWith(Theories.class)
	public static class A005 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("演算子==の判定（null判定）");
		}
		
		@DataPoints
		public static Fixture[] datas = {
			// IP1.IP2,==の判定
			new Fixture(new Ip("192.168.0.1"), new Ip("192.168.0.1"), true),
			new Fixture(new Ip("192.168.0.1"), new Ip("192.168.0.2"), false),
			new Fixture(new Ip("192.168.0.1"), null, false),
			new Fixture(new Ip("::1"), new Ip("::1"), true), 
			new Fixture(new Ip("::1%1"), new Ip("::1%1"), true), 
			new Fixture(new Ip("::1%1"), new Ip("::1"), false), 
			new Fixture(new Ip("ff01::1"), new Ip("::1"), false),
			new Fixture(new Ip("::1"), null, false),
		};
		static class Fixture {
			private Ip ip0;
			private Ip ip1;
			private boolean expected;

			public Fixture(Ip ip0, Ip ip1, boolean expected) {
				this.ip0 = ip0;
				this.ip1 = ip1;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);

			String ipStr0 = "null";
			if (fx.ip0 != null) {
				ipStr0 = fx.ip0.toString();
			}
			String ipStr1 = "null";
			if (fx.ip1 != null) {
				ipStr1 = fx.ip1.toString();
			}

			System.out.printf("Ip(%s) ip.equals(%s) => %s\n", ipStr0, ipStr1, fx.expected);

			boolean b = fx.ip0.equals(fx.ip1);
			assertSame(fx.ip0.equals(fx.ip1), fx.expected);
			
		}
	}
	
	@RunWith(Theories.class)
	public static class A006 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("getAddrV4()で取得した値からコンストラクタIp(int ip)を使用して再構築する");
		}
		
		@DataPoints
		public static Fixture[] datas = {
			// IP1.IP2,==の判定
			new Fixture("1.2.3.4"),
			new Fixture("192.168.0.1"),
			new Fixture("255.255.255.255"),
			new Fixture("INADDR_ANY"),
		};
		static class Fixture {
			private String ipStr;

			public Fixture(String ipStr) {
				this.ipStr = ipStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);
			
			Ip p1 = new Ip(fx.ipStr);
			int i = p1.getAddrV4();
			Ip p2 = new Ip(i);
			System.out.printf("Ip(%s) => ip.getAddrV4()=0x%x(%d) => new Ip(0x%x) => %s \n", fx.ipStr,i,i,i,p2.toString());

			assertThat(p2.toString(),is(fx.ipStr));
			
		}
	}
	
	@RunWith(Theories.class)
	public static class A007 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("getAddrV6H()とgetAddrV6L()で取得した値からコンストラクタIp(long h,long l)を使用して再構築する");
		}
		
		@DataPoints
		public static Fixture[] datas = {
			// IP1.IP2,==の判定
			new Fixture("102:304:506:708:90a:b0c:d0e:f01"),
			new Fixture("ff83::e:f01"),
			new Fixture("::1"),
			new Fixture("fff::"),
		};
		static class Fixture {
			private String ipStr;

			public Fixture(String ipStr) {
				this.ipStr = ipStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);
			
			Ip p1 = new Ip(fx.ipStr);
			long h = p1.getAddrV6H();
			long l = p1.getAddrV6L();
			Ip p2 = new Ip(h, l);
			System.out.printf("Ip(%s) => ip.getAddrV6H()=0x%x  ip.getAddrV6L()=0x%x => new Ip(0x%x,0x%x) => %s \n", fx.ipStr, h, l, h, l, p2.toString());

			assertThat(p2.toString(), is(fx.ipStr));
			
		}
	}
	
	@RunWith(Theories.class)
	public static class A008 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("文字列によるコンストラクタで例外(IllegalArgumentException)が発生することを確認する");
		}
		
		@DataPoints
		public static Fixture[] datas = {
			//コンストラクタに与える文字列
			new Fixture(""),
			new Fixture("IN_ADDR_ANY"),
			new Fixture("xxx"),
			new Fixture("192.168.0.1.2"),
			new Fixture(null),
			new Fixture("11111::"),
		};
		static class Fixture {
			private String ipStr;

			public Fixture(String ipStr) {
				this.ipStr = ipStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);

			System.out.printf("Ip(%s) => throw IllegalArgumentException\n",fx.ipStr);
			
			try {
				Ip p1 = new Ip(fx.ipStr);
				Assert.fail("この行が実行されたらエラー");
			} catch (IllegalArgumentException ex) {
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}
//IllegalArgumentExceptionが発生することを確認する	
//	new Fixture("", "0.0.0.0"), 
//	new Fixture("", "IN_ADDR_AAA_ANY"), 
	

}
