package bjd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import bjd.util.IDispose;
import bjd.util.Util;

//オプションを記憶するＤＢ
//明示的にDispose()若しくはSave()を呼ばないと、保存されない
public final class Reg implements IDispose {

	private String path;
	private HashMap<String, String> ar = new HashMap<>();

	public Reg(String path) {
		this.path = path;
		File file = new File(path);
		if (file.exists()) {
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

	@Override
	public void dispose() {
		save();
	}

	public void save() {
		ArrayList<String> lines = new ArrayList<>();
		for (String key : ar.keySet()) {
			String str = String.format("%s=%s", key, ar.get(key));
			lines.add(str);
		}
		Util.textFileSave(new File(path), lines);
	}

	public int getInt(String key) {
		if (key == null) { //key==nulの場合　0が返される
			return 0;
		}
		
		if (key.equals("")) { //key==""の場合　0が返される
			return 0;
		}
		
		try {
			return Integer.valueOf(getString(key));
		} catch (Exception ex) {
			return 0;
		}
	}

	public void setInt(String key, int val) {
		if (key != null) { // key==nullの場合は処理なし
			setString(key, String.valueOf(val));
		}
	}

	public String getString(String key) {
		if (key == null) { //ket==""の場合　””が返される
			return "";
		}
		if (key.equals("")) { //key==""の場合　""が返される
			return "";
		}
		
		String ret = ar.get(key);
		if (ret == null) {
			return ""; //検索結果がヒットしなかった場合は""が返される
		}
		return ret;
	}

	public void setString(String key, String val) {
		if (key != null) { // key==nullの場合は処理なし
			ar.remove(key);
			if (val == null) { //val==nullの場合は、""を保存する
				val = "";
			}
			ar.put(key, val);
		}
	}
}
