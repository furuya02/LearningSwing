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


/**
 * @author ws09
 * 
 */
@RunWith(Enclosed.class)
public class OneValTest {

	@RunWith(Theories.class)
	public static class �f�t�H���g�l�Őݒ肵���l���������擾�ł��Ă��邩 {

		@DataPoint
		public static Fixture d1 = new Fixture(CtrlType.CHECKBOX, true, "true");
		@DataPoint
		public static Fixture d2 = new Fixture(CtrlType.CHECKBOX, false, "false");

		// @DataPoint
		// public static Fixture d3 = new Fixture(CtrlType.INT, 100, "100");

		@Theory
		public void TestFunc(Fixture fx) {
			OneVal oneVal = null;
			switch (fx.ctrlType) {
			case CHECKBOX:
				oneVal = new OneVal("testName", fx.defaultValue, Crlf.NEXTLINE,
						new CtrlCheckBox("help"));
				break;
			default:
				assertThat(fx.ctrlType.toString(), is("not implement"));
			}
			boolean isDebug = false;
			// String expected = fx.expected;
			// String actual = oneVal.toReg(isDebug);
			// assertThat(actual, is(expected));
			assertThat(oneVal.toReg(isDebug), is(fx.expected));

			System.out.println("�f�t�H���g�l�Őݒ肵���l���������擾�ł��Ă��邩:" + fx);
		}

		static class Fixture {
			private Object defaultValue;
			private String expected;
			private CtrlType ctrlType;

			public Fixture(CtrlType ctrlType, Object defaultValue,
					String expected) {
				this.ctrlType = ctrlType;
				this.defaultValue = defaultValue;
				this.expected = expected;
			}

			public String toString() {
				return ctrlType + " " + defaultValue + " " + expected;
			}
		}
	}

	@RunWith(Theories.class)
	public static class fromReg�Ő��ۂ̔��f�͐������� {

		@DataPoint
		public static Fixture d1 = new Fixture(CtrlType.CHECKBOX, "true",
				Status.SUCCESS);
		@DataPoint
		public static Fixture d2 = new Fixture(CtrlType.CHECKBOX, "TRUE",
				Status.SUCCESS);
		@DataPoint
		public static Fixture d3 = new Fixture(CtrlType.CHECKBOX, "false",
				Status.SUCCESS);
		@DataPoint
		public static Fixture d4 = new Fixture(CtrlType.CHECKBOX, "FALSE",
				Status.SUCCESS);
		@DataPoint
		public static Fixture d5 = new Fixture(CtrlType.CHECKBOX, "t",
				Status.ERROR);
		@DataPoint
		public static Fixture d6 = new Fixture(CtrlType.CHECKBOX, "",
				Status.ERROR);

		@Theory
		public void TestFunc(Fixture fx) {
			OneVal oneVal = null;
			switch (fx.ctrlType) {
			case CHECKBOX:
				oneVal = new OneVal("testName", true, Crlf.NEXTLINE,
						new CtrlCheckBox("help"));
				break;
			default:
				assertThat(fx.ctrlType.toString(), is("not implement"));
			}
			if (fx.expected == Status.SUCCESS) {
				assertTrue(oneVal.fromReg(fx.actual));
			} else {
				assertFalse(oneVal.fromReg(fx.actual));

			}
			System.out.println("fromReg()�Ő��ۂ̔��f�͐�������:" + fx);
		}

		enum Status {
			SUCCESS, ERROR
		}

		static class Fixture {
			private CtrlType ctrlType;
			private String actual;
			private Status expected;
			public Fixture(CtrlType ctrlType, String actual, Status expected) {
				this.ctrlType = ctrlType;
				this.actual = actual;
				this.expected = expected;
			}
			public String toString() {
				return ctrlType + " " + actual + " " + expected;
			}
		}
	}

	// public static class Next {
	//
	// @Before
	// public void setUp() throws Exception {
	// System.out.println("setUp");
	// }
	//
	// @After
	// public void tearDown() throws Exception {
	// System.out.println("tearDown");
	// }
	//
	// @Test
	// public void testToRegCheckBox() {
	// System.out.println("��testToRegCheckBox");
	// OneVal oneVal = new OneVal("testName", true, Crlf.NEXTLINE,
	// new CtrlCheckBox("help"));
	// boolean isDebug = false;
	// // �f�t�H���g�l�@�m�F "true"
	// assertEquals("true", oneVal.toReg(isDebug));
	// // �Z�b�g�����l���m�F
	// oneVal.fromReg("false");
	// assertEquals(false, oneVal.getValue());
	// // �l�݂̐ݒ�̎��s
	// assertEquals(false, oneVal.fromReg(""));
	// assertEquals(false, oneVal.fromReg("ccc"));
	// assertEquals(false, oneVal.fromReg("F"));
	// // �l�݂̐ݒ�̐���
	// assertEquals(true, oneVal.fromReg("true"));
	// assertEquals(true, oneVal.fromReg("false"));
	// assertEquals(true, oneVal.fromReg("TRUE"));
	// assertEquals(true, oneVal.fromReg("FALSE"));
	//
	// System.out.println("END");
	//
	// }
	//
	// /**
	// * {@link sample.OneVal#toReg(boolean)} �̂��߂̃e�X�g�E���\�b�h�B
	// */
	// @Test
	// public void testToReg() {
	// System.out.println("testToReg");
	// // assertEquals(true, false);
	// }
	//
	// /**
	// * {@link sample.OneVal#fromReg(java.lang.String)} �̂��߂̃e�X�g�E���\�b�h�B
	// */
	// @Test
	// public void testFromReg() {
	// System.out.println("testFromReg");
	// assertEquals(true, true);
	// }
	// }

}
