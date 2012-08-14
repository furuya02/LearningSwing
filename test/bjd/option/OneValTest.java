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

import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlFile;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlFont;
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
			Util.dispClass("OneValTest-A001", "デフォルト値をtoReg()で取り出す");
		}

		@DataPoints
		public static Fixture[] datas = {
			//コントロールの種類,デフォルト値,toRegの出力
			new Fixture(CtrlType.CHECKBOX, true, "true"),
			new Fixture(CtrlType.CHECKBOX, false, "false"),
			new Fixture(CtrlType.INT, 100, "100"),
			new Fixture(CtrlType.INT, 0, "0"),
			new Fixture(CtrlType.INT, -100, "-100"),
			new Fixture(CtrlType.FILE, "c:\\test.txt", "c:\\test.txt"),
			new Fixture(CtrlType.FOLDER, "c:\\test", "c:\\test"),
			new Fixture(CtrlType.TEXTBOX, "abcdefg１２３", "abcdefg１２３"),
			new Fixture(CtrlType.RADIO, 1, "1"),
			new Fixture(CtrlType.RADIO, 5, "5"),
			new Fixture(CtrlType.FONT, new Font("Times New Roman", Font.ITALIC, 15), "Times New Roman,2,15"),
			new Fixture(CtrlType.FONT, new Font("Serif", Font.BOLD, 8), "Serif,1,8"),
			new Fixture(CtrlType.MEMO, "1\r\n2\r\n3\r\n", "1\t2\t3\t"),
			new Fixture(CtrlType.MEMO, "123", "123"),
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

			Util.dispCtrlType(fx.ctrlType);
			System.out.printf("default値=%s toReg()=\"%s\"\n", fx.actual, fx.expected);

			OneVal oneVal = Util.createOneVal(fx.ctrlType, fx.actual);
			boolean isDebug = false;
			assertThat(oneVal.toReg(isDebug), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static class A002 {

		@BeforeClass
		public static void before() {
			Util.dispClass("OneValTest-A002", "fromReg()で設定した値をtoReg()で取り出す");
		}

		@DataPoints
		public static Fixture[] datas = {
			//コントロールの種類,fromRegで設定してtoRegで取得する文字列
			new Fixture(CtrlType.CHECKBOX, "true"),
			new Fixture(CtrlType.CHECKBOX, "false"),
			new Fixture(CtrlType.INT, "100"),
			new Fixture(CtrlType.INT, "0"),
			new Fixture(CtrlType.FILE, "c:\\test.txt"),
			new Fixture(CtrlType.FOLDER, "c:\\test"),
			new Fixture(CtrlType.TEXTBOX, "abcdefg１２３"),
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

			Util.dispCtrlType(fx.ctrlType);
			System.out.printf("fromReg(\"%s\") toReg()=\"%s\"\n", fx.actual, fx.actual);

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
			Util.dispClass("OneValTest-A003", "fromReg()の不正パラメータ判定");
		}

		@DataPoints
		public static Fixture[] datas = { 
			//コントロールの種類,fromRegに入力する文字列,fromRegの戻り値
			new Fixture(CtrlType.CHECKBOX, "true", true),
			new Fixture(CtrlType.CHECKBOX, "TRUE", true),
			new Fixture(CtrlType.CHECKBOX, "false", true),
			new Fixture(CtrlType.CHECKBOX, "FALSE", true),
			new Fixture(CtrlType.CHECKBOX, "t", false), // 不正入力
			new Fixture(CtrlType.CHECKBOX, "", false), // 不正入力
			new Fixture(CtrlType.INT, "-100", true),
			new Fixture(CtrlType.INT, "0", true),
			new Fixture(CtrlType.INT, "aaa", false), // 不正入力
			new Fixture(CtrlType.FILE, "c:\\test.txt", true), 
			new Fixture(CtrlType.FOLDER, "c:\\test", true), 
			new Fixture(CtrlType.TEXTBOX, "abcdefg１２３", true), 
			new Fixture(CtrlType.RADIO, "0", true), 
			new Fixture(CtrlType.RADIO, "5", true), 
			new Fixture(CtrlType.RADIO, "-1", false), //不正入力 Radioは0以上
			new Fixture(CtrlType.FONT, "Default,-1,1", false), //不正入力(styleが無効値)
			new Fixture(CtrlType.FONT, "Default,2,-1", false), //不正入力(sizeが0以下)
			new Fixture(CtrlType.FONT, "XXX,1,8", true), //　(Font名ではエラーが発生しない)
			new Fixture(CtrlType.FONT, "Serif,1,8", true), //不正入力
			new Fixture(CtrlType.MEMO, null, false), //不正入力
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

			Util.dispCtrlType(fx.ctrlType);
			System.out.printf("fromReg(\"%s\") = %s\n", fx.actual, fx.expected);
			
			OneVal oneVal = Util.createOneVal(fx.ctrlType, null);

			assertSame(oneVal.fromReg(fx.actual), fx.expected);
		}
	}
	/**
	 * 共通的に利用されるメソッド
	 */
	private static class Util {
		/**
		 * OneValの生成
		 * @param val
		 *            デフォルト値(nullを設定した場合、適切な値を自動でセットする)
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
				default:
					// not implement.
					throw new IllegalArgumentException(ctrlType.toString());
			}
			return new OneVal("name", val, Crlf.NEXTLINE, oneCtrl);
		}
		/**
		 * コンソール表示
		 */
		//public static void dispClass(Object cls) {
		//	System.out.printf("%s ", cls.getClass().getName().split("\\$")[1]);
		// }
		public static void dispClass(String className, String msg) {
			System.out.println("\n----------------------------------------------------");
			System.out.printf("[%s] %s\n", className, msg);
			System.out.println("----------------------------------------------------");
		}
		public static void dispCtrlType(CtrlType ctrlType) {
			System.out.printf(" (%s) ", ctrlType);
		}
	}
}
