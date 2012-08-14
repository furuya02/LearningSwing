package bjd;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class Base64Test {

	@RunWith(Theories.class)
	public static class A001 {

		@BeforeClass
		public static void before() {
			Util.dispClass("A001", "Base64�̃G���R�[�h�y�уf�R�[�h");
		}

		@DataPoints
		public static Fixture[] datas = {
			// ���͕�����,�o�͕�����
			new Fixture("�{���͐��V�Ȃ�", "�{���͐��V�Ȃ�"),
			new Fixture("123", "123"),
			new Fixture("", ""),
			new Fixture(null, ""),
			new Fixture("1\r\n2", "1\r\n2"),
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
		public void test(Fixture fx) throws MessagingException, IOException {
			String s = Base64.encode(fx.actual);
			String expected = Base64.decode(s);
			System.out.printf("encode(%s)=%s  decode(%s)=%s\n", fx.actual, s, s, expected);
			assertThat(expected, is(fx.expected));
		}
	}

	/**
	 * ���ʓI�ɗ��p����郁�\�b�h
	 */
	private static class Util {
		public static void dispClass(String className, String msg) {
			System.out.println("\n----------------------------------------------------");
			System.out.printf("[%s] %s\n", className, msg);
			System.out.println("----------------------------------------------------");
		}
	}

}
