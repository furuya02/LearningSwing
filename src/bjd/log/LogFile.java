package bjd.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.server.OneServer;
import bjd.util.FileSearch;
import bjd.util.IDispose;
import bjd.util.Util;


public final class LogFile implements IDispose {

	private Logger logger;
	private OneServer remoteServer;
	private Conf conf;
	private LogView logView;

	private boolean useLog;

	private OneLogFile normalLog; // 通常ログ
	private OneLogFile secureLog; // セキュアログ

	private Calendar dt = null; // インスタンス生成時に初期化し、日付が変化したかどうかの確認に使用する
	private Calendar lastDelete = null; // new DateTime(0);
	private Timer timer = null;
	
	private Object lock = new Object(); //排他制御用オブジェクト

	public LogFile(Logger logger, Conf conf, LogView logView,
			boolean useLog, OneServer remoteServer) {
		this.logger = logger;
		this.conf = conf;
		this.logView = logView;
		this.remoteServer = remoteServer;

		dt = Calendar.getInstance();
		lastDelete = Calendar.getInstance();
		lastDelete.setTime(new Date(0));

		this.useLog = (boolean) conf.get("useLogFile");
		if (!useLog) { // サービス起動ではログ保存されない
			this.useLog = useLog;
		}

		if (useLog) {
			if (!(new File((String) conf.get("saveDirectory")).exists())) {
				logger.set(LogKind.Error, null, 9000031, String.format("saveDirectory=%s", (String) conf.get("saveDirectory")));
				useLog = false;
			}
			logOpen();

			// 5分に１回のインターバルタイマ
			timer = new Timer();
			timer.schedule(new MyTimer(), 0, 1000 * 60 * 5);
		}
	}

