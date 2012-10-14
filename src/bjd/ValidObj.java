package bjd;

import bjd.util.Util;

/**
 * コンストラクタで文字列を受け取って初期化されるようなオブジェクトの「実行時例外」と「チェック例外」を処理する基底クラス<br>
 * 
 * 継承クラス  Ip Mac BindAddr　OneLog
 * @author SIN
 *
 */
public abstract class ValidObj {
	/**
	 * 初期化に失敗するとtrueに設定される
	 * trueになっている、このオブジェクトを使用すると「実行時例外」が発生する
	 */
	private boolean initialiseFailed = false; //初期化失敗
	
	
	protected abstract void init();

	/**
	 * コンストラクタで初期化に失敗した時に使用する呼び出す<br>
	 * 内部変数が初期化され例外（IllegalArgumentException）がスローされる<br>
	 * @param paramStr 初期化文字列
	 * @throws IllegalArgumentException 
	 */
	protected final void throwException(String paramStr) {
		initialiseFailed = true; //初期化失敗
		init(); // デフォルト値での初期化
		//throw new Exception(String.format("引数が不正です \"%s\"", ipStr)); 
		throw new IllegalArgumentException(String.format("[ValidObj] 引数が不正です。 \"%s\"", paramStr));
	}

	/**
	 * 初期化が失敗している場合は、実行時例外が発生する<br>
	 * 全ての公開メソッドの最初に挿入する<br>
	 */
	protected final void checkInitialise() {
		if (initialiseFailed) {
			Util.runtimeError("[ValidObj] このオブジェクトは、初期化に失敗しているため使用できません");
		}
	}
}
