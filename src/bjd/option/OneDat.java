package bjd.option;

import java.util.ArrayList;
import bjd.util.IDispose;

public class OneDat implements IDispose {

	private boolean enable;
	private ArrayList<String> strList = new ArrayList<>();
	private boolean[] isSecretList;

	@Override
	public void dispose() {

	}

	public boolean isEnable() {
		return enable;
	}

	// 必要になったら有効にする
	// public void setEnable(boolean enable) {
	// this.enable = enable;
	// }

	public ArrayList<String> getStrList() {
		return strList;
	}

	private OneDat() {
		// デフォルトコンストラクタの隠蔽
	}

	public OneDat(boolean enable, String[] list, boolean[] isSecretList) {

		if (list == null || isSecretList == null || list.length != isSecretList.length) {
			throw new IllegalArgumentException("引数に矛盾があります");
		}

		this.enable = enable;
		this.isSecretList = new boolean[list.length];
		for (int i = 0; i < list.length; i++) {
			strList.add(list[i]);
			this.isSecretList[i] = isSecretList[i];
		}
	}

	// 　コンストラクタで定義した型に一致していないときfalseを返す
	public boolean fromReg(String str) {
		
		if (str == null) {
			return false;
		}
		String[] tmp = str.split("\t");

		//カラム数確認
		if (tmp.length != strList.size() + 1) {
			return false;
		}
		
		//enableカラム
		switch (tmp[0]) {
			case "":
				enable = true;
				break;
			case "#":
				enable = false;
				break;
			default:
				return false;
		}
		//以降の文字列カラム
		strList = new ArrayList<String>();
		for (int i = 1; i < tmp.length; i++) {
			strList.add(tmp[i]);
		}
		return true;
	}

	public String toReg(boolean isSecret) {
		StringBuilder sb = new StringBuilder();
		if (!enable) {
			sb.append("#");
		}
		for (int i = 0; i < strList.size(); i++) {
			sb.append('\t');
			if (isSecret && isSecretList[i]) { // シークレットカラム
				sb.append("***");
			} else {
				sb.append(strList.get(i));
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException(); // 誤って使用されないように例外（未実装）とする
	}
}
