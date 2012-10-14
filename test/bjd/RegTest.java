package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import bjd.net.Ip;
import bjd.util.TestUtil;

public final class RegTest {

	// 前処理
	private File before(String fileName) {
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		File file = new File(String.format("%s\\%s.regtest", currentDir, fileName));
		if (file.exists()) {
			file.delete();
		}
		Reg reg = new Reg(file.getPath());
		TestUtil.dispPrompt(this, String.format("reg=new Reg(%s) => setInt(key1,1) => setString(key2,\"2\") => dispose()", fileName));
		reg.setInt("key1", 1);
		reg.setString("key2", "2");
		reg.dispose();
		return file;
	}

	// 後処理
	private void after(File file) {
		file.delete();
	}

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 設定されている値を読み出す　getInt()");

		File file = before("a001"); //前処理

		Reg reg = new Reg(file.getPath());
		String key = "key1";
		int actual = reg.getInt(key);
		TestUtil.dispPrompt(this, String.format("reg.getInt(\"%s\")= %d", key, actual));
		assertThat(actual, is(1));
		reg.dispose();

		after(file); //後始末
	}


	@Test
	public void a002() {

		TestUtil.dispHeader("a002 設定されている値を読み出す　getString()");

		File file = before("a002"); //前処理
		
		Reg reg = new Reg(file.getPath());
		String key = "key2";
		String actual = reg.getString(key);
		TestUtil.dispPrompt(this, String.format("reg.getString(\"%s\")= \"%s\"", key, actual));
		assertThat(actual, is("2"));
		reg.dispose();

		after(file); //後始末
	}


	@Test
	public void a003() {

		TestUtil.dispHeader("a003 保存されていないKeyで読み出すと例外が発生する　getInt()");

		File file = before("a003"); //前処理
		
		Reg reg = new Reg(file.getPath());
		String key = "xxx";
		
		TestUtil.dispPrompt(this, String.format("reg.getInt(\"%s\") => IllegalArgumentException", key));
		try {
			@SuppressWarnings("unused")
			int actual = reg.getInt(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (IllegalArgumentException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");
	}


	@Test
	public void a004() {

		TestUtil.dispHeader("a004 保存されていないKeyで読み出すと 例外が発生する　getString()");

		File file = before("a004"); //前処理
		
		Reg reg = new Reg(file.getPath());
		String key = "xxx";
		
		TestUtil.dispPrompt(this, String.format("reg.getString(\"%s\")  => IllegalArgumentException", key));
		try {
			@SuppressWarnings("unused")
			String actual = reg.getString(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (IllegalArgumentException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");
		

	}
	
	@Test
	public void a005() {

		TestUtil.dispHeader("a005 Key=null で読み出すと例外がが発生する　getInt()");

		File file = before("a005"); //前処理
		
		Reg reg = new Reg(file.getPath());
		String key = null;
		
		TestUtil.dispPrompt(this, String.format("reg.getString(\"%s\")  => IllegalArgumentException", key));
		try {
			@SuppressWarnings("unused")
			int actual = reg.getInt(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (IllegalArgumentException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");
	}


	@Test
	public void a006() {

		TestUtil.dispHeader("a006 Key=null で読みだすと例外が発生する　getString()");

		File file = before("a006"); //前処理
		
		Reg reg = new Reg(file.getPath());
		String key = null;

		
		TestUtil.dispPrompt(this, String.format("reg.getString(%s)  => IllegalArgumentException", key));
		try {
			@SuppressWarnings("unused")
			String actual = reg.getString(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (IllegalArgumentException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");

	}
	
	@Test
	public void a007() {

		TestUtil.dispHeader("a007 Key=null で値を設定すると例外が発生する　setInt()");

		File file = before("a007"); //前処理
		
		Reg reg = new Reg(file.getPath());
		String key = "TEST";
		int val = 123;
		reg.setInt(key, val);
		TestUtil.dispPrompt(this, String.format("reg.setInt(%s,%d)", key, val));

		key = null;
		TestUtil.dispPrompt(this, String.format("reg.getString(%s)  => IllegalArgumentException", key));
		try {
			@SuppressWarnings("unused")
			int actual = reg.getInt(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (IllegalArgumentException ex) {

			key = "TEST";
			int actual = reg.getInt(key);
			assertThat(actual, is(val));
			TestUtil.dispPrompt(this, String.format("reg.getInt(%s)=%d", key, actual));
			
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");

	}

	@Test
	public void a008() {

		TestUtil.dispHeader("a008 Key=null で値を設定すると例外が発生する　setString()");

		File file = before("a008"); //前処理
		
		Reg reg = new Reg(file.getPath());
		String key = null;
		String val = "123";
		
		TestUtil.dispPrompt(this, String.format("reg.setString(%s,\"%s\") => IllegalArgumentException", key, val));
		try {
			reg.setString(key, val);
			Assert.fail("この行が実行されたらエラー");
		} catch (IllegalArgumentException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");
	}
	
	@Test
	public void a009() {

		TestUtil.dispHeader("a009 val=null で値を設定すると\"\"が保存される　setString()");

		File file = before("a009"); //前処理
		
		Reg reg = new Reg(file.getPath());
		String key = "key2";
		String val = null;
		reg.setString(key, val);
		TestUtil.dispPrompt(this, String.format("reg.setString(\"%s\",%s)", key, val));

		String actual = reg.getString(key);
		TestUtil.dispPrompt(this, String.format("reg.getString(\"%s\")= \"%s\"", key, actual));
		assertThat(actual, is(""));
		
		reg.dispose();

		after(file); //後始末

	}


}
