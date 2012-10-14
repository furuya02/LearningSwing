package bjd.net;

import java.util.ArrayList;

import bjd.util.Util;

/**
 * バインドアドレスを表現するクラス
 * 
 * @author SIN
 *
 */
public final class BindAddr {
	private Ip ipV4;
	private Ip ipV6;
	private BindStyle bindStyle;
	/**
	 * 所為化に失敗するとtrueに設定される
	 * trueの時に、このオブジェクトを使用すると実行時例外が発生する
	 */
	private boolean initialiseFailed = false; //初期化失敗

	/**
	 * IPv4アドレスの取得
	 * @return IpV4アドレス
	 */
	public Ip getIpV4() {
		checkInitialise();
		return ipV4;
	}

	/**
	 * IPv6アドレスの取得
	 * @return IpV6アドレス
	 */
	public Ip getIpV6() {
		checkInitialise();
		return ipV6;
	}

	/**
	 * バインド方法の取得
	 * @return バインドスタイル
	 */
	public BindStyle getBindStyle() {
		checkInitialise();
		return bindStyle;
	}

	/**
	 * デフォルト値の初期化<br>
	 * IPv4=INADDR_ANY<br>
	 * IPv6=IN6ADDR_ANY_INIT<br>
	 * ディアルバインド<br>
	 */
	private void init() {
		bindStyle = BindStyle.V4ONLY;
		ipV4 = new Ip("INADDR_ANY");
		ipV6 = new Ip("IN6ADDR_ANY_INIT");
	}

	/**
	 * コンストラクタ<br>
	 * すべてデフォルト値で初期化される
	 */
	public BindAddr() {
		init(); // デフォルト値での初期化
	}

	/**
	 * コンストラクタ
	 * @param bindStyle バインド方法
	 * @param ipV4 V4アドレス
	 * @param ipV6 V6アドレス
	 */
	public BindAddr(BindStyle bindStyle, Ip ipV4, Ip ipV6) {
		this.bindStyle = bindStyle;
		this.ipV4 = ipV4;
		this.ipV6 = ipV6;
	}

	/**
	 * コンストラクタ(文字列指定)
	 * 文字列が無効で初期化に失敗した場合は、例外(IllegalArgumentException)がスローされる<br>
	 * 初期化に失敗したオブジェクトを使用すると「実行時例外」が発生するので、生成時に必ず例外処理しなければならない<br>
	 * 
	 * @param str　指定文字列
	 */
	public BindAddr(String str) throws IllegalArgumentException {
		if (str == null) {
			throwException(str); //初期化失敗
		}

		String[] tmp = str.split(",");
		if (tmp.length != 3) {
			throwException(str); //初期化失敗
		}

		if (tmp[0].equals("V4_ONLY") || tmp[0].equals("V4Only") || tmp[0].equals("V4ONLY")) {
			tmp[0] = "V4ONLY";
		} else if (tmp[0].equals("V6_ONLY") || tmp[0].equals("V6Only") || tmp[0].equals("V6ONLY")) {
			tmp[0] = "V6ONLY";
		} else if (tmp[0].equals("V46_DUAL") || tmp[0].equals("V46Dual") || tmp[0].equals("V46DUAL")) {
			tmp[0] = "V46DUAL";
		} else {
			throwException(str); //初期化失敗
		}

		try {
			bindStyle = BindStyle.valueOf(tmp[0]);
			ipV4 = new Ip(tmp[1]);
			ipV6 = new Ip(tmp[2]);
		} catch (Exception ex) {
			throwException(str); //初期化失敗
		}
		if (ipV4.getInetKind() != InetKind.V4) {
			throwException(str); //初期化失敗
		}
		if (ipV6.getInetKind() != InetKind.V6) {
			throwException(str); //初期化失敗
		}

	}

	/**
	 * 文字列化
	 */
	@Override
	public String toString() {
		checkInitialise();
		return String.format("%s,%s,%s", bindStyle, ipV4, ipV6);
	}

	/**
	 * 比較
	 */
	@Override
	public boolean equals(Object o) {
		checkInitialise();
		// 非NULL及び型の確認
		if (o == null || !(o instanceof BindAddr)) {
			return false;
		}
		BindAddr b = (BindAddr) o;

		if (bindStyle == b.getBindStyle()) {
			if (ipV4.equals(b.getIpV4())) {
				if (ipV6.equals(b.getIpV6())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		assert false : "Use is not assumed.";
		return 101;
	}

	/**
	 * プロトコルを指定してOneBindの配列を取得<br>
	 * 取得した配列分だけインターフェースへのbindが必要となる
	 * 
	 * @param protocolKind TCP/UDP
	 * @return　OneBind配列
	 */
	public OneBind[] createOneBind(ProtocolKind protocolKind) {
		checkInitialise();
		ArrayList<OneBind> ar = new ArrayList<>();
		if (bindStyle != BindStyle.V4ONLY) {
			ar.add(new OneBind(ipV6, protocolKind));
		}
		if (bindStyle != BindStyle.V6ONLY) {
			ar.add(new OneBind(ipV4, protocolKind));
		}
		return ar.toArray(new OneBind[0]);
	}

	/**
	 * 競合があるかどうかの確認
	 * @param b 比較するBindAddr
	 * @return 競合ありの場合、ｔrue
	 */
	public boolean checkCompetition(BindAddr b) {
		checkInitialise();
		boolean v4Competition = false; // V4競合の可能性
		boolean v6Competition = false; // V6競合の可能性
		switch (bindStyle) {
			case V46DUAL:
				if (b.getBindStyle().equals(BindStyle.V46DUAL)) {
					v4Competition = true;
					v6Competition = true;
				} else if (b.getBindStyle().equals(BindStyle.V4ONLY)) {
					v4Competition = true;
				} else {
					v6Competition = true;
				}
				break;
			case V4ONLY:
				if (!b.getBindStyle().equals(BindStyle.V6ONLY)) {
					v4Competition = true;
				}
				break;
			case V6ONLY:
				if (!b.getBindStyle().equals(BindStyle.V4ONLY)) {
					v6Competition = true;
				}
				break;
			default:
				break;
		}

		//V4競合の可能性がある場合
		if (v4Competition) {
			// どちらかがANYの場合は、競合している
			if (ipV4.getAny() || b.getIpV4().getAny()) {
				return true;
			}
			if (ipV4.equals(b.getIpV4())) {
				return true;
			}
		}
		// V6競合の可能性がある場合
		if (v6Competition) {
			// どちらかがANYの場合は、競合している
			if (ipV6.getAny() || b.ipV6.getAny()) {
				return true;
			}
			if (ipV6.equals(b.getIpV6())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * コンストラクタで初期化に失敗した時に使用する<br>
	 * 内部変数は初期化され例外（IllegalArgumentException）がスローされる<br>
	 * @param ipStr 初期化文字列
	 * @throws IllegalArgumentException 
	 */
	private void throwException(String ipStr) {
		initialiseFailed = true; //初期化失敗
		init(); // デフォルト値での初期化
		throw new IllegalArgumentException(String.format("引数が不正です \"%s\"", ipStr));
	}

	/**
	 * 初期化成否のチェック<br>
	 * 初期化が失敗している場合は、実行時例外が発生する<br>
	 * 全ての公開メソッドで使用される<br>
	 */
	private void checkInitialise() {
		if (initialiseFailed) {
			Util.runtimeError("このオブジェクト(Ip)は、初期化に失敗るため使用することができません");
		}
	}

}
