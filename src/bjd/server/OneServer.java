package bjd.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;

import bjd.Kernel;
import bjd.ThreadBase;
import bjd.TraceKind;
import bjd.acl.AclList;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.log.OneLog;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.OperateCrlf;
import bjd.net.ProtocolKind;
import bjd.net.SockObj;
import bjd.net.SocketObjState;
import bjd.net.Ssl;
import bjd.net.TcpObj;
import bjd.net.UdpObj;
import bjd.option.Dat;
import bjd.option.OneOption;
import bjd.util.IDispose;

//各サーバオブジェクトの基底クラス
//****************************************************************
// OneServer １つのバインドアドレス：ポートごとにサーバを表現するクラス
//****************************************************************
public abstract class OneServer extends ThreadBase implements IDispose{

	protected OneBind oneBind;
	protected AclList aclList;
	protected Ssl ssl;
	protected Logger logger;
	protected OneOption oneOption;

	private boolean busy; //排他制御

	public abstract String ｇetMsg(int messageNo);

	protected int Timeout;

	//子スレッド管理
	private Object lock = new Object(); //排他制御用オブジェクト

	private  ArrayList<Thread> childThreads = new ArrayList<Thread>();
	private int childCount; //多重度のカウンタ
	private  int multiple; //同時接続数


	private String nameTag;

	public String getNameTag() {
		return nameTag;
	}

	@Override
	public void dispose() {
		// TODO 自動生成されたメソッド・スタブ

	}

	//RemoteServerでのみ使用される
	public void append(OneLog oneLog){}

	public void addTrace(TraceKind traceKind, String str, Ip ip) {
		// TODO Auto-generated method stub

	}

	//ステータス表示用
	@Override
	public String toString(){
		String stat = (kernel.getJp()) ? "+ サービス中 " : "+ In execution ";
		if (!isRunnig()){
			stat = (kernel.getJp()) ? "- 停止 " : "- Initialization failure ";
		}
		return String.format("%s\t%-20s\t[%s\t:%s %s]\tThread %d/%d", stat, nameTag, oneBind.getAddr(),
				oneBind.getProtocol().toString().toUpperCase(), (int) oneOption.getValue("port"), childCount,multiple);
	}

	//リモート操作(データの取得)
	public String Cmd(String cmdStr){
		return "";
	}

