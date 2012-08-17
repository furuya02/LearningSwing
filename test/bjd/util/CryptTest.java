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
			TestUtil.dispHeader("encrypt�y��decrypt");
		}

		@DataPoints
		public static Fixture[] datas = {
			// ���͕�����
			new Fixture("�{���͐��V�Ȃ�"),
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
			assertThat(expected, is(not("ERROR"))); //ERROR���o�͂��ꂽ�ꍇ�́A�e�X�g���s
			assertThat(expected, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class A002 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("encrypt�̃G���[����");
		}

		@DataPoints
		public static Fixture[] datas = {
			// ���͕�����
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
			assertThat(expected, is("ERROR")); //ERROR���o�͂��ꂽ�ꍇ�́A�e�X�g����
		}
	}

	@RunWith(Theories.class)
	public static class A003 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("decrypt�̃G���[����");
		}

		@DataPoints
		public static Fixture[] datas = {
			// ���͕�����
			new Fixture(null),
			new Fixture(""),
			new Fixture("123"),
			new Fixture("�{���͐��V�Ȃ�"),
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
			assertThat(expected, is("ERROR")); //ERROR���o�͂��ꂽ�ꍇ�́A�e�X�g����
		}
	}

}