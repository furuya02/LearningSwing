package bjd;

import bjd.log.ILogger;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.sock.SockBase;
import bjd.util.IDispose;

//スレッドの起動停止機能を持った基本クラス
public abstract class ThreadBase implements IDispose, ILogger, ILife {
	private MyThread myThread = null;
	private boolean runnig = false;
	private boolean life; //ILife
	protected Kernel kernel;

	private Logger logger;
	protected String nameTag;

	//コンストラクタ
	protected ThreadBase(Kernel kernel, String nameTag) {
		this.kernel = kernel;
		this.nameTag = nameTag;
		logger = kernel.createLogger(nameTag, true, this);
	}

	public String getNameTag() {
		return nameTag;
	}

	public final boolean isRunnig() {
		return runnig;
	}

	//時間を要するループがある場合、ループ条件で値がtrueであることを確認する
	//falseになったら直ちにループを中断する
	public boolean isLife() {
		return life;
	}

	@Override
	public void dispose() {
		stop();
	}

	//【スレッド開始前処理】//return falseでスレッド起動をやめる
	protected abstract boolean onStartThread();

	//Override可能
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

	//【スレッド終了処理】
	protected abstract void onStopThread();

	//Override可能
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

	//【スレッドループ】
	protected abstract void onRunThread();

	class MyThread extends Thread {
		@Override
		public void run() {
			runnig = true;
			try {
				onRunThread();
			} catch (Exception ex) {
				logger.set(LogKind.Error, (SockBase) null, 9000021, ex.getMessage());
				logger.exception(ex);
			}
			//	kernel.getView().setColor();
			runnig = false;
		}
	}
}