	//コンストラクタ
	protected OneServer(Kernel kernel, String nameTag, OneBind oneBind){
		super(kernel,nameTag);

		oneOption = kernel.getListOption().get(nameTag);
		this.oneBind = oneBind;
		logger = kernel.createLogger(nameTag, (boolean) oneOption.getValue("useDetailsLog"),this);

		multiple = (int) oneOption.getValue("multiple");

		//ACLリスト
		try{
			//定義が存在するかどうかのチェック
			Dat acl = (Dat) oneOption.getValue("acl");
			aclList = new AclList(acl, (int) oneOption.getValue("enableAcl"), logger);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		Timeout = (int) oneOption.getValue("timeOut");
	}

	//サーバ停止処理
	protected abstract void onStopServer(); //サーバ停止処理

	@Override
	protected void onStopThread(){
		onStopServer(); //子クラスのスレッド停止処理
		if (ssl != null){
			ssl.dispose();
		}
	}

	//サーバ開始処理
	//サーバが正常に起動できる場合(isInitSuccess==true)のみスレッド開始できる
	protected abstract boolean onStartServer(); //サーバ開始処理

	@Override
	protected boolean onStartThread(){
		return onStartServer(); //子クラスのスレッド開始処理
	}

	@Override
	protected void onLoopThread(){

		int port = (int) oneOption.getValue("port");

		String bindStr = String.format("%s:%d %s", oneBind.getAddr(), port, oneBind.getProtocol());

		logger.set(LogKind.Normal, null, 9000000, bindStr);

		//DOSを受けた場合、multiple数まで連続アクセスまでは記憶してしまう
		//DOSが終わった後も、その分だけ復帰に時間を要する

		SockObj sockObj = oneBind.getProtocol() == ProtocolKind.Tcp	
				? new TcpObj(kernel, logger, oneBind.getAddr(), port, multiple, ssl)
		: new UdpObj(kernel, logger, oneBind.getAddr(), port, multiple);

				if (sockObj.getState() == SocketObjState.Error){
				    Thread.sleep(1000); //このウエイトが無いと応答不能になる
				}else{
				    busy = false; //排他制御
				    while (life){
				        while (busy){
				            if (!life){
				                break;
				            }
				            Thread.sleep(10);
				        }
				        //callBack関数の中で、子オブジェクトを作成し、作成完了すると排他制御が解除される
				        busy = true;
				        sockObj.StartServer(CallBackFunc);

				        //チャイルドスレッドオブジェクトの整理
				        synchronized (lock) {
				            for (int i = childThreads.size() - 1; i >= 0; i--){
				                if (childThreads.get(i).isAlive()){
				                    continue;
				                }
				                //childThreads.get(i) = null;
				                childThreads.remove(i);
				            }
				        }
				    }
				}
				sockObj.close();

				while (childCount != 0){
					Thread.sleep(100);
					//サーバ停止でハングアップして、中断をかけた時ここにくる場合、動作中の子プロセスが終了していない
					//ここで、opBase.nameTagを確認すれば、何のサーバプロセスが動作中かどうかが分かる
				}
				logger.set(LogKind.Normal, null, 9000001, bindStr);
	}

	public void CallBackFunc(IAsyncResult ar){
		SockObj sockObj = ((SockObj) (ar.AsyncState)).CreateChildObj(ar);
		busy = false; //排他制御解除
		if (sockObj != null){
			if (childCount >= multiple){
				logger.set(LogKind.Secure, sockObj, 9000004,String.format("count:%d/multiple:%d", childCount, multiple));
				//同時接続数を超えたのでリクエストをキャンセルします
				sockObj.close(); //2009.06.04
			}
			else{
				//Ver5.3.5
				if (sockObj.getRemoteEndPoint().getAddress().toString().equals(_denyAddress)){
					logger.set(LogKind.Secure, null, 9000016, String.format("address:%s", _denyAddress));
					//このアドレスからのリクエストは許可されていません
					Thread.sleep(100);
					sockObj.close();
				}
				else{

					synchronized (lock) {
						Thread t = new Thread(SubThread){IsBackground = true};
						t.start(sockObj);
						childThreads.add(t);
					}
				}
			}
		}

	}

	protected abstract void onSubThread(SockObj sockObj);

	private String _denyAddress = ""; //Ver5.3.5 DoS対処

	//１リクエストに対する子スレッドとして起動される
	public void SubThread(Object o){
		childCount++; //多重度のカウンタ

		SockObj sockObj = (SockObj) o;

		try{
			//***************************************************************
			// ACL制限のチェック
			//***************************************************************
			if (aclList != null){
				if (!aclList.check(new Ip(sockObj.getRemoteEndPoint().getAddress().toString()))){
					sockObj.close();
					_denyAddress = sockObj.getRemoteEndPoint().getAddress().toString();
					childCount--; //多重度のカウンタ
					return;
				}
			}

			//クライアントのホスト名を逆引きする
			sockObj.resolve((boolean) oneOption.getValue("useResolve"), logger);



			//_subThreadの中でSockObjは破棄する（ただしUDPの場合は、クローンなのでClose()してもsocketは破棄されない）
			logger.set(LogKind.Detail, sockObj, 9000002,
					String.format("count=%d Local=%s Remote=%s", childCount, sockObj.getLocalEndPoint(),sockObj.getRemoteEndPoint()));

			//接続元情報
			//Ip remoteAddr = new Ip(sockObj.RemoteEndPoint.Address.toString());
			//Ver5.4.1 sockObjのプロパティ(RemoteAddr)に変更
			//Ip remoteAddr = new Ip(sockObj.RemoteEndPoint.Address.toString());
			sockObj.RemoteAddr = new Ip(sockObj.getRemoteEndPoint().Address.toString());

			//Ver5.4.1 sockObjのプロパティ(RemoteHost)に変更
			//String remoteHost = sockObj.RemoteHostName;

			//Ver5.4.1
			//_subThread(sockObj, new RemoteInfo(remoteHost, remoteAddr));//接続単位の処理
			onSubThread(sockObj); //接続単位の処理

			sockObj.close();

			logger.set(LogKind.Detail, sockObj, 9000003,
					String.format("count=%d Local=%s Remote=%s", childCount, sockObj.getLocalEndPoint(),sockObj.getRemoteEndPoint()));

		}
		catch (Exception ex){
			logger.set(LogKind.Error, sockObj, 9000037, ex.getMessage());
			logger.exception(ex);
		}
		childCount--; //多重度のカウンタ
	}
	//TODO パラメータ及び戻り値を変更
	//public boolean WaitLine(TcpObj tcpObj, ref String cmdStr, ref String paramStr){
    public Cmd WaitLine(TcpObj tcpObj){
		//var dt = DateTime.Now.AddSeconds(Timeout);
		Calendar c = Calendar.getInstance(); 
		c.add(Calendar.SECOND,Timeout);
		
		while (life){
			Cmd cmd = recvCmd(tcpObj);
		    if (cmd==null){
				return null;
			}
			if (!(cmd.getCmdStr().equals(""))){
				return cmd;
			}
			//if (dt < DateTime.Now){
			if(c.compareTo(Calendar.getInstance())<0){
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
	protected Cmd recvCmd(TcpObj tcpObj){
		if (tcpObj.getState() != SocketObjState.Connect) //切断されている
			return null;
		byte [] recvbuf = tcpObj.LineRecv(Timeout, OperateCrlf.Yes, this);
		if (recvbuf == null){
			return null; //切断された
		}
		String str = new String(recvbuf,Charset.forName("Shift-JIS"));
		if (str == ""){
			return null;
		}
		//受信行をコマンドとパラメータに分解する（コマンドとパラメータは１つ以上のスペースで区切られている）
		String cmdStr = null;
		String paramStr = null;
		for (int i = 0; i < str.length(); i++){
			if (str.charAt(i) == ' '){
				if (cmdStr == null)
					cmdStr = str.substring(0, i);
			}
			if (cmdStr == null || str.charAt(i) == ' '){
				continue;
			}
			paramStr = str.substring(i);
			break;
		}
		if (cmdStr == null){ //パラメータ区切りが見つからなかった場合
			cmdStr = str; //全部コマンド
		}
		return new Cmd(str,cmdStr,paramStr);
	}
	//受信したコマンドを表現するクラス
	class Cmd{
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
	    
        public Cmd(String str,String cmdStr,String paramStr){
            this.str = str;
            this.cmdStr = cmdStr;
            this.paramStr = paramStr;
        }
	}
}



