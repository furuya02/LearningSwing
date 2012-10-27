package bjd.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;

import bjd.Kernel;
import bjd.ThreadBase;
import bjd.ValidObjException;
import bjd.acl.AclList;
import bjd.ctrl.CtrlType;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.log.OneLog;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.OperateCrlf;
import bjd.net.ProtocolKind;
import bjd.net.Ssl;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OptionSample;
import bjd.sock.SockObj;
import bjd.sock.SockServer;
import bjd.sock.SockState;
import bjd.sock.SockTcp;
import bjd.sock.SockUdp;
import bjd.util.Util;

//各サーバオブジェクトの基底クラス
//****************************************************************
// OneServer １つのバインドアドレス：ポートごとにサーバを表現するクラス
//****************************************************************
public abstract class OneServer extends ThreadBase {

	private OneBind oneBind;
	private AclList aclList;
	private Ssl ssl = null;
	private Logger logger;
	private Conf conf;
	private SockServer sockServer = null;
	private int timeout;

	protected final Conf getConf() {
		return conf;
	}
	
	public abstract String getMsg(int messageNo);


	//子スレッド管理
	private Object lock = new Object(); //排他制御用オブジェクト
	private ArrayList<Thread> childThreads = new ArrayList<Thread>();
	private int multiple; //同時接続数

	//ステータス表示用
	@Override
	public final String toString() {
		String stat = getJp() ? "+ サービス中 " : "+ In execution ";
		if (!isRunnig()) {
			stat = getJp() ? "- 停止 " : "- Initialization failure ";
		}
		return String.format("%s\t%20s\t[%s\t:%s %s]\tThread %d/%d", stat, getNameTag(), oneBind.getAddr(), oneBind.getProtocol().toString().toUpperCase(),
				(int) conf.get("port"), count(), multiple);
	}

	public final int count() {
		//チャイルドスレッドオブジェクトの整理
		for (int i = childThreads.size() - 1; i >= 0; i--) {
			if (!childThreads.get(i).isAlive()) {
				childThreads.remove(i);
			}
		}
		return childThreads.size();
	}

	//リモート操作(データの取得)
	public final String cmd(String cmdStr) {
		return "";
	}

	public final SockState getSockState() {
		if (sockServer == null) {
			return SockState.Error;
		}
		return sockServer.getSockState();
	}

	//コンストラクタ
	protected OneServer(Kernel kernel, String nameTag, Conf conf, OneBind oneBind) {
		super(kernel, nameTag);

		this.conf = conf;
		this.oneBind = oneBind;
		//DEBUG用
		if (this.conf == null) {
			OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
			this.conf = new Conf(optionSample);
			this.conf.set("port", 9990);
			this.conf.set("multiple", 10);
			this.conf.set("acl", new Dat(new CtrlType[0]));
			this.conf.set("enableAcl", 1);
			this.conf.set("timeOut", 3);
		}
		//DEBUG用
		if (this.oneBind == null) {
			Ip ip = null;
			try {
				ip = new Ip("127.0.0.1");
			} catch (ValidObjException ex) {
				//127.0.0.1で例外となるようなら実行時例外とするしかない
				Util.runtimeError("new Ip(127.0.0.1)");
			}
			this.oneBind = new OneBind(ip, ProtocolKind.Tcp);
		}

		this.logger = kernel.createLogger(nameTag, (boolean) this.conf.get("useDetailsLog"), this);
		multiple = (int) this.conf.get("multiple");

		//ACLリスト
		try {
			//定義が存在するかどうかのチェック
			Dat acl = (Dat) this.conf.get("acl");
			aclList = new AclList(acl, (int) this.conf.get("enableAcl"), logger);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		timeout = (int) this.conf.get("timeOut");
	}

	@Override
	public final void start() {

		super.start();

		//bindが完了するまで待機する
		while (sockServer == null || sockServer.getSockState() == SockState.IDLE) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public final void stop() {
		if (sockServer == null) {
			return; //すでに終了処理が終わっている
		}
		super.stop(); //life=false ですべてのループを解除する
		sockServer.close();

		// 全部の子スレッドが終了するのを待つ
		while (count() > 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sockServer = null;

	}

	@Override
	public final void dispose() {
		// super.dispose()は、ThreadBaseでstop()が呼ばれるだけなので必要ない
		stop();
	}

	//スレッド停止処理
	protected abstract void onStopServer(); //スレッド停止処理

	@Override
	protected final void onStopThread() {
		onStopServer(); //子クラスのスレッド停止処理
		if (ssl != null) {
			ssl.dispose();
		}
	}

	//スレッド開始処理
	//サーバが正常に起動できる場合(isInitSuccess==true)のみスレッド開始できる
	protected abstract boolean onStartServer(); //スレッド開始処理

	@Override
	protected final boolean onStartThread() {
		return onStartServer(); //子クラスのスレッド開始処理
	}

	@Override
	protected final void onRunThread() {

		int port = (int) conf.get("port");
		String bindStr = String.format("%s:%d %s", oneBind.getAddr(), port, oneBind.getProtocol());
		logger.set(LogKind.NORMAL, (SockObj) null, 9000000, bindStr);

		//DOSを受けた場合、multiple数まで連続アクセスまでは記憶してしまう
		//DOSが終わった後も、その分だけ復帰に時間を要する

		sockServer = new SockServer(oneBind.getProtocol());

		if (sockServer.getSockState() != SockState.Error) {
			if (sockServer.getProtocolKind() == ProtocolKind.Tcp) {
				runTcpServer(port);
			} else {
				runUdpServer(port);
			}
		}
	}

	private void runTcpServer(int port) {

		int listenMax = 5;

		if (!sockServer.bind(oneBind.getAddr(), port, listenMax)) {
			System.out.println(String.format("bind()=false %s", sockServer.getLastEror()));
		} else {
			while (isLife()) {
				final SockTcp child = (SockTcp) sockServer.select(this);
				if (child == null) {
					break;
				}
				if (count() >= multiple) {
					logger.set(LogKind.SECURE, sockServer, 9000004, String.format("count:%d/multiple:%d", count(), multiple));
					//同時接続数を超えたのでリクエストをキャンセルします
					child.close();
					continue;
				}
				//***************************************************************
				// ACL制限のチェック
				//***************************************************************
				//					if (aclList != null) {
				//						if (!aclList.check(new Ip(sockObj.getRemoteAddress().getAddress().toString()))) {
				//							child.close();
				//							denyAddress = sockObj.getRemoteAddress().getAddress().toString();
				//							continue;
				//						}
				//					}

				synchronized (lock) {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							subThread((SockTcp) child);
						}
					});
					t.start();
					childThreads.add(t);
				}
			}

		}
	}