	public void dispose() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		logClose();
		if (useLog) {
			logDelete(); // 過去ログの自動削除
		}
	}

	class MyTimer extends TimerTask {
		@Override
		public void run() {
			// if (runMode != RunMode.Normal && runMode != RunMode.Service) {
			// return;
			// }
			if (!useLog) {
				return;
			}

			Calendar now = Calendar.getInstance();

			// 日付が変わっている場合は、ファイルを初期化する
			if (lastDelete.getTime().getTime() != 0
					&& lastDelete.get(Calendar.DATE) == now.get(Calendar.DATE)) {
				return;
			}

			synchronized (lock) {
				logClose(); // クローズ
				logDelete(); // 過去ログの自動削除
				logOpen(); // オープン
				lastDelete = now;
			}
		}
	}

	// ログファイルへの追加
	public void append(OneLog oneLog) {

		//TODO 表示制限の所が怪しいので、再設計してテストを作成する必要がある
		// 表示制限の確認
		Dat limit = (Dat) conf.get("limitString");
		boolean isHit = false;
		if (limit != null) {
			for (OneDat o : limit) {
				if (o.isEnable()) { //有効なデータだけを対象にする
					if (oneLog.toString().indexOf(o.getStrList().get(0)) != -1) {
						isHit = true;
						break;
					}
				}
			}
		}
		boolean isDisplay = true;
		if ((int) conf.get("isDisplay") == 0) {
			isDisplay = isHit;
		} else {
			isDisplay = !isHit;
		}
		
		if (isDisplay) {
			// ログビューへの追加
			if (logView != null) {
				logView.append(oneLog);
			}
			// リモートサーバへの追加
			if (remoteServer != null) {
				remoteServer.append(oneLog);
			}
		}

		synchronized (lock) {
			// セキュリティログは、表示制限に関係なく書き込む
			if (secureLog != null && oneLog.isSecure()) {
				secureLog.set(oneLog.toString());
			}
			// 通常ログの場合
			if (normalLog != null) {
				// ルール適用除外　もしくは　表示対象になっている場合
				if (!(boolean) conf.get("useLimitString") || isDisplay) {
					normalLog.set(oneLog.toString());
				}
			}
		}
	}

	void logOpen() {
		// ログファイルオープン
		dt = Calendar.getInstance(); // 現在時間で初期化される
		String fileName = "";
		String saveDirectory = (String) conf.get("saveDirectory");

		switch ((int) conf.get("normalFileName")) {
			case 0:// bjd.yyyy.mm.dd.log
				fileName = String.format("%s\\bjd.%04d.%02d.%02d.log", saveDirectory, dt.get(Calendar.YEAR), (dt.get(Calendar.MONTH) + 1), dt.get(Calendar.DATE));
				break;
			case 1:// bjd.yyyy.mm.log
				fileName = String.format("%s\\bjd.%04d.%02d.log", saveDirectory, dt.get(Calendar.YEAR), (dt.get(Calendar.MONTH) + 1));
				break;
			case 2:// BlackJumboDog.Log
				fileName = String.format("%s\\BlackJumboDog.Log", saveDirectory);
				break;
			default:
				Util.designProblem(String.format("nomalFileName=%d", (int) conf.get("normalFileName")));
		}
		normalLog = new OneLogFile(fileName);

		switch ((int) conf.get("secureFileName")) {
			case 0:// secure.yyyy.mm.dd.log
				fileName = String.format("%s\\secure.%04d.%02d.%02d.log", saveDirectory, dt.get(Calendar.YEAR), (dt.get(Calendar.MONTH) + 1), dt.get(Calendar.DATE));
				break;
			case 1:// secure.yyyy.mm.log
				fileName = String.format("%s\\secure.%04d.%02d.log", saveDirectory, dt.get(Calendar.YEAR), (dt.get(Calendar.MONTH) + 1));
				break;
			case 2:// secure.Log
				fileName = String.format("%s\\secure.Log", saveDirectory);
				break;
			default:
				Util.designProblem(String.format("secureFileName=%d", (int) conf.get("secureFileName")));
		}
		secureLog = new OneLogFile(fileName);
	}

	private void logClose() {
		// オープン中のログファイルがある場合はクローズする
		if (normalLog != null) {
			normalLog.dispose();
			normalLog = null;
		}
		if (secureLog != null) {
			secureLog.dispose();
			secureLog = null;
		}
	}

	// 過去ログの自動削除
	private void logDelete() {

		// 自動ログ削除が無効な場合は、処理なし
		if (!(boolean) conf.get("useLogClear")) {
			return;
		}
		// 0を指定した場合、削除しない
		if ((int) conf.get("saveDays") == 0) {
			return;
		}

		if (!(new File((String) conf.get("saveDirectory"))).exists()) {
			return;
		}

		if (normalLog != null) {
			normalLog.dispose();
			normalLog = null;
		}
		if (secureLog != null) {
			secureLog.dispose();
			secureLog = null;
		}

		// ログディレクトリの検索
		FileSearch fs = new FileSearch((String) conf.get("saveDirectory"));
		// 一定
		ArrayList<File> files = Util.merge(fs.listFiles("BlackJumboDog.Log"), fs.listFiles("secure.Log"));
		for (File f : files) {
			tail(f.getParent(), (int) conf.get("saveDays"), Calendar.getInstance()); // saveDays日分以外を削除
		}
		// 日ごと
		files = Util.merge(fs.listFiles("bjd.????.??.??.Log"),
				fs.listFiles("secure.????.??.??.Log"));
		for (File f : files) {
			String[] tmp = f.getName().split("\\.");
			if (tmp.length == 5) {
				try {
					int year = Integer.valueOf(tmp[1]);
					int month = Integer.valueOf(tmp[2]);
					int day = Integer.valueOf(tmp[3]);

					deleteLog(year, month, day, (int) conf.get("saveDays"), f.getPath());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		// 月ごと
		files = Util.merge(fs.listFiles("bjd.????.??.Log"),
				fs.listFiles("secure.????.??.Log"));
		for (File f : files) {
			String[] tmp = f.getName().split("\\.");
			if (tmp.length == 4) {
				try {
					int year = Integer.valueOf(tmp[1]);
					int month = Integer.valueOf(tmp[2]);
					int day = 30;
					deleteLog(year, month, day, (int) conf.get("saveDays"), f.getPath());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void deleteLog(int year, int month, int day, int saveDays,
			String fullName) {
		try {
			// var targetDt = new DateTime(year, month, day);
			Calendar targetDt = Calendar.getInstance(); // 現在時間で初期化される
			targetDt.set(year, month, day);
			targetDt.add(Calendar.DAY_OF_MONTH, saveDays);

			if (dt.getTimeInMillis() > targetDt.getTimeInMillis()) {
				logger.set(LogKind.Detail, null, 9000032, fullName);
				(new File(fullName)).delete();
			}

		} catch (Exception ex) {
			logger.set(LogKind.Error, null, 9000045, String.format(
					"year=%d mont=%d day=%d %s", year, month, day, fullName));
			(new File(fullName)).delete();
		}
	}
	

	private void tail(String fileName, int saveDays, Calendar now) {
		Calendar targetDt = Calendar.getInstance(); //インスタンスの生成（初期化時間は関係ない）
		DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		File file = new File(fileName);
		try {
			ArrayList<String> lines = new ArrayList<>();
			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				boolean isNeed = false;
				while (true) {
					String str = br.readLine();
					if (str == null) {
						break;
					}
					if (isNeed) {
						lines.add(str);
					} else {
						String[] tmp = str.split("\t");
						if (tmp.length > 1) {
							targetDt.setTime((Date) f.parse(tmp[0]));
							targetDt.add(Calendar.DAY_OF_MONTH, saveDays);
							if (now.getTimeInMillis() < targetDt.getTimeInMillis()) {
								isNeed = true;
								lines.add(str);
							}
						}
					}
				}
				br.close();
			}
			Util.textFileSave(file, lines);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
