package bjd;

import bjd.log.ILogger;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.util.IDispose;

/**
 * スレッドの起動停止機能を持った基本クラス
 * 
 * @author SIN
 *
 */
public abstract class ThreadBase implements IDispose, ILogger {
//	private Kernel kernel;
	private MyThread myThread = null;
	private boolean runnig = false;
	private boolean life;
//	private String nameTag;
	private Logger logger;

	//protected ThreadBase(Kernel kernel, String nameTag) {
	/**
	 * 
	 * @param logger　スレッド実行中に例外がスローされたとき表示するためのLogger(nullを設定可能)
	 */
	protected ThreadBase(Logger logger) {
		this.logger = logger;
//		this.kernel = kernel;
//		this.nameTag = nameTag;
//		logger = kernel.createLogger(nameTag, true, this);
	}

//	protected final boolean getJp() {
//		return kernel.getJp();
//	}
//
//	public final String getNameTag() {
//		return nameTag;
//	}

	/**
	 * スレッドが動作中かどうか
	 * @return
	 */
	public final boolean isRunnig() {
		return runnig;
	}

	/**
	 * 時間を要するループがある場合、ループ条件で値がtrueであることを確認する<br>
	 * falseになったら直ちにループを中断する
	 * @return life
	 */
	public final boolean isLife() {
		return life;
	}

	/**
	 * 終了処理<br>
	 * Override可能<br>
	 */
	public void dispose() {
		stop();
	}

	//【スレッド開始前処理】//return falseでスレッド起動をやめる
	protected abstract boolean onStartThread();

	/**
	 * 	開始処理<br>
	 * Override可能<br>
	 */
	public void start() {
		if (isRunnig()) {
			return;
		}
		if (!onStartThread()) {
			return;
		}
		try {
			life = true;
			myThread = new MyThread();
			myThread.start();
			while (!isRunnig()) { //start()を抜けた時点でisRunnigがtrueになるように、スレッド処理を待つ
				Thread.sleep(10);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 【スレッド終了処理】
	 */
	protected abstract void onStopThread();

	/**
	 * 	停止処理<br>
	 * Override可能<br>
	 */
	public void stop() {
		life = false; //スイッチを切るとLoop内の無限ループからbreakする
		while (isRunnig()) { //stop()を抜けた時点でisRunnigがfalseになるように、処理が終了するまで待つ
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		onStopThread();
		myThread = null;
	}

	/**
	 * 【スレッドループ】
	 */
	protected abstract void onRunThread();

	private class MyThread extends Thread {
		@Override
		public void run() {
			runnig = true;
			try {
				onRunThread();
			} catch (Exception ex) {
				if (logger != null) {
					logger.set(LogKind.ERROR, null, 9000021, ex.getMessage());
					logger.exception(ex);
				}
			}
			//	kernel.getView().setColor();
			runnig = false;
		}
	}
}
