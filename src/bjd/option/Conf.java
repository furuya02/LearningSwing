package bjd.option;

import java.util.ArrayList;
import java.util.HashMap;

import bjd.ctrl.CtrlType;
import bjd.util.Util;

public final class Conf {
	//複雑なOptionクラスが結合するのを防ぐためのクラス
	//単にOptionの値を取得（テストの際は設定）するだけのクラスは、このクラスを使用する
	private HashMap<String, Object> ar = new HashMap<>();

	public Conf(OneOption oneOption) {
		ArrayList<OneVal> list = oneOption.getListVal().getList(null);
		for (OneVal o : list) {
			CtrlType ctrlType = o.getOneCtrl().getCtrlType();
			switch (ctrlType) {
				case CHECKBOX:
				case TEXTBOX:
				case ADDRESSV4:
				case ADDRESSV6:
				case BINDADDR:
				case FOLDER:
				case FILE:
				case COMBOBOX:
				case DAT:
				case INT:
				case MEMO:
				case FONT:
				case RADIO:
				case HIDDEN:
					ar.put(o.getName(), o.getValue());
					break;
				case TABPAGE:
				case GROUP:
				case LABEL:
					break;
				default:
					Util.runtimeError(String.format("未定義 %s", ctrlType));
			}
		}
	}

	public Object get(String name) {
		if (!ar.containsKey(name)) { //HashMapの存在確認
			Util.runtimeError(String.format("未定義 %s", name));
		}
		return ar.get(name);
	}

	public void set(String name, Object value) {
		if (!ar.containsKey(name)) { //HashMapの存在確認
			Util.runtimeError(String.format("未定義 %s", name));
		}
		ar.put(name, value);
	}
}
