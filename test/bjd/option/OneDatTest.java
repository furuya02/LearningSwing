package bjd.option;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertSame;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.util.TestUtil;

@RunWith(Enclosed.class)
public class OneDatTest {

	@RunWith(Theories.class)
	public static class A001 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("�R���X�g���N�^�ŏ���������toReg()�̏o�͂��m�F���� [*]��isSecretList toReg()�̃p�����[�^�� needSecret");
		}

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(true, new String[] { "user1", "pass" }, new boolean[] { false, false }, true, "\tuser1\tpass"),
				new Fixture(true, new String[] { "user1", "pass" }, new boolean[] { true, false }, true, "\t***\tpass"), 
				new Fixture(true, new String[] { "user1", "pass" }, new boolean[] { true, false }, false, "\tuser1\tpass"), 
		};

		static class Fixture {
			private boolean enable;
			private String[] list;
			private boolean[] isSecretList;
			private boolean needSecret;
			private String expected;

			public Fixture(boolean enable, String[] list, boolean[] isSecretList, boolean needSecret, String expected) {

				this.enable = enable;
				this.list = list;
				this.isSecretList = isSecretList;
				this.needSecret = needSecret;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < fx.list.length; i++) {
				if (sb.length() != 0) {
					sb.append(",");
				}
				sb.append(fx.list[i]);
				sb.append(String.format("[%s]", fx.isSecretList[i] ? "*" : ""));
			}
			System.out.printf("new OneDat(%s,{%s}) toReg(%s)=\"%s\"\n", fx.enable, sb.toString(), fx.needSecret, fx.expected);

			OneDat oneDat = new OneDat(fx.enable, fx.list, fx.isSecretList);
			assertThat(oneDat.toReg(fx.needSecret), is(fx.expected));
		}
	}
	
	@RunWith(Theories.class) public static class A002 {
		@BeforeClass public static void before() {
			TestUtil.dispHeader("fromReg�ŏ���������toReg()�ŏo�͂���"); 
		}
		
		@DataPoints public static Fixture[] datas = { 
				new Fixture(2, "\tuser1\tpass", "\tuser1\tpass"),
				new Fixture(2, "#\tuser1\tpass", "#\tuser1\tpass"),
				new Fixture(3, "\tn1\tn2\tn3", "\tn1\tn2\tn3"),
		};
		
		static class Fixture {
			private int max; //�J������ (�R���X�g���N�^�������p)
			private String actual;
			private String expected;

			public Fixture(int max, String actual, String expected) {
				this.max = max;
				this.actual = actual;
				this.expected = expected;
			}
		}
		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			System.out.printf("new OneDat(max=%d) frmoReg(\"%s\") => toReg()=\"%s\"\n", fx.max, fx.actual, fx.expected);

			String [] list = new String[fx.max];
			boolean [] isSecretList = new boolean[fx.max];
			for (int i = 0; i < fx.max; i++) {
				list[i] = "";
				isSecretList[i] = false;
			}
			OneDat oneDat = new OneDat(true, list, isSecretList);
			oneDat.fromReg(fx.actual);
			assertThat(oneDat.toReg(false), is(fx.expected));
		}
	}

	@RunWith(Theories.class) public static class A003 {
		@BeforeClass public static void before() {
			TestUtil.dispHeader("// fromReg�ɖ����ȓ��͂����������Afalse���A��"); 
		}
		
		@DataPoints public static Fixture[] datas = { 
				new Fixture(3, "\tuser1\tpass"), //�J�������F��v
				new Fixture(2, null),
				new Fixture(3, "_\tn1\tn2\tn3"), //����������
				new Fixture(3, ""), //����������
				new Fixture(3, "\t"), //����������
		};
		
		static class Fixture {
			private int max; //�J������ (�R���X�g���N�^�������p)
			private String str;

			public Fixture(int max, String str) {
				this.max = max;
				this.str = str;
			}
		}
		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			System.out.printf("new OneDat(max=%d) frmoReg(\"%s\") = false\n", fx.max, fx.str);

			String [] list = new String[fx.max];
			boolean [] isSecretList = new boolean[fx.max];
			for (int i = 0; i < fx.max; i++) {
				list[i] = "";
				isSecretList[i] = false;
			}
			OneDat oneDat = new OneDat(true, list, isSecretList);
			assertSame(oneDat.fromReg(fx.str), false);
		}
	}

	// �R���X�g���N�^�Œ�`�����J�����^�Ƃ̉F��v��A����������fromReg�ɑ������Ƃ��Afalse���A��
	
}
