package bjd.option;

import java.awt.Font;
import java.lang.reflect.Field;
import java.util.ArrayList;

import bjd.Kernel;

public final class ConfLog extends OneConf {

	private LogLimit logLimit;
	private boolean useLogFile; //「ログファイルを生成
	private String saveDirectory = ""; //ログ保存ディレクトリ
	private boolean useLogClear; //自動ログ削除
	private int saveDays; //ログ保存日数
	private int normalFileName; //通常ファイルの種類
	private int secureFileName; //セキュアファイルの種類
	private Font font; 

	public ConfLog(OneOption option, Kernel kernel) {
		if (option != null) {
			logLimit = new LogLimit(option);
			useLogFile = (boolean) option.getValue("useLogFile");
			if (kernel != null) {
				saveDirectory = kernel.env((String) option.getValue("saveDirectory"));
			}
			useLogClear = (boolean) option.getValue("useLogClear");
			saveDays = (int) option.getValue("saveDays");
			normalFileName = (int) option.getValue("normalFileName");
			secureFileName = (int) option.getValue("secureFileName");
			font = (Font) option.getValue("font");
		}
	}

	@Override
	protected Field abstractGetField(String tag) {
		try {
			return (ConfLog.class).getDeclaredField(tag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean useLogFile() {
		return useLogFile;
	}

	public String saveDirectory() {
		return saveDirectory;
	}

	public boolean useLogClear() {
		return useLogClear;
	}

	public int saveDays() {
		return saveDays;
	}

	public int normalFileName() {
		return normalFileName;
	}

	public int secureFileName() {
		return secureFileName;
	}

	public Font font() {
		return font;
	}
	//********************************************************
	// LogLimitをカバーするメソッド
	//********************************************************
	public boolean isDisplay(String str) {
		if (logLimit != null) {
			return logLimit.isDisplay(str);
		}
		return true;
	}

	public boolean useLimitString() {
		if (logLimit != null) {
			return logLimit.useLimitString();
		}
		return true;
	}

	/**
	 * ConfigLogの中で使用される「使用制限」を表現するクラス
	 */
	final class LogLimit {
		private boolean isDisplay; //ログビューに表示するかどうか
		private ArrayList<String> ar = new ArrayList<>();

		//表示制限ルールをログファイルに適用するかどうか
		private boolean useLimitString;

		public boolean useLimitString() {
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
