package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import bjd.log.ConfLog;
import bjd.log.LogFile;
import bjd.util.FileSearch;
import bjd.util.TestUtil;

public final class RegTest {

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 setInt() で保存したデータを　getInt()で読み出す");

		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		File file = new File(currentDir + "\\a001.ini");

		Reg reg = new Reg(file.getPath());
		TestUtil.dispPrompt(this, String.format("reg　= new Reg(%s)", file.getPath()));

		String key = "key";
		int val = 123;
		reg.setInt(key, val);
		TestUtil.dispPrompt(this, String.format("reg.setInt(%s,%d)", key, val));

		reg.dispose();
		TestUtil.dispPrompt(this, "reg.dispose()");

		reg = new Reg(file.getPath());
		TestUtil.dispPrompt(this, String.format("reg　= new Reg(%s)", file.getPath()));

		int actual = reg.getInt(key);
		TestUtil.dispPrompt(this, String.format("reg.getInt(%s)= %d", key, actual));

		assertThat(actual, is(val));

		//後始末
		reg.dispose();
		file.delete();
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 setString() で保存したデータを　getString()で読み出す");

		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		File file = new File(currentDir + "\\a002.ini");

		Reg reg = new Reg(file.getPath());
		TestUtil.dispPrompt(this, String.format("reg　= new Reg(%s)", file.getPath()));

		String key = "key";
		String val = "value";
		reg.setString(key, val);
		TestUtil.dispPrompt(this, String.format("reg.setStr(%s,%s)", key, val));

		reg.dispose();
		TestUtil.dispPrompt(this, "reg.dispose()");

		reg = new Reg(file.getPath());
		TestUtil.dispPrompt(this, String.format("reg　= new Reg(%s)", file.getPath()));

		String actual = reg.getString(key);
		TestUtil.dispPrompt(this, String.format("reg.getString(%s)= %s", key, actual));

		assertThat(actual, is(val));

		//後始末
		reg.dispose();
		file.delete();
	}
	
	@Test
	public void a003() {

		TestUtil.dispHeader("a003 保存されていないデータを　getInt()で読み出すと 0が返される");

		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		File file = new File(currentDir + "\\a003.ini");

		Reg reg = new Reg(file.getPath());
		TestUtil.dispPrompt(this, String.format("reg　= new Reg(%s)", file.getPath()));

		String key = "key";
		int actual = reg.getInt(key);
		TestUtil.dispPrompt(this, String.format("reg.getInt(%s) = %d", key, actual));

		assertThat(actual, is(0));

		//後始末
		reg.dispose();
		file.delete();
	}

	@Test
	public void a004() {

		TestUtil.dispHeader("a004 保存されていないデータを　getString()で読み出すと\"\"が返される");

		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		File file = new File(currentDir + "\\a004.ini");

		Reg reg = new Reg(file.getPath());
		TestUtil.dispPrompt(this, String.format("reg　= new Reg(%s)", file.getPath()));

		String key = "key";
		String actual = reg.getString(key);
		TestUtil.dispPrompt(this, String.format("reg.getInt(%s) = %s", key, actual));
		
		assertThat(actual, is(""));

		//後始末
		reg.dispose();
		file.delete();
	}
	
	
}
