package bjd.acl;

import bjd.net.Ip;

/**
 * ACLの基底クラス
 * 
 * 開始アドレス Ip start と　終了アドレス　Ip end を保持している<br>
 * 上位クラスを実装する際、コンストラクタで初期化に失敗した場合は、throwException(String ipStr)を呼出し、IllegalArgumentExceptionをスローする
 * 
 * @author SIN
 *
 */
abstract class Acl {
	/**
	 * １つのACLにつけられた名前<br>
	 * 任意の文字列を指定できる
	 */
	private String name;
	/**
	 * ACLの範囲(開始IPアドレス)
	 */
	private Ip start;
	/**
	 * ACLの範囲(終了IPアドレス)
	 */
	private Ip end;

	/**
	 * 検査対象のアドレスがACL(範囲内)に含まれるかどうかの検査
	 * @param ip　検査アドレス
	 * @return　範囲内の場合 true
	 */
	abstract boolean isHit(Ip ip);

	/**
	 * ACLコンストラクタ
	 * @param name アドレス範囲を示す初期化文字列
	 */
	protected Acl(String name) {
		this.name = name;
	}

	/**
	 * ACLの名前取得
	 * @return 名前
	 */
	protected final String getName() {
		return name;
	}
	/**
	 * ACLの開始アドレスの設定
	 * @param start　開始アドレス
	 */
	protected final void setStart(Ip start) {
		this.start = start;
	}
	/**
	 * ACLの開始アドレスの取得
	 * @return　開始アドレス
	 */
	protected final Ip getStart() {
		return start;
	}
	/**
	 * ACLの終了アドレスの設定
	 * @param start　終了アドレス
	 */
	protected final void setEnd(Ip end) {
		this.end = end;
	}
	/**
	 * ACLの終了アドレスの取得
	 * @return　終了アドレス
	 */
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
