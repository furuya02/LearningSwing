package bjd.util;

import junit.framework.Assert;
import org.junit.Test;


public class ListBaseTest {

	//ListBaseを継承すｔるクラスを定義
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
		System.out.println("a001 3要素追加してsize()を確認する");
			
		TestClass ts = new TestClass();
	
		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		Assert.assertEquals(ts.size(), 3);
	}

	@Test
	public void a002() {
		System.out.println("a002 拡張forループで要素を取り出す");

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
		System.out.println("a003 3要素追加してremobve()の後、要素を確認する");

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
		System.out.println("a004 3要素追加してwhile()で回してみる");

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