/**
 * 
 */
package sample;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class OneValTest {

	@RunWith(Theories.class)
	public static class デフォルト値をtoRegで取り出す {

		@DataPoint
		public static Fixture d1 = new Fixture(CtrlType.CHECKBOX, true, "true");
		@DataPoint
		public static Fixture d2 = new Fixture(CtrlType.CHECKBOX, false, "false");
		@DataPoint
		public static Fixture d3 = new Fixture(CtrlType.INT, 100, "100");
		@DataPoint
		public static Fixture d4 = new Fixture(CtrlType.INT, 0, "0");
		@DataPoint
		public static Fixture d5 = new Fixture(CtrlType.INT, -100, "-100");
		@DataPoint
		public static Fixture d6 = new Fixture(CtrlType.FILE,"c:\\test.txt", "c:\\test.txt");
		@DataPoint
		public static Fixture d7 = new Fixture(CtrlType.FOLDER,"c:\\test", "c:\\test");
		@DataPoint
		public static Fixture d8 = new Fixture(CtrlType.TEXTBOX,"abcdefg１２３", "abcdefg１２３");
		
		@Theory
		public void test(Fixture fx) {

			String className = this.getClass().getName().split("\\$")[1];
			System.out.printf("%s [%s] default値=%s toReg()=\"%s\"\n", className, fx.ctrlType, fx.actual, fx.expected);

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
	public static class fromRegで設定した値をtoRegで取り出す {

		@DataPoint
		public static Fixture d1 = new Fixture(CtrlType.CHECKBOX, "true");
		@DataPoint
		public static Fixture d2 = new Fixture(CtrlType.CHECKBOX, "false");
		@DataPoint
		public static Fixture d3 = new Fixture(CtrlType.INT, "100");
		@DataPoint
		public static Fixture d4 = new Fixture(CtrlType.INT, "0");

		@DataPoint
		public static Fixture d5 = new Fixture(CtrlType.FILE,"c:\\test.txt");
		@DataPoint
		public static Fixture d6 = new Fixture(CtrlType.FOLDER,"c:\\test");
		@DataPoint
		public static Fixture d7 = new Fixture(CtrlType.TEXTBOX,"abcdefg１２３");

		@Theory
		public void test(Fixture fx) {

			String className = this.getClass().getName().split("\\$")[1];
			System.out.printf("%s [%s] fromReg(\"%s\") toReg()=\"%s\"\n", className, fx.ctrlType, fx.actual, fx.actual);

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
	public static class fromRegの不正パラメータ判定 {

		@DataPoint
		public static Fixture d1 = new Fixture(CtrlType.CHECKBOX, "true", true);
		@DataPoint
		public static Fixture d2 = new Fixture(CtrlType.CHECKBOX, "TRUE", true);
		@DataPoint
		public static Fixture d3 = new Fixture(CtrlType.CHECKBOX, "false", true);
		@DataPoint
		public static Fixture d4 = new Fixture(CtrlType.CHECKBOX, "FALSE", true);
		@DataPoint
		public static Fixture d5 = new Fixture(CtrlType.CHECKBOX, "t", false); // 不正入力
		@DataPoint
		public static Fixture d6 = new Fixture(CtrlType.CHECKBOX, "", false); // 不正入力
		@DataPoint
		public static Fixture d7 = new Fixture(CtrlType.INT, "100", true);
		@DataPoint
		public static Fixture d8 = new Fixture(CtrlType.INT, "-100", true);
		@DataPoint
		public static Fixture d9 = new Fixture(CtrlType.INT, "0", true);
		@DataPoint
		public static Fixture d10 = new Fixture(CtrlType.INT, "aaa", false); // 不正入力
		@DataPoint
		public static Fixture d11 = new Fixture(CtrlType.FILE,"c:\\test.txt",true);
		@DataPoint
		public static Fixture d12 = new Fixture(CtrlType.FOLDER,"c:\\test", true);
		@DataPoint
		public static Fixture d13 = new Fixture(CtrlType.TEXTBOX,"abcdefg１２３", true);

		@Theory
		public void test(Fixture fx) {

			String className = this.getClass().getName().split("\\$")[1];
			System.out.printf("%s [%s] fromReg(\"%s\") = %s\n", className, fx.ctrlType, fx.actual, fx.expected);

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

	private static class Util {
		/**
		 * OneValを生成する（共通利用）
		 *
		 * @param val
		 *     デフォルト値(nullを設定した場合、適切な値を自動でセットする)
		 */
		public static OneVal createOneVal(CtrlType ctrlType, Object val) {
			final String help = "help";
			final String name = "name";
			switch (ctrlType) {
				case CHECKBOX:
					return new OneVal(name, (val != null) ? val : true    , Crlf.NEXTLINE, new CtrlCheckBox(help));
				case INT:
					return new OneVal(name, (val != null) ? val : 1       , Crlf.NEXTLINE, new CtrlInt(help));
				case FILE:
					return new OneVal(name, (val != null) ? val : "1.txt" , Crlf.NEXTLINE, new CtrlFile(help));
				case FOLDER:
					return new OneVal(name, (val != null) ? val : "c:\tmp", Crlf.NEXTLINE, new CtrlFolder(help));
				case TEXTBOX:
					return new OneVal(name, (val != null) ? val : "abc"   , Crlf.NEXTLINE, new CtrlTextBox(help));
				default:
					// not implement.
					throw new IllegalArgumentException(ctrlType.toString());
			}
		}
	}
}
