/**
 * 
 */
package bjd.option;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlFile;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.CtrlType;

@RunWith(Enclosed.class)
public class OneValTest {

	@RunWith(Theories.class)
	public static class �f�t�H���g�l��toReg�Ŏ��o�� {

		@DataPoints
		public static Fixture[] DATAS = {
			new Fixture(CtrlType.CHECKBOX, true, "true"),
			new Fixture(CtrlType.CHECKBOX, false, "false"),
			new Fixture(CtrlType.INT, 100, "100"),
			new Fixture(CtrlType.INT, 0, "0"),
			new Fixture(CtrlType.INT, -100, "-100"),
			new Fixture(CtrlType.FILE, "c:\\test.txt", "c:\\test.txt"),
			new Fixture(CtrlType.FOLDER, "c:\\test", "c:\\test"),
			new Fixture(CtrlType.TEXTBOX, "abcdefg�P�Q�R", "abcdefg�P�Q�R"),
		};

		@Theory
		public void test(Fixture fx) {

			Util.dispClass(this);
			Util.dispCtrlType(fx.ctrlType);
			System.out.printf("default�l=%s toReg()=\"%s\"\n", fx.actual, fx.expected);

			OneVal oneVal = Util.createOneVal(fx.ctrlType, fx.actual);

			boolean isDebug = false;
			assertThat(oneVal.toReg(isDebug), is(fx.expected));
		}

		static class Fixture {
			private CtrlType ctrlType;
			private Object actual;
			private String expected;

			public Fixture(CtrlType ctrlType, Object actual, String expected) {
				this.ctrlType = ctrlType;
				this.actual = actual;
				this.expected = expected;
			}
		}
	}

	@RunWith(Theories.class)
	public static class fromReg�Őݒ肵���l��toReg�Ŏ��o�� {

		@DataPoints
		public static Fixture[] DATAS = {
			new Fixture(CtrlType.CHECKBOX, "true"),
			new Fixture(CtrlType.CHECKBOX, "false"),
			new Fixture(CtrlType.INT, "100"),
			new Fixture(CtrlType.INT, "0"),
			new Fixture(CtrlType.FILE, "c:\\test.txt"),
			new Fixture(CtrlType.FOLDER, "c:\\test"),
			new Fixture(CtrlType.TEXTBOX, "abcdefg�P�Q�R"),
		};
		
		@Theory
		public void test(Fixture fx) {

			Util.dispClass(this);
			Util.dispCtrlType(fx.ctrlType);
			System.out.printf("fromReg(\"%s\") toReg()=\"%s\"\n", fx.actual, fx.actual);

			OneVal oneVal = Util.createOneVal(fx.ctrlType, null);

			boolean isDebug = false;
			oneVal.fromReg(fx.actual);
			assertThat(oneVal.toReg(isDebug), is(fx.actual));
		}

		static class Fixture {
			private CtrlType ctrlType;
			private String actual;

			public Fixture(CtrlType ctrlType, String actual) {
				this.ctrlType = ctrlType;
				this.actual = actual;
			}
		}
	}

	@RunWith(Theories.class)
	public static class fromReg�̕s���p�����[�^���� {

		@DataPoints
		public static Fixture[] DATAS = { 
			new Fixture(CtrlType.CHECKBOX, "true", true),
			new Fixture(CtrlType.CHECKBOX, "TRUE", true),
			new Fixture(CtrlType.CHECKBOX, "false", true),
			new Fixture(CtrlType.CHECKBOX, "FALSE", true),
			new Fixture(CtrlType.CHECKBOX, "t", false), // �s������
			new Fixture(CtrlType.CHECKBOX, "", false), // �s������
			new Fixture(CtrlType.INT, "100", true),
			new Fixture(CtrlType.INT, "-100", true),
			new Fixture(CtrlType.INT, "0", true),
			new Fixture(CtrlType.INT, "aaa", false), // �s������
			new Fixture(CtrlType.FILE, "c:\\test.txt", true), 
			new Fixture(CtrlType.FOLDER, "c:\\test", true), 
			new Fixture(CtrlType.TEXTBOX, "abcdefg�P�Q�R", true), 
		};

		@Theory
		public void test(Fixture fx) {

			Util.dispClass(this);
			Util.dispCtrlType(fx.ctrlType);
			System.out.printf("fromReg(\"%s\") = %s\n", fx.actual, fx.expected);

			OneVal oneVal = Util.createOneVal(fx.ctrlType, null);

			assertSame(oneVal.fromReg(fx.actual), fx.expected);
		}

		static class Fixture {
			private CtrlType ctrlType;
			private String actual;
			private boolean expected;

			public Fixture(CtrlType ctrlType, String actual, boolean expected) {
				this.ctrlType = ctrlType;
				this.actual = actual;
				this.expected = expected;
			}
		}
	}
	/**
	 * ���ʓI�ɗ��p����郁�\�b�h
	 */
	private static class Util {
		/**
		 * OneVal�𐶐�����
		 * @param val
		 *            �f�t�H���g�l(null��ݒ肵���ꍇ�A�K�؂Ȓl�������ŃZ�b�g����)
		 */
		public static OneVal createOneVal(CtrlType ctrlType, Object val) {
			final String help = "help";
			final String name = "name";
			switch (ctrlType) {
				case CHECKBOX:
					return new OneVal(name, (val != null) ? val : true, Crlf.NEXTLINE, new CtrlCheckBox(help));
				case INT:
					return new OneVal(name, (val != null) ? val : 1, Crlf.NEXTLINE, new CtrlInt(help));
				case FILE:
					return new OneVal(name, (val != null) ? val : "1.txt", Crlf.NEXTLINE, new CtrlFile(help));
				case FOLDER:
					return new OneVal(name, (val != null) ? val : "c:\tmp", Crlf.NEXTLINE, new CtrlFolder(help));
				case TEXTBOX:
					return new OneVal(name, (val != null) ? val : "abc", Crlf.NEXTLINE, new CtrlTextBox(help));
				default:
					// not implement.
					throw new IllegalArgumentException(ctrlType.toString());
			}
		}
		/**
		 * �R���\�[���\��
		 */
		public static void dispClass(Object cls) {
			System.out.printf("%s ", cls.getClass().getName().split("\\$")[1]);
		}
		public static void dispCtrlType(CtrlType ctrlType) {
			System.out.printf("(%s) ", ctrlType);
		}
	}
}
