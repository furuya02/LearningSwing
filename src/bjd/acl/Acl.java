package bjd.acl;

import bjd.net.Ip;

/**
 * ACLの基底クラス
 * 
 * 開始アドレス Ip start と　終了アドレス　Ｉｐ　ｅｎｄ　を保持している
 * 上位クラスのコンストラクタによる初期化に失敗した場合、throwException(String ipStr)を呼出し、IllegalArgumentExceptionをスローする
 * 
 * @author SIN
 *
 */
abstract class Acl {
	private String name;
	private Ip start;
	private Ip end;

	abstract boolean isHit(Ip ip);

	protected Acl(String name) {
		this.name = name;
	}

	protected final String getName() {
		return name;
	}

	protected final void setStart(Ip start) {
		this.start = start;
	}

	protected final Ip getStart() {
		return start;
	}

	protected final void setEnd(Ip end) {
		this.end = end;
	}

	protected final Ip getEnd() {
		return end;
	}

	/**
	 * startとendを入れ替える
	 * 大小関係が逆になったとき使用する
	 * 
	 */
	protected final void swap() {
		Ip ip = start;
		start = end;
		end = ip;
	}

	//データを初期化し、例外を発生させる
	/**
	 * コンストラクタで初期化に失敗した時に使用する
	 * 内部変数は初期化され例外（IllegalArgumentException）がスローされる
	 * @param ipStr 初期化文字列
	 */
	protected final void throwException(String ipStr) {
		//TODO 無効オブジェクトで初期化する
		start = new Ip("0.0.0.0");
		end = new Ip("0.0.0.0");
		throw new IllegalArgumentException(String.format("引数が不正です \"%s\"", ipStr));
	}
	
}