	private void runUdpServer(int port) {


		if (!sockServer.bind(oneBind.getAddr(), port)) {
			System.out.println(String.format("bind()=false %s", sockServer.getLastEror()));
		} else {

			while (isLife()) {
				final SockUdp child = (SockUdp) sockServer.select(this);
				if (child == null) {
					break;
				}
				if (count() >= multiple) {
					logger.set(LogKind.SECURE, sockServer, 9000004, String.format("count:%d/multiple:%d", count(), multiple));
					//同時接続数を超えたのでリクエストをキャンセルします
					child.close();
					continue;
				}
				//***************************************************************
				// ACL制限のチェック
				//***************************************************************
				//					if (aclList != null) {
				//						if (!aclList.check(new Ip(sockObj.getRemoteAddress().getAddress().toString()))) {
				//							child.close();
				//							denyAddress = sockObj.getRemoteAddress().getAddress().toString();
				//							continue;
				//						}
				//					}

				synchronized (lock) {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							subThread((SockUdp) child);
						}
					});
					t.start();
					childThreads.add(t);
				}
			}

		}
	}

	protected abstract void onSubThread(SockObj sockObj);

	private String denyAddress = ""; //Ver5.3.5 DoS対処

	//１リクエストに対する子スレッドとして起動される
	public final void subThread(SockObj sockObj) {

		try {

			//クライアントのホスト名を逆引きする
			sockObj.resolve((boolean) conf.get("useResolve"), logger);

			//_subThreadの中でSockObjは破棄する（ただしUDPの場合は、クローンなのでClose()してもsocketは破棄されない）
			logger.set(LogKind.DETAIL, sockObj, 9000002,
					String.format("count=%d Local=%s Remote=%s", count(), sockObj.getLocalAddress().toString(), sockObj.getRemoteAddress().toString()));

			onSubThread(sockObj); //接続単位の処理
			sockObj.close();

			logger.set(LogKind.DETAIL, sockObj, 9000003,
					String.format("count=%d Local=%s Remote=%s", count(), sockObj.getLocalAddress().toString(), sockObj.getRemoteAddress().toString()));

		} catch (Exception ex) {
			logger.set(LogKind.ERROR, sockObj, 9000037, ex.getMessage());
			logger.exception(ex);
		}
	}

	//RemoteServerでのみ使用される
	//public virtual void Append(OneLog oneLog){}

	public final Cmd waitLine(SockTcp sockTcp) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, timeout);

		while (isLife()) {
			Cmd cmd = recvCmd(sockTcp);
			if (cmd == null) {
				return null;
			}
			if (!(cmd.getCmdStr().equals(""))) {
				return cmd;
			}
			if (c.compareTo(Calendar.getInstance()) < 0) {
				return null;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	//TODO RecvCmdのパラメータ形式を変更するが、これは、後ほど、Web,Ftp,SmtpのServerで使用されているため影響がでる予定
	protected final Cmd recvCmd(SockTcp sockTcp) {
		if (sockTcp.getSockState() != SockState.CONNECT) { //切断されている
			return null;
		}
		byte[] recvbuf = sockTcp.lineRecv(timeout, OperateCrlf.Yes, this);
		if (recvbuf == null) {
			return null; //切断された
		}
		String str = new String(recvbuf, Charset.forName("Shift-JIS"));
		if (str.equals("")) {
			return null;
		}
		//受信行をコマンドとパラメータに分解する（コマンドとパラメータは１つ以上のスペースで区切られている）
		String cmdStr = null;
		String paramStr = null;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ') {
				if (cmdStr == null) {
					cmdStr = str.substring(0, i);
				}
			}
			if (cmdStr == null || str.charAt(i) == ' ') {
				continue;
			}
			paramStr = str.substring(i);
			break;
		}
		if (cmdStr == null) { //パラメータ区切りが見つからなかった場合
			cmdStr = str; //全部コマンド
		}
		return new Cmd(str, cmdStr, paramStr);
	}

	//受信したコマンドを表現するクラス
	class Cmd {
		private String str;
		private String cmdStr;
		private String paramStr;

		public String getStr() {
			return str;
		}

		public String getCmdStr() {
			return cmdStr;
		}

		public String getParamStr() {
			return paramStr;
		}

		public Cmd(String str, String cmdStr, String paramStr) {
			this.str = str;
			this.cmdStr = cmdStr;
			this.paramStr = paramStr;
		}
	}

	//未実装
	public void append(OneLog oneLog) {

	}
}
