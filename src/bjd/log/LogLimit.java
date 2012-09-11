package bjd.log;

import java.util.ArrayList;

import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.option.OneOption;

public final class LogLimit {
	private boolean enabled;
	private ArrayList<String> ar = new ArrayList<>();

	//表示制限ルールをログファイルに適用するかどうか
	private boolean useLimitString;

	public boolean getUseLimitString() {
		return useLimitString;
	}

	public LogLimit(OneOption oneOption) {
		enabled = ((int) oneOption.getValue("EnableLimitString") == 0);
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

	public boolean enable(String logStr) {
		for (String str : ar) {
			if (logStr.indexOf(str) != -1) {//ヒット
				return enabled;
			}
		}
		return !enabled;
	}
}
