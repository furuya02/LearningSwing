package bjd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import bjd.util.IDispose;
import bjd.util.Util;

/**
 * オプションを記憶するＤＢ<br>
 * 明示的にDispose()若しくはSave()を呼ばないと、保存されない
 * 
 * @author SIN
 *
 */
public final class Reg implements IDispose {

	private String path;
	private HashMap<String, String> ar = new HashMap<>();

	/**
	 * コンストラクタ
	 * 
	 * @param path 記憶ファイル名
	 */
	public Reg(String path) {
		this.path = path;
		File file = new File(path);

		if (file.exists()) { //ファイルが存在する場合は、保存されているデータを読み込む
			ArrayList<String> lines = Util.textFileRead(file);
			for (String s : lines) {
				int index = s.indexOf("=");
				if (index < 1) {
					break;
				}
				String key = s.substring(0, index);
				String val = s.substring(index + 1);
				ar.put(key, val);
			}
		}
	}

	/**
	 * 終了処理
	 */
	@Override
	public void dispose() {
		save();
	}

	/**
	 * 保存
	 */
	public void save() {
		ArrayList<String> lines = new ArrayList<>();
		for (String key : ar.keySet()) {
			String str = String.format("%s=%s", key, ar.get(key));
			lines.add(str);
		}
		Util.textFileSave(new File(path), lines);
	}

	/**
	 * int値を読み出す<br>
	 * key==null若しくは、Key=="" の場合や、Keyの値がintでない場合、例外がスローされる
	 * 
	 * @param key キー
	 * @return int値
	 * @throws RegException 取得失敗 
	 */
	public int getInt(String key) throws RegException {
		try {
			return Integer.valueOf(getString(key));
		} catch (NumberFormatException ex) {

		}
		throw new RegException("getInt()");
	}

	/**
	 * int値を設定する<br>
	 * 
	 * key==null若しくはKey==""の場合、例外がスローされる
	 * 
	 * @param key
	 * @param val
	 * @throws RegException 
	 */
	public void setInt(String key, int val) throws RegException {
		setString(key, String.valueOf(val));
	}

	/**
	 * String値を読み出す<br>
	 * key==nullの場合や、Key==""の場合、例外がスローされる
	 * 
	 * @param key
	 * @return String値
	 * @throws RegException 
	 * @throws IllegalArgumentException
	 */
	public String getString(String key) throws RegException {
		if (key == null || key.equals("")) {
			// key==null 若しくは Key==""の時、例外がスローされる
			throw new RegException("");
		}
		String ret = ar.get(key);
		if (ret == null) {
			//検索結果がヒットしなかった場合、例外がスローされる
			throw new RegException("");
		}
		return ret;
	}

	/**
	 * String値の設定<br>
	 * key==nullの場合や、Key==""の場合、例外がスローされる
	 * 
	 * @param key
	 * @param val Strnig値
	 * @throws RegException  
	 */
	public void setString(String key, String val) throws RegException {
		if (key == null || key.equals("")) {
			// key==null 若しくは Key==""の時、例外がスローされる
			throw new RegException("");
		}
		ar.remove(key);
		if (val == null) { //val==nullの場合は、""を保存する
			val = "";
		}
		ar.put(key, val);
	}
}
