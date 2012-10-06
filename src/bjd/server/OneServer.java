package bjd.server;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;

import bjd.Kernel;
import bjd.ThreadBase;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.OperateCrlf;
import bjd.net.ProtocolKind;
import bjd.acl.AclList;
import bjd.net.Ssl;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneOption;
import bjd.server.OneServer2.Cmd;
import bjd.sock.SockAccept;
import bjd.sock.SockObj;
import bjd.sock.SockState;
import bjd.sock.TcpObj;
import bjd.sock.UdpObj;
import bjd.util.Debug;

//各サーバオブジェクトの基底クラス
//****************************************************************
// OneServer １つのバインドアドレス：ポートごとにサーバを表現するクラス
//****************************************************************
public abstract class OneServer extends ThreadBase {

	protected OneBind oneBind;

	protected AclList aclList;
	protected Ssl ssl;
	private Logger logger;
	private Conf conf;
	private SockObj sockObj = null;

	private boolean isBusy; //排他制御

	public abstract String getMsg(int messageNo);

	protected int timeout;

	//子スレッド管理
	private Object lock = new Object(); //排他制御用オブジェクト
	private ArrayList<Thread> childThreads = new ArrayList<Thread>();
	private int multiple; //同時接続数

	//ステータス表示用
	@Override
	public String toString() {
		String stat = kernel.getJp() ? "+ サービス中 " : "+ In execution ";
		if (!isRunnig()) {
			stat = kernel.getJp() ? "- 停止 " : "- Initialization failure ";
		}
		return String.format("%s\t%20s\t[%s\t:%s %s]\tThread %d/%d", stat, nameTag, oneBind.getAddr(), oneBind.getProtocol().toString().toUpperCase(),
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
	public String cmd(String cmdStr) {
		return "";
	}

	public final SockState getSockState() {
		return sockObj.getSockState();
	}

	//コンストラクタ
	protected OneServer(Kernel kernel, String nameTag, Conf conf, OneBind oneBind) {
		super(kernel, nameTag);

		this.conf = conf;
		this.oneBind = oneBind;
		this.logger = kernel.createLogger(nameTag, (boolean) conf.get("useDetailsLog"), this);

		multiple = (int) conf.get("multiple");

		//ACLリスト
		try {
			//定義が存在するかどうかのチェック
			Dat acl = (Dat) conf.get("acl");
			aclList = new AclList(acl, (int) conf.get("enableAcl"), logger);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		timeout = (int) conf.get("timeOut");
	}
	
	@Override
	public final void start() {
		super.start();
		//sockStateがBind若しくはエラーとなるまで待機する
		while (sockObj == null || sockObj.getSockState() == SockState.Idle) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public final void stop() {
		sockObj.close();
		super.stop();

		// 全部の子スレッドが終了するのを待つ
		while (count() > 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public final void dispose() {
		// super.dispose()は、ThreadBaseでstop()が呼ばれるだけなので必要ない
		stop();
	}




	//スレッド停止処理
	protected abstract void onStopServer(); //スレッド停止処理

	@Override
	protected void onStopThread() {
		onStopServer(); //子クラスのスレッド停止処理
		if (ssl != null) {
			ssl.dispose();
		}
	}

	//スレッド開始処理
	//サーバが正常に起動できる場合(isInitSuccess==true)のみスレッド開始できる
	protected abstract boolean onStartServer(); //スレッド開始処理

	@Override
	protected boolean onStartThread() {
		return onStartServer(); //子クラスのスレッド開始処理
	}

	@Override
	protected void onRunThread() {
		
		int port = (int) conf.get("port");
		String bindStr = String.format("%s:%d %s", oneBind.getAddr(), port, oneBind.getProtocol());
		logger.set(LogKind.Normal, (SockObj) null, 9000000, bindStr);

		//DOSを受けた場合、multiple数まで連続アクセスまでは記憶してしまう
		//DOSが終わった後も、その分だけ復帰に時間を要する

		sockObj = (oneBind.getProtocol() == ProtocolKind.Tcp) ? (SockObj) new TcpObj() : new UdpObj(InetKind.V4);


		if (sockObj.getSockState() != SockState.Error) {
			TcpObj tcpObj = (TcpObj) sockObj;

			int listenMax = 5;
			if(!tcpObj.bind(oneBind.getAddr(), port, listenMax)){
				//TODO Debug Print
				System.out.println(String.format("bind()=false %s",tcpObj.getLastEror()));
			}else{
				while (isLife()) {
					final TcpObj child = tcpObj.select(this);
					if (child == null) {
						break;
					}
					if (count() >= multiple) {
						logger.set(LogKind.Secure, sockObj, 9000004, String.format("count:%d/multiple:%d", count(), multiple));
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
								subThread((TcpObj) child);
							}
						});
						t.start();
						childThreads.add(t);
					}
				}

			}
		}
	}



	protected abstract void onSubThread(SockObj sockObj);

	private String denyAddress = ""; //Ver5.3.5 DoS対処

	//１リクエストに対する子スレッドとして起動される
	public void subThread(SockObj sockObj) {

		try {

			//クライアントのホスト名を逆引きする
			sockObj.resolve((boolean) conf.get("useResolve"), logger);

			//_subThreadの中でSockObjは破棄する（ただしUDPの場合は、クローンなのでClose()してもsocketは破棄されない）
			logger.set(LogKind.Detail, sockObj, 9000002,
					String.format("count={0} Local={1} Remote={2}", count(), sockObj.getLocalAddress().toString(),
							sockObj.getRemoteAddress().toString()));

			onSubThread(sockObj); //接続単位の処理
			sockObj.close();

			System.out.println(String.format("subThread() sock.close()"));

			logger.set(LogKind.Detail, sockObj, 9000003,
					String.format("count=%d Local=%s Remote=%s", count(), sockObj.getLocalAddress().toString(),
							sockObj.getRemoteAddress().toString()));

		} catch (Exception ex) {
			logger.set(LogKind.Error, sockObj, 9000037, ex.getMessage());
			logger.exception(ex);
		}
	}

	//RemoteServerでのみ使用される
	//public virtual void Append(OneLog oneLog){}

	public final Cmd waitLine(SockAccept sockAccept) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, timeout);

		while (isLife()) {
			Cmd cmd = recvCmd(sockAccept);
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
	protected final Cmd recvCmd(SockAccept sockAccept) {
		if (sockAccept.getSockState() != SockState.Connect) { //切断されている
			return null;
		}
		byte[] recvbuf = sockAccept.lineRecv(timeout, OperateCrlf.Yes, this);
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
}
