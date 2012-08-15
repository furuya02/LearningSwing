/**
 * 
 */
package bjd.option;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertSame;
import static org.hamcrest.CoreMatchers.is;

import java.awt.Font;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.TestUtil;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlFile;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlFont;
import bjd.ctrl.CtrlHidden;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlMemo;
import bjd.ctrl.CtrlRadio;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.CtrlType;
import bjd.ctrl.OneCtrl;

@RunWith(Enclosed.class)
public class OneValTest {

	@RunWith(Theories.class)
	public static class A001 {
		
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("�f�t�H���g�l��toReg()�Ŏ��o��");
		}

		@DataPoints
		public static Fixture[] datas = {
			//�R���g���[���̎��,�f�t�H���g�l,toReg�̏o��
			new Fixture(CtrlType.CHECKBOX, true, "true"),
			new Fixture(CtrlType.CHECKBOX, false, "false"),
			new Fixture(CtrlType.INT, 100, "100"),
			new Fixture(CtrlType.INT, 0, "0"),
			new Fixture(CtrlType.INT, -100, "-100"),
			new Fixture(CtrlType.FILE, "c:\\test.txt", "c:\\test.txt"),
			new Fixture(CtrlType.FOLDER, "c:\\test", "c:\\test"),
			new Fixture(CtrlType.TEXTBOX, "abcdefg�P�Q�R", "abcdefg�P�Q�R"),
			new Fixture(CtrlType.RADIO, 1, "1"),
			new Fixture(CtrlType.RADIO, 5, "5"),
			new Fixture(CtrlType.FONT, new Font("Times New Roman", Font.ITALIC, 15), "Times New Roman,2,15"),
			new Fixture(CtrlType.FONT, new Font("Serif", Font.BOLD, 8), "Serif,1,8"),
			new Fixture(CtrlType.MEMO, "1\r\n2\r\n3\r\n", "1\t2\t3\t"),
			new Fixture(CtrlType.MEMO, "123", "123"),
			new Fixture(CtrlType.HIDDEN, null, "60392a0d922b9077"),//���̑���A004�Ńe�X�g����
		};
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

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("(%s) default�l=%s toReg()=\"%s\"\n", fx.ctrlType, fx.actual, fx.expected);

			OneVal oneVal = Util.createOneVal(fx.ctrlType, fx.actual);
			boolean isDebug = false;
			assertThat(oneVal.toReg(isDebug), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static class A002 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("fromReg()�Őݒ肵���l��toReg()�Ŏ��o��");
		}

