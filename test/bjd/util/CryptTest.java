package bjd.util;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;


import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.util.Crypt;

@RunWith(Enclosed.class)
public class CryptTest {

	@RunWith(Theories.class)
	public static class A001 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("encrypt及びdecrypt");
		}

		@DataPoints
		public static Fixture[] datas = {
			// 入力文字列
			new Fixture("本日は晴天なり"),
			new Fixture("123"),
			new Fixture(""),
			new Fixture("1\r\n2"),
		};

		static class Fixture {
			private String actual;

			public Fixture(String actual) {
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) throws MessagingException, IOException {
			TestUtil.dispPrompt(this);
			
			String s = Crypt.encrypt(fx.actual);
			String expected = Crypt.decrypt(s);
			System.out.printf("encrypt(%s)=%s  decrypt(%s)=%s\n", fx.actual, s, s, expected);
			assertThat(expected, is(not("ERROR"))); //ERRORが出力された場合は、テスト失敗
			assertThat(expected, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class A002 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("encryptのエラー発生");
		}

		@DataPoints
		public static Fixture[] datas = {
			// 入力文字列
			new Fixture(null),
		};

		static class Fixture {
			private String actual;

			public Fixture(String actual) {
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) throws MessagingException, IOException {
			TestUtil.dispPrompt(this);
			
			String expected = Crypt.encrypt(fx.actual);
			System.out.printf("encrypt(%s)=%s\n", fx.actual, expected);
			assertThat(expected, is("ERROR")); //ERRORが出力された場合は、テスト成功
		}
	}

	@RunWith(Theories.class)
	public static class A003 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("decryptのエラー発生");
		}

		@DataPoints
		public static Fixture[] datas = {
			// 入力文字列
			new Fixture(null),
			new Fixture(""),
			new Fixture("123"),
			new Fixture("本日は晴天なり"),
		};

		static class Fixture {
			private String actual;

			public Fixture(String actual) {
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) throws MessagingException, IOException {
			TestUtil.dispPrompt(this);
			
			String expected = Crypt.decrypt(fx.actual);
			System.out.printf("encrypt(%s)=%s\n", fx.actual, expected);
			assertThat(expected, is("ERROR")); //ERRORが出力された場合は、テスト成功
		}
	}

}