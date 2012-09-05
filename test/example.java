//package bjd.option;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.*;
//
//
//@RunWith(Enclosed.class)
//public class ListValTest {

//	@RunWith(Theories.class)
//	public static class A001 {
//		@BeforeClass
//		public static void before() {
//			TestUtil.dispHeader("デフォルト値をtoReg()で取り出す");
//		}
//
//		@DataPoints
//		public static Fixture[] datas = {
//				//コントロールの種類,デフォルト値,toRegの出力
//				new Fixture(CtrlType.CHECKBOX, true, "true"), new Fixture(CtrlType.CHECKBOX, false, "false"), new Fixture(CtrlType.INT, 100, "100"),
//		};
//		static class Fixture {
//			private CtrlType ctrlType;
//			private Object actual;
//			private String expected;
//
//			public Fixture(CtrlType ctrlType, Object actual, String expected) {
//				this.ctrlType = ctrlType;
//				this.actual = actual;
//				this.expected = expected;
//			}
//		}
//
//		@Theory
//		public void test(Fixture fx) {
//
//			TestUtil.dispPrompt(this);
//			System.out.printf("(%s) default値=%s toReg()=\"%s\"\n", fx.ctrlType, fx.actual, fx.expected);
//
//			OneVal oneVal = Util.createOneVal(fx.ctrlType, fx.actual);
//			boolean isDebug = false;
//			assertThat(oneVal.toReg(isDebug), is(fx.expected));
//		}
//	}

	//TestUtil.dispHeader("TODO TEST example");
	//fail("まだ実装されていません");
//}

