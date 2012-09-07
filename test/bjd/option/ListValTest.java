package bjd.option;

import java.util.ArrayList;

import junit.framework.Assert;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import bjd.ctrl.CtrlInt;
import bjd.util.TestUtil;

//@RunWith(Enclosed.class)
public class ListValTest {
    
    @Test
    public void A001() {
        TestUtil.dispHeader("A001 複数のOneValをadd()で追加した後、getList（)で取得した値が正しいかをテストする");

        ListVal listVal = new ListVal();
        listVal.add(new OneVal("n1",1,Crlf.NEXTLINE, new CtrlInt("help",10)));
        listVal.add(new OneVal("n2",1,Crlf.NEXTLINE, new CtrlInt("help",10)));
        
        ArrayList<OneVal> list = listVal.getList(null);
        StringBuilder sb = new StringBuilder();
        for(OneVal o : list){
            sb.append(o.getName());
            sb.append(",");
        }
        String expected = "n1,n2,";

        System.out.printf("listVal.getList(null)=%s expected=%s\n",sb.toString(),expected);
        assertThat(sb.toString(),is(expected));
    }

    

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
	
	//TestUtil.dispHeader("TODO TEST add（）でOneValを追加して、getList()一覧で列挙できるかどうかのテスト（複数のパターンでTESTが必要）");
	//TestUtil.dispHeader("TODO TEST add（）でOneValを追加して、searchで検索できるかどうかのテスト（複数のパターンでTESTが必要）");
	
	//fail("まだ実装されていません");
}

