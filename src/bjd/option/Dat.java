package bjd.option;

import java.util.ArrayList;

import bjd.ctrl.CtrlType;
import bjd.util.ListBase;

public class Dat extends ListBase<OneDat> {
	
	//private ArrayList<CtrlType> ctrlTypeList;
	private boolean[] isSecretList; //シークレット化が必要なカラム
	private int colMax;
	
	public Dat(ArrayList<CtrlType> ctrlTypeList) {
		//this.ctrlTypeList = ctrlTypeList;
		//カラム数の初期化
		colMax = ctrlTypeList.size();
		//isSecretListの生成
		isSecretList = new boolean[colMax];
		for (int i = 0; i < colMax; i++) {
			isSecretList[i] = false;
			if (ctrlTypeList.get(i) == CtrlType.HIDDEN) {
				isSecretList[i] = true;
			}
		}
	}
	
	public boolean add(boolean enable, String str) {
		if (str == null) {
			throw new IllegalArgumentException("引数にnullが渡されました");
		}
		String[] list = str.split("\t");
		if (list.length != colMax) {
			throw new IllegalArgumentException("カラム数が一致しません");
		}

		if (!ar.add(new OneDat(enable, list, isSecretList))) {
			return false;
		}
		return true;
	}
	
    public String toReg(boolean isSecret) {
    	StringBuilder sb = new StringBuilder();
        for (OneDat o : ar) {
            if (sb.length() != 0) {
                sb.append("\b");
            }
            sb.append(o.toReg(isSecret));
        }
        return sb.toString();
    }

	public boolean fromReg(String str) {
		ar.clear();
		if (str == null) {
			return false;
		}
		if (str.equals("")) {
			return false;
		}
		// 各行処理
		String [] lines = str.split("\b");
		if (lines.length <= 0) {
			return false;
		}
		for (String l : lines) { 
			//ダミーのOneDatを作成
			OneDat oneDat = new OneDat(true, new String[colMax], isSecretList);
			if (oneDat.fromReg(l)) {
				if (ar.add(oneDat)) {
					continue; // 処理成功
				}
			}
			//処理失敗
			ar.clear();
			return false;
		}
		return true;
	}
}
