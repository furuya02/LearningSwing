package bjd.log;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import bjd.util.FileSearch;
import bjd.util.TestUtil;
import bjd.util.Util;

public final class LogFileTest {
	
	//多重スレッドでテストが走ると、同一ファイルにアクセスしてしまうので、テストごとにテンポラリディレクトリを用意するようにする
	private File before(String tmpDir) {
		File dir; // 作業ディレクトリ
		ConfLog conf = null;
		String path = String.format("%s\\%s", new File(".").getAbsoluteFile().getParent(), tmpDir);
		dir = new File(path);
		// 作業ディレクトリの生成
		if (dir.exists()) {
			Util.fileDelete(dir);
		}
		dir.mkdir();
		return dir;
	}

	private void after(File dir) {
		// 作業ディレクトリの破棄
		Util.fileDelete(dir);
	}

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 LogFileの生成時に、オプションで指定したログファイルが生成されているか");

		File dir = before("a001");
		ConfLog conf = new ConfLog(null, null);
		conf.setTestValue("saveDirectory", dir.getPath());
		

		for (int n = 0; n <= 2; n++) {
			conf.setTestValue("normalFileName", n);
			conf.setTestValue("secureFileName", n);

			LogFile logFile = new LogFile(null, conf, null, true, null);

			// ログの種類に応じた２つのファイルが生成されていることを確認する
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
		
		after(dir);
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 append(OneLog)して、それぞれのファイルに当該行数が追加されているかどうか");

		File dir = before("a002");
		ConfLog conf = new ConfLog(null, null);
		conf.setTestValue("saveDirectory", dir.getPath());

		conf.setTestValue("normalFileName", 2); // BlackJumboDog
		conf.setTestValue("secureFileName", 2); // Secure.Log
		conf.setTestValue("useLogFile", true);
		//conf.setTestValue("saveDays",100);
		//conf.setTestValue("useLogClear",false);
		

		LogFile logFile = new LogFile(null, conf, null, true, null);

		String s1 = "2012/06/01 00:00:00\tDetail\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s2 = "2012/06/02 00:00:00\tError\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s3 = "2012/06/03 00:00:00\tSecure\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		TestUtil.dispPrompt(this, String.format("append(%s)", s1));
		TestUtil.dispPrompt(this, String.format("append(%s)", s2));
		TestUtil.dispPrompt(this, String.format("append(%s)", s3));

		logFile.append(new OneLog(s1));
		logFile.append(new OneLog(s2));
		logFile.append(new OneLog(s3));
		logFile.dispose();

		String fileName = "BlackJumboDog.Log";
		ArrayList<String> lines = Util.textFileRead(new File(String.format("%s\\%s", dir.getPath(), fileName)));
		assertThat(lines.size(), is(3));
		TestUtil.dispPrompt(this, String.format("%s には　%d行　追加されている", fileName, lines.size()));

		fileName = "secure.Log";
		lines = Util.textFileRead(new File(String.format("%s\\%s", dir.getPath(), fileName)));
		assertThat(lines.size(), is(1));
		TestUtil.dispPrompt(this, String.format("%s には　%d行　追加されている", fileName, lines.size()));

		after(dir);
	}

	@Test
	public void a003() {

		TestUtil.dispHeader("a003 tail() 2012/09/01~7日分のログを準備して9/7(本日)からsaveDays=2でtailする");

		File dir = before("a003");
		ConfLog conf = new ConfLog(null, null);
		conf.setTestValue("saveDirectory", dir.getPath());

		conf.setTestValue("normalFileName", 2); // BlackJumboDog
		conf.setTestValue("secureFileName", 2); // Secure.Log
		conf.setTestValue("useLogFile", true);
		conf.setTestValue("saveDays", 30);

		LogFile logFile = new LogFile(null, conf, null, true, null);

		//2012/09/01~7日分のログを準備
		String s1 = "2012/09/01 00:00:00\tDetail\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s2 = "2012/09/02 00:00:00\tError\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s3 = "2012/09/03 00:00:00\tSecure\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s4 = "2012/09/04 00:00:00\tSecure\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s5 = "2012/09/05 00:00:00\tSecure\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s6 = "2012/09/06 00:00:00\tSecure\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s7 = "2012/09/07 00:00:00\tSecure\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		TestUtil.dispPrompt(this, String.format("append(%s)", s1));
		TestUtil.dispPrompt(this, String.format("append(%s)", s2));
		TestUtil.dispPrompt(this, String.format("append(%s)", s3));
		TestUtil.dispPrompt(this, String.format("append(%s)", s4));
		TestUtil.dispPrompt(this, String.format("append(%s)", s5));
		TestUtil.dispPrompt(this, String.format("append(%s)", s6));
		TestUtil.dispPrompt(this, String.format("append(%s)", s7));
		logFile.append(new OneLog(s1));
		logFile.append(new OneLog(s2));
		logFile.append(new OneLog(s3));
		logFile.append(new OneLog(s4));
		logFile.append(new OneLog(s5));
		logFile.append(new OneLog(s6));
		logFile.append(new OneLog(s7));

		int saveDays = 2;
		String fileName = "BlackJumboDog.Log";
		String path = String.format("%s\\%s", dir.getPath(), fileName);
		File file = new File(path);

		ArrayList<String> lines = Util.textFileRead(file);
		assertThat(lines.size(), is(7));
		TestUtil.dispPrompt(this, String.format("%s には　%d行　追加されている", fileName, lines.size()));

		try {
			//リフレクションを使用してprivateメソッドにアクセスする
			Method tail = LogFile.class.getDeclaredMethod("tail", new Class[] { String.class, int.class, Calendar.class });
			tail.setAccessible(true);
			Calendar c = Calendar.getInstance();
			c.set(2012, 8, 7);
			System.out.println(String.format("本日を、%d.%d.%dにセットする", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)));
			tail.invoke(logFile, path, saveDays, c);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		TestUtil.dispPrompt(this, String.format("tail(%s,%d) 保存日数=%d", fileName, saveDays, saveDays));

		lines = Util.textFileRead(file);
		assertThat(lines.size(), is(2));

		for (String s : lines) {
			TestUtil.dispPrompt(this, s);
		}
		TestUtil.dispPrompt(this, String.format("%s には　%d行　追加されている", fileName, lines.size()));

		logFile.dispose();

		after(dir);
	}


	// TODO ■LogFileTest deleteLog 未実装


}