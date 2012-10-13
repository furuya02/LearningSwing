package bjd.acl;

import bjd.net.Ip;

/**
 * ACLの基底クラス
 * 
 * 開始アドレス Ip start と　終了アドレス　Ｉｐ　ｅｎｄ　を保持している
 * コンストラクタによる初期化に失敗した場合、boolean status がfalseとなる
 * 
 * public変数は、name,start,end,status
 * publicメソッドは、isHit(Ip)
 * 
 * @author SIN
 *
 */
public abstract class Acl {
	private String name;
	private boolean status;
	private Ip start;
	private Ip end;

	public abstract boolean isHit(Ip ip);

	protected Acl(String name) {
		this.name = name;
		this.status = false;
	}

	public final String getName() {
		return name;
	}

	protected final void setStatus(boolean status) {
		this.status = status;
	}

	//public final boolean getStatus() {
	//	return status;
	//}

	protected final void setStart(Ip start) {
		this.start = start;
	}

	public final Ip getStart() {
		return start;
	}

	protected final void setEnd(Ip end) {
		this.end = end;
	}

	public final Ip getEnd() {
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
}
