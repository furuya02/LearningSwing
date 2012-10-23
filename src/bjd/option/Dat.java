package bjd.option;

import bjd.ctrl.CtrlType;
import bjd.util.ListBase;

/**
 * オリジナルデータ型<br>
 * OneDatのリストを管理する<br>
 * コンストラクタで指定する CtrlType [] によってOneDatの型が及びシークレートカラムが決定する<br>
 * 
 * @author SIN
 *
 */
public final class Dat extends ListBase<OneDat> {
	
	/**
	 * シークレット化が必要なカラム
	 */
	private boolean[] isSecretList;
	private int colMax;
	
	/**
	 * コンストラクタ<br>
	 * 
	 * @param ctrlTypeList 扱うOneDatの型指定
	 */
	public Dat(CtrlType [] ctrlTypeList) {
		//カラム数の初期化
		colMax = ctrlTypeList.length;
		//isSecretListの生成
		isSecretList = new boolean[colMax];
		for (int i = 0; i < colMax; i++) {
			isSecretList[i] = false;
			if (ctrlTypeList[i] == CtrlType.HIDDEN) {
				isSecretList[i] = true;
			}
		}
	}
	
	
	/**
	 * 文字列によるOneDatの追加<br>
	 * 内部で、OneDatの型がチェックされる<br>
	 * 
	 * @param enable 有効/無効
	 * @param str OneDatを生成する文字列
	 * @throws DatException 不正な文字列によってOneDatが生成できない
	 * @return 成功・失敗
	 */
	public boolean add(boolean enable, String str) throws DatException {
		if (str == null) {
			throw new DatException("引数にnullが渡されました");
		}
		String[] list = str.split("\t");
		if (list.length != colMax) {
			throw new DatException("カラム数が一致しません");
		}

		if (!getAr().add(new OneDat(enable, list, isSecretList))) {
			return false;
		}
		return true;
	}
	/**
	 * 文字列化<br>
	 * @param isSecret 秘匿が必要なカラムを***に変換して出力する
	 * @return 出力文字列
	 */
    public String toReg(boolean isSecret) {
    	StringBuilder sb = new StringBuilder();
        for (OneDat o : getAr()) {
            if (sb.length() != 0) {
                sb.append("\b");
            }
            sb.append(o.toReg(isSecret));
        }
        return sb.toString();
    }

    /**
     * 文字列による初期化
     * @param str　
     * @return　成否
     * @throws DatException 初期化文字列が不正なためOneDatの初期化に失敗
     */
	public boolean fromReg(String str) throws DatException {
 		getAr().clear();
		if (str == null) {
			throw new DatException("str == null");
		}
		if (str.equals("")) {
			throw new DatException("str == \"\"");
		}
		// 各行処理
		String [] lines = str.split("\b");
		if (lines.length <= 0) {
			throw new DatException("lines.length <= 0");
		}
		
		for (String l : lines) { 
			//ダミーのOneDatを作成
			OneDat oneDat = new OneDat(true, new String[colMax], isSecretList);
			
			if (l.split("\t").length != isSecretList.length + 1) { // +1はenableカラムの分
				throw new DatException("col != isSecretList.length");
			}
			
			if (oneDat.fromReg(l)) {
				if (getAr().add(oneDat)) {
					continue; // 処理成功
				}
			}
			//処理失敗
			getAr().clear();
			throw new DatException(String.format("oneDat.fromReg(%s)", l));
		}
		return true;
	}
}
