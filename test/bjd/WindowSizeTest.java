package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import bjd.ctrl.ListView;
import bjd.option.ConfBasic;
import bjd.util.TestUtil;

public final class WindowSizeTest {

	private File createFile(String fileName) {
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		return new File(String.format("%s\\%s", currentDir, fileName));
	}

	private WindowSize createWindowSize(File file) {
		ConfBasic conf = new ConfBasic(null);
		conf.setTestValue("useLastSize", true);
		return new WindowSize(conf, file.getPath());
	}

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 listViewのサイズの復元");

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

	//TODO ■NEXT　Frameの保存と再現
}
