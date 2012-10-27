package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import junit.framework.Assert;

import org.junit.Test;

import bjd.ctrl.ListView;
import bjd.option.Conf;
import bjd.option.OptionBasic;
import bjd.util.TestUtil;

public final class WindowSizeTest {

	private File createFile(String fileName) {
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		return new File(String.format("%s\\%s", currentDir, fileName));
	}

	private WindowSize createWindowSize(File file) {
		Conf conf = new Conf(new OptionBasic(new Kernel(), "", "Basic"));
		conf.set("useLastSize", true);
		try {
			return new WindowSize(conf, file.getPath());
		} catch (IOException e) {
			Assert.fail();
		}
		return null;
	}

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 listViewのカラムの復元");

		String tag = "a001";
		File file = createFile(tag);
		WindowSize windowSize = createWindowSize(file); //　生成

		ListView listView = new ListView(tag);
		listView.addColumn("col1");
		listView.addColumn("col2");
		listView.setColWidth(0, 111);
		listView.setColWidth(1, 222);
		windowSize.save(listView); // カラムサイズ保存

		TestUtil.dispPrompt(this, "windowSize.save(listView) col0=111 col1=222");

		windowSize.dispose(); // 破棄
		listView.dispose(); // 破棄

		listView = new ListView(tag);
		listView.addColumn("col1");
		listView.addColumn("col2");

		windowSize.read(listView); // カラムサイズ読込

		//１カラム目のサイズ
		int actual = listView.getColWidth(0);
		assertThat(actual, is(111));
		TestUtil.dispPrompt(this, String.format("windowSize.read(listView) col0=%d", actual));

		//2カラム目のサイズ
		actual = listView.getColWidth(1);
		assertThat(actual, is(222));
		TestUtil.dispPrompt(this, String.format("windowSize.read(listView) col1=%d", actual));

		windowSize.dispose(); // 破棄
		listView.dispose(); // 破棄

		file.delete(); //後始末

	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 listViewのカラムの復元 保存データが無いとき最低値100を読み出す");

		String tag = "a002";
		File file = createFile(tag);
		WindowSize windowSize = createWindowSize(file); //　生成

		ListView listView = new ListView(tag);
		listView.addColumn("col1");
		listView.addColumn("col2");

		windowSize.read(listView); // カラムサイズ読込

		//１カラム目のサイズ
		int actual = listView.getColWidth(0);
		assertThat(actual, is(100));
		TestUtil.dispPrompt(this, String.format("windowSize.read(listView) col0=%d", actual));

		//2カラム目のサイズ
		actual = listView.getColWidth(1);
		assertThat(actual, is(100));
		TestUtil.dispPrompt(this, String.format("windowSize.read(listView) col1=%d", actual));

		windowSize.dispose(); // 破棄
		listView.dispose(); // 破棄

		file.delete(); //後始末

	}
	
	@Test
	public void a003() {

		TestUtil.dispHeader("a003 JFrameのサイズの復元");

		String tag = "a003";
		File file = createFile(tag);
		WindowSize windowSize = createWindowSize(file); //　生成

		JFrame frame = new JFrame(tag);
		frame.setSize(200, 100);
		frame.setLocation(30, 40);
		windowSize.save(frame); // サイズ保存
		TestUtil.dispPrompt(this, "windowSize.save(frame) width=200 height=100 x=30 y=40");
		windowSize.dispose(); // 破棄
		frame.dispose(); // 破棄

		frame = new JFrame(tag);
		windowSize.read(frame); // サイズ読込
		TestUtil.dispPrompt(this, "windowSize.read(frame)");

		int actual = frame.getWidth();
		int expected = 200;
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("width=%d", actual));

		actual = frame.getHeight();
		expected = 100;
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("heighth=%d", actual));

		actual = frame.getX();
		expected = 30;
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("x=%d", actual));

		actual = frame.getY();
		expected = 40;
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("y=%d", actual));

		windowSize.dispose(); // 破棄
		frame.dispose(); // 破棄

		file.delete(); //後始末

	}
	

	@Test
	public void a004() {

		TestUtil.dispHeader("a004 JFrameのサイズの復元 保存データが無いときデフォルト値を読み出す");

		String tag = "a004";
		File file = createFile(tag);
		WindowSize windowSize = createWindowSize(file); //　生成

		//		JFrame frame = new JFrame(tag);
		//		windowSize.save(frame); // サイズ保存
		//		TestUtil.dispPrompt(this, "windowSize.save(frame) XXXX=XXX");
		//		windowSize.dispose(); // 破棄
		//		frame.dispose(); // 破棄

		JFrame frame = new JFrame(tag);
		windowSize.read(frame); // サイズ読込
		TestUtil.dispPrompt(this, "windowSize.read(frame)");

		int actual = frame.getWidth();
		int expected = 800;
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("width=%d", actual));

		actual = frame.getHeight();
		expected = 400;
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("heighth=%d", actual));

		actual = frame.getX();
		expected = 0;
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("x=%d", actual));

		actual = frame.getY();
		expected = 0;
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("y=%d", actual));

		windowSize.dispose(); // 破棄
		frame.dispose(); // 破棄

		file.delete(); //後始末

	}
}