		@DataPoints
		public static Fixture[] datas = {
			//�R���g���[���̎��,fromReg�Őݒ肵��toReg�Ŏ擾���镶����
			new Fixture(CtrlType.CHECKBOX, "true"),
			new Fixture(CtrlType.CHECKBOX, "false"),
			new Fixture(CtrlType.INT, "100"),
			new Fixture(CtrlType.INT, "0"),
			new Fixture(CtrlType.FILE, "c:\\test.txt"),
			new Fixture(CtrlType.FOLDER, "c:\\test"),
			new Fixture(CtrlType.TEXTBOX, "abcdefg�P�Q�R"),
			new Fixture(CtrlType.RADIO, "1"),
			new Fixture(CtrlType.RADIO, "0"),
			new Fixture(CtrlType.FONT, "Times New Roman,2,15"),
			new Fixture(CtrlType.FONT, "Serif,1,8"),
			new Fixture(CtrlType.MEMO, "1\t2\t3\t"),
		};
		static class Fixture {
			private CtrlType ctrlType;
			private String actual;
			public Fixture(CtrlType ctrlType, String actual) {
				this.ctrlType = ctrlType;
				this.actual = actual;
			}
		}
		
		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);
			System.out.printf("(%s) fromReg(\"%s\") toReg()=\"%s\"\n", fx.ctrlType, fx.actual, fx.actual);

			OneVal oneVal = Util.createOneVal(fx.ctrlType, null);

			boolean isDebug = false;
			oneVal.fromReg(fx.actual);
			assertThat(oneVal.toReg(isDebug), is(fx.actual));
		}
	}

	@RunWith(Theories.class)
	public static class A003 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("fromReg()�̕s���p�����[�^����");
		}

		@DataPoints
		public static Fixture[] datas = { 
			//�R���g���[���̎��,fromReg�ɓ��͂��镶����,fromReg�̖߂�l
			new Fixture(CtrlType.CHECKBOX, "true", true),
			new Fixture(CtrlType.CHECKBOX, "TRUE", true),
			new Fixture(CtrlType.CHECKBOX, "false", true),
			new Fixture(CtrlType.CHECKBOX, "FALSE", true),
			new Fixture(CtrlType.CHECKBOX, "t", false), // �s������
			new Fixture(CtrlType.CHECKBOX, "", false), // �s������
			new Fixture(CtrlType.INT, "-100", true),
			new Fixture(CtrlType.INT, "0", true),
			new Fixture(CtrlType.INT, "aaa", false), // �s������
			new Fixture(CtrlType.FILE, "c:\\test.txt", true), 
			new Fixture(CtrlType.FOLDER, "c:\\test", true), 
			new Fixture(CtrlType.TEXTBOX, "abcdefg�P�Q�R", true), 
			new Fixture(CtrlType.RADIO, "0", true), 
			new Fixture(CtrlType.RADIO, "5", true), 
			new Fixture(CtrlType.RADIO, "-1", false), //�s������ Radio��0�ȏ�
			new Fixture(CtrlType.FONT, "Default,-1,1", false), //�s������(style�������l)
			new Fixture(CtrlType.FONT, "Default,2,-1", false), //�s������(size��0�ȉ�)
			new Fixture(CtrlType.FONT, "XXX,1,8", true), //�@(Font���ł̓G���[���������Ȃ�)
			new Fixture(CtrlType.FONT, "Serif,1,8", true), //�s������
			new Fixture(CtrlType.MEMO, null, false), //�s������
			//new Fixture(CtrlType.HIDDEN, null, false), //�s������
		};
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
	
		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("(%s) fromReg(\"%s\") = %s\n", fx.ctrlType, fx.actual, fx.expected);
			
			OneVal oneVal = Util.createOneVal(fx.ctrlType, null);

			assertSame(oneVal.fromReg(fx.actual), fx.expected);
		}
	}
	
	@RunWith(Theories.class)
	public static class A004 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("isDebug���g�p����toReg()�o��");
		}

		@DataPoints
		public static Fixture[] datas = { 
				// �R���g���[���̎��,isDebug,fromReg�ɓ��͂��镶����,toReg�̏o��
				new Fixture(CtrlType.HIDDEN, true, "123", "***"),
				new Fixture(CtrlType.HIDDEN, false, "123", "2d7ee3636680c1f6"),
				new Fixture(CtrlType.HIDDEN, false, "", "60392a0d922b9077"),
				new Fixture(CtrlType.HIDDEN, false, "�{���͐��V�Ȃ�", "503c983b94f87e6a9295796bb439a054"), 
		};
		
		static class Fixture {
			private CtrlType ctrlType;
			private boolean isDebug;
			private String actual;
			private String expected;
			public Fixture(CtrlType ctrlType, boolean isDebug,String actual, String expected) {
				this.ctrlType = ctrlType;
				this.isDebug = isDebug;
				this.actual = actual;
				this.expected = expected;
			}
		}
	
		@Theory
		public void test(Fixture fx) {
			
			TestUtil.dispPrompt(this);
			System.out.printf("(%s) Default=\"%s\" toReg(%s) = %s\n", fx.ctrlType, fx.actual, fx.isDebug, fx.expected);

			OneVal oneVal = Util.createOneVal(fx.ctrlType, fx.actual);
			String s = oneVal.toReg(fx.isDebug);

			assertThat(oneVal.toReg(fx.isDebug), is(fx.expected));
		}
	}
	/**
	 * ���ʓI�ɗ��p����郁�\�b�h
	 */
	private static class Util {
		/**
		 * OneVal�̐���
		 * @param val
		 *            �f�t�H���g�l(null��ݒ肵���ꍇ�A�K�؂Ȓl�������ŃZ�b�g����)
		 */
		public static OneVal createOneVal(CtrlType ctrlType, Object val) {
			final String help = "help";
			OneCtrl oneCtrl = null;
			switch (ctrlType) {
				case CHECKBOX:
					if (val == null) {
						val = true;
					}
					oneCtrl = new CtrlCheckBox(help);
					break;
				case INT:
					if (val == null) {
						val = 1;
					}
					oneCtrl = new CtrlInt(help);
					break;
				case FILE:
					if (val == null) {
						val = "1.txt";
					}
					oneCtrl = new CtrlFile(help);
					break;
				case FOLDER:
					if (val == null) {
						val = "c:\temp";
					}
					oneCtrl = new CtrlFolder(help);
					break;
				case TEXTBOX:
					if (val == null) {
						val = "abc";
					}
					oneCtrl = new CtrlTextBox(help);
					break;
				case RADIO:
					if (val == null) {
						val = 0;
					}
					oneCtrl = new CtrlRadio(help);
					break;
				case FONT:
					if (val == null) {
						val = new Font("Default", Font.PLAIN, 9);
					}
					oneCtrl = new CtrlFont(help);
					break;
				case MEMO:
					if (val == null) {
						val = "1";
					}
					oneCtrl = new CtrlMemo(help);
					break;
				case HIDDEN:
					if (val == null) {
						val = "";
					}
					oneCtrl = new CtrlHidden(help);
					break;
				default:
					// not implement.
					throw new IllegalArgumentException(ctrlType.toString());
			}
			return new OneVal("name", val, Crlf.NEXTLINE, oneCtrl);
		}
	
	}
}
