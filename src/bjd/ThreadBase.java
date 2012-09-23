package bjd;

import bjd.log.ILogger;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.util.IDispose;

//スレッドの起動停止機能を持った基本クラス
public abstract class ThreadBase implements IDispose, ILogger {
	private MyThread myThread = null;
	private boolean runnig = false;
	protected boolean life;
	protected Kernel kernel;

	private Logger logger;
	private String nameTag;

	//コンストラクタ
	protected ThreadBase(Kernel kernel, String nameTag) {
		this.kernel = kernel;
		this.nameTag = nameTag;
		logger = kernel.createLogger(nameTag, true, this);
	}

	public final String getNameTag() {
		return nameTag;
	}

	public final boolean isRunnig() {
		return runnig;
	}

	@Override
	public void dispose() {
	}

	public final String getMsg(int messageNo) {
		switch (messageNo) {
			case 1:
				return (kernel.getJp()) ? "ThreadBase::loop()で例外が発生しました" : "An exception occurred in ThreadBase::Loop()";
			default:
				break;
		}
		return "unknown";
	}

	//【スレッド開始前処理】//return falseでスレッド起動をやめる
	protected abstract boolean onStartThread();

	public final void start() {
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

	//【スレッドループ】
	protected abstract void onLoopThread();

	private void loop() {
		onLoopThread();
	}

	//【スレッド終了処理】
	protected abstract void onStopThread();

	public final void stop() {
		if (isRunnig()) { //起動されている場合
			life = false; //スイッチを切るとLoop内の無限ループからbreakする
			while (isRunnig()) { //stop()を抜けた時点でisRunnigがfalseになるように、処理が終了するまで待つ
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		onStopThread();
		myThread = null;
	}

	class MyThread extends Thread {
		@Override
		public void run() {
			runnig = true;
			try {
				loop();
			} catch (Exception ex) {
				logger.set(LogKind.Error, null, 1, ex.getMessage());
				logger.exception(ex);
			}
			//life = true;//Stop()でスレッドを停止する時、life=falseでループから離脱させ、このlife=trueで処理終了を認知する
			runnig = false;
			kernel.getView().setColor();
		}
	}
}
