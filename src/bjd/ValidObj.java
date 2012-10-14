package bjd;

import bjd.util.Util;

/**
 * 文字列を受け取って初期化されるオブジェクトの基底クラス<br>
 * 
 * 継承クラス  Ip Mac BindAddr
 * @author SIN
 *
 */
public abstract class ValidObj {
	/**
	 * 所為化に失敗するとtrueに設定される
	 * trueの時に、このオブジェクトを使用すると実行時例外が発生する
	 */
	private boolean initialiseFailed = false; //初期化失敗
	
	
	protected abstract void init();

	/**
	 * コンストラクタで初期化に失敗した時に使用する<br>
	 * 内部変数は初期化され例外（IllegalArgumentException）がスローされる<br>
	 * @param ipStr 初期化文字列
	 * @throws IllegalArgumentException 
	 */
	protected final void throwException(String ipStr) {
		initialiseFailed = true; //初期化失敗
		init(); // デフォルト値での初期化
		//throw new Exception(String.format("引数が不正です \"%s\"", ipStr)); 
		throw new IllegalArgumentException(String.format("引数が不正です \"%s\"", ipStr));
	}

	/**
	 * 初期化成否のチェック<br>
	 * 初期化が失敗している場合は、実行時例外が発生する<br>
	 * 全ての公開メソッドで使用される<br>
	 */
	protected final void checkInitialise() {
		if (initialiseFailed) {
			Util.runtimeError("このオブジェクト(Ip)は、初期化に失敗るため使用することができません");
		}
	}
}
