package bjd.util;

import junit.framework.Assert;
import org.junit.Test;


public class ListBaseTest {

	//ListBase���p��������N���X���`
	class OneClass implements IDispose {
		private String s;
		public OneClass(String s) {
			this.s = s;
		}
		public String getS() {
			return s;
		}
		@Override
		public void dispose() {
		}
	}
	class TestClass extends ListBase<OneClass> {
		public void add(OneClass o) {
			ar.add(o);
		}
	}
		
	@Test
	public void a001() {
		System.out.println("a001 3�v�f�ǉ�����size()���m�F����");
			
		TestClass ts = new TestClass();
	
		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		Assert.assertEquals(ts.size(), 3);
	}

	@Test
	public void a002() {
		System.out.println("a002 �g��for���[�v�ŗv�f�����o��");

		TestClass ts = new TestClass();

		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		StringBuilder sb = new StringBuilder();
		for (OneClass o : ts) {
			sb.append(o.getS());
		}
		Assert.assertEquals(sb.toString(), "123");
	}
	
	@Test
	public void a003() {
		System.out.println("a003 3�v�f�ǉ�����remobve()�̌�A�v�f���m�F����");

		TestClass ts = new TestClass();

		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));
		ts.remove(0);
		StringBuilder sb = new StringBuilder();
		for (OneClass o : ts) {
			sb.append(o.getS());
		}
		Assert.assertEquals(sb.toString(), "23");
	}
	
	@Test
	public void a004() {
		System.out.println("a004 3�v�f�ǉ�����while()�ŉ񂵂Ă݂�");

		TestClass ts = new TestClass();

		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		StringBuilder sb = new StringBuilder();
		while (ts.hasNext()) {
			sb.append(ts.next().getS());
		}
		Assert.assertEquals(sb.toString(), "123");
	}
}