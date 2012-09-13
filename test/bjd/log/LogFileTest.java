package bjd.log;

import java.io.File;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bjd.util.FileSearch;
import bjd.util.TestUtil;
import bjd.util.Util;

public final class LogFileTest {

	private static File dir; //作業ディレクトリ
	private static ConfLog conf = null;
	
	@BeforeClass
	public static void beforeClass() {
		String path = String.format("%s\\testDir", new File(".").getAbsoluteFile().getParent());
		dir = new File(path);

		conf = new ConfLog(null, null);
		conf.setTestValue("saveDirectory", dir.getPath());
	}

	@Before
	public void before() {

		//作業ディレクトリの生成
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	@After
	public void after() {
		//作業ディレクトリの破棄
		Util.fileDelete(dir);
	}

	@AfterClass
	public static void doAfterClass() {
	}

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 LogFileの生成時に、オプションで指定したログファイルが生成されているか");


		for (int n = 0; n <= 2; n++) {
			conf.setTestValue("nomalFileName", n);
			conf.setTestValue("secureFileName", n);

			LogFile logFile = new LogFile(null, conf, null, true, null);

			//ログの種類に応じた２つのファイルが生成されていることを確認する
			FileSearch fs = new FileSearch(dir.getPath());
			File[] files = fs.listFiles("*.Log"); // n==2
			switch (n) {
				case 0:
					files = fs.listFiles("*.????.??.??.log");
					break;
				case 1:
					files = fs.listFiles("*.????.??.log");
					break;
				default:
					break;
			}
			assertThat(files.length, is(2));
			TestUtil.dispPrompt(this, String.format("ログの種類 = %d files.length = %d", n, files.length));
			logFile.dispose(); // ログクローズ
			for (File f : files) {
				f.delete();
				System.out.print(" " + f.getName());
			}
			System.out.println();
		}

	}

	
	//TODO ■LogFileTest deleteLog  未実装
	//TODO ■LogFileTest tail 未実装
	//TODO ■LogFileTest append(OneLOg)未実装
	
}
