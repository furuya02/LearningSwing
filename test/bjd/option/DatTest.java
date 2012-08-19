package bjd.option;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ctrl.CtrlType;
import bjd.option.OneDatTest.A001.Fixture;
import bjd.util.TestUtil;

@RunWith(Enclosed.class)
public class DatTest {
	@RunWith(Theories.class)
	public static class A001 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("fromReg()�ŏ���������toReg(false)�Ŏ��o��");
		}

		@DataPoints
		public static Fixture[] datas = {
			new Fixture(2, "#\tn1\tn2\b\tn1\tn2"),		
			new Fixture(1, "\tn1\b\tn1\b#\tn1\b#\tn1"),		
		};

		static class Fixture {
			private int colMax;
			private String str;

			public Fixture(int colMax, String str) {
				this.colMax = colMax;
				this.str = str;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			ArrayList<CtrlType> ctrlTypeList = new ArrayList<CtrlType>();
			for (int i = 0; i < fx.colMax; i++) {
				ctrlTypeList.add(CtrlType.INT);
			}

			System.out.printf("fromReg(\"%s\") => toReg(\"%s\")\n", fx.colMax, fx.str);

			Dat dat = new Dat(ctrlTypeList);
			dat.fromReg(fx.str);
			assertThat(dat.toReg(false), is(fx.str));
		}
	}
	
	@RunWith(Theories.class)
	public static class A002 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("fromReg()�ɖ����ȕ������^�����false���Ԃ�");
		}

		@DataPoints
		public static Fixture[] datas = {
			//new Fixture(3, "#\tn1\tn2\b\tn1\tn2"), //�J�������s��v		
			//new Fixture(1, "#\tn1\b\tn1\tn2"), //�J�������s��v		
			//new Fixture(1, "_\tn1"), //�����f�[�^		
			new Fixture(1, "\b"), //�����f�[�^		
			//new Fixture(1, ""),		
			//new Fixture(1, null),		
		};

		static class Fixture {
			private int colMax;
			private String str;

			public Fixture(int colMax, String str) {
				this.colMax = colMax;
				this.str = str;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			ArrayList<CtrlType> ctrlTypeList = new ArrayList<CtrlType>();
			for (int i = 0; i < fx.colMax; i++) {
				ctrlTypeList.add(CtrlType.INT);
			}

			System.out.printf("colMax=%d fromReg(\"%s\") => false\n", fx.colMax, fx.str);

			Dat dat = new Dat(ctrlTypeList);
			assertSame(dat.fromReg(fx.str), false);
		}
	}

	
//	@Test
//	public void testAdd() {
//		fail("�܂���������Ă��܂���");
//	}
//
//	@Test
//	public void testToReg() {
//		fail("�܂���������Ă��܂���");
//	}
//
//	@Test
//	public void testFromReg() {
//		fail("�܂���������Ă��܂���");
//	}

}
