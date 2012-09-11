package bjd.log;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.option.OneOption;

public final class ConfigLog {

	private LogLimit logLimit;
	private boolean useLogFile; //「ログファイルを生成
	private String saveDirectory = ""; //ログ保存ディレクトリ
	private boolean useLogClear; //自動ログ削除
	private int saveDays; //ログ保存日数
	private int nomalFileName; //通常ファイルの種類
	private int secureFileName; //セキュアファイルの種類

	public ConfigLog(OneOption option, Kernel kernel) {
		logLimit = new LogLimit(option);
		useLogFile = (boolean) option.getValue("useLogFile");
		saveDirectory = kernel.env((String) option.getValue("saveDirectory"));
		useLogClear = (boolean) option.getValue("useLogClear");
		saveDays = (int) option.getValue("saveDays");
		nomalFileName = (int) option.getValue("nomalFileName");
		secureFileName = (int) option.getValue("secureFileName");
	}

	public boolean isUseLogFile() {
		return useLogFile;
	}

	public String getSaveDirectory() {
		return saveDirectory;
	}

	public boolean isUseLogClear() {
		return useLogClear;
	}

	public int getSaveDays() {
		return saveDays;
	}

	public int getNomalFileName() {
		return nomalFileName;
	}

	public int getSecureFileName() {
		return secureFileName;
	}
	
	//********************************************************
	// LogLimitをカバーするメソッド
	//********************************************************
	public boolean isDisplay(String str) {
		return logLimit.isDisplay(str);
	}
	public boolean getUseLimitString() {
		return logLimit.getUseLimitString();
	}

	/**
	 * ConfigLogの中で使用される「使用制限」を表現するクラス
	 */
	final class LogLimit {
		private boolean isDisplay; //ログビューに表示するかどうか
		private ArrayList<String> ar = new ArrayList<>();

		//表示制限ルールをログファイルに適用するかどうか
		private boolean useLimitString;

		public boolean getUseLimitString() {
			return useLimitString;
		}

		public LogLimit(OneOption oneOption) {
			isDisplay = ((int) oneOption.getValue("isDisplay") == 0);
			Dat dat = (Dat) oneOption.getValue("LimitString");
			if (dat != null) {
				for (OneDat o : dat) {
					if (o.isEnable()) { //有効なデータだけを対象にする
						ar.add(o.getStrList().get(0));
					}
				}
			}
			useLimitString = (boolean) oneOption.getValue("UseLimitString");
		}

		public boolean isDisplay(String logStr) {
			for (String str : ar) {
				if (logStr.indexOf(str) != -1) { //ヒット
					return isDisplay;
				}
			}
			return !isDisplay;
		}
	}
}
