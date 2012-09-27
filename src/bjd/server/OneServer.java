package bjd.server;

import java.net.ServerSocket;
import java.nio.channels.SocketChannel;
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
import bjd.net.ISocket;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.OperateCrlf;
import bjd.net.ProtocolKind;
import bjd.net.SockAccept;
import bjd.net.SockServer;
import bjd.net.SockObj;
import bjd.net.SockState;
import bjd.net.Ssl;
import bjd.net.TcpObj;
import bjd.net.UdpObj;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.util.Debug;
import bjd.util.IDispose;

//各サーバオブジェクトの基底クラス
//****************************************************************
// OneServer １つのバインドアドレス：ポートごとにサーバを表現するクラス
//****************************************************************
public abstract class OneServer extends ThreadBase implements ISocket, IDispose {

	private SockServer sockServer = null;

	protected OneBind oneBind;
	protected AclList aclList;
	protected Ssl ssl;
	protected Logger logger;

	protected Conf conf;


	//public abstract String getMsg(int messageNo);

	protected int timeout;

	//子スレッド管理
	private Object lock = new Object(); //排他制御用オブジェクト

	private ArrayList<Thread> childThreads = new ArrayList<Thread>();
	private int multiple; //同時接続数

	private String nameTag;

	public String getNameTag() {
		return nameTag;
	}

	public int count(){
		//チャイルドスレッドオブジェクトの整理
		for (int i = childThreads.size() - 1; i >= 0; i--){
			if (!childThreads.get(i).isAlive()){
				childThreads.remove(i);
				Debug.print(this,String.format("childThreads.remove(%d)",i));
			}
		}
		return childThreads.size(); 
	}
	
	@Override
	public void dispose() {
		super.dispose(); // life=false; これによって、接続中の子スレッドは、全部ループを抜け、終了に向かう

		Debug.print(this,"dispose() start");
		
		while(count()>0){ // 全部の子スレッドが終了するのを待つ
			Debug.print(this,"count()>0");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sockServer.close(); //サーバスレッドを停止する　bind()が終了する
        Debug.print(this,"dispose() end");
	}

	//RemoteServerでのみ使用される
	public void append(OneLog oneLog) {
	}

	public void addTrace(TraceKind traceKind, String str, Ip ip) {
		// TODO Auto-generated method stub

	}

	//ステータス表示用
	@Override
	public String toString() {
		String stat = (kernel.getJp()) ? "+ サービス中 " : "+ In execution ";
		if (!isRunnig()) {
			stat = (kernel.getJp()) ? "- 停止 " : "- Initialization failure ";
		}
		return String.format("%s\t%-20s\t[%s\t:%s %s]\tThread %d/%d", stat, nameTag, oneBind.getAddr(), oneBind.getProtocol().toString().toUpperCase(), (int) conf.get("port"), count(), multiple);
	}

	//リモート操作(データの取得)
	public String Cmd(String cmdStr) {
		return "";
	}

	//コンストラクタ
	protected OneServer(Kernel kernel, String nameTag,  Conf conf, OneBind oneBind) {
		super(kernel, nameTag);
		this.conf = conf;

		this.oneBind = oneBind;
		logger = kernel.createLogger(nameTag, (boolean) conf.get("useDetailsLog"), this);

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

	//サーバ停止処理
	protected abstract void onStopServer(); //サーバ停止処理

	@Override
	protected void onStopThread() {
		onStopServer(); //子クラスのスレッド停止処理
		if (ssl != null) {
			ssl.dispose();
		}
	}

	//サーバ開始処理
	
	//サーバが正常に起動できる場合(isInitSuccess==true)のみスレッド開始できる
	protected abstract boolean onStartServer(); //サーバ開始処理

	@Override
	protected boolean onStartThread() {
		return onStartServer(); //子クラスのスレッド開始処理
	}

	@Override
	protected void onLoopThread() {

        Debug.print(this,"onLoopThread() start");

		int port = (int) conf.get("port");
		String bindStr = String.format("%s:%d %s", oneBind.getAddr(), port, oneBind.getProtocol());
		logger.set(LogKind.Normal, null, 9000000, bindStr);
		//Dosを受けた場合、multiple数まで連続アクセスまでは記憶してしまう
		//Dosが終わった後も、その分だけ復帰に時間を要する

		if (oneBind.getProtocol() == ProtocolKind.Tcp) {
			//sockObj = new TcpObj(kernel, logger, oneBind.getAddr(), port, multiple, ssl);
			sockServer = new SockServer(oneBind.getAddr(), port, multiple, this);
		} else {
			//sockObj = new UdpObj(kernel, logger, oneBind.getAddr(), port, multiple);
			//sockObj = new UdpObj();
		}
		if (sockServer.getSockState() == SockState.Error) {
			logger.set(LogKind.Error, null, 9000035, sockServer.getLastEror()); //Socket生成でエラーが発生しました。[TCP]
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //このウエイトが無いと応答不能になる
		}
		sockServer.bind(); // この中でループとなる(停止は、selector.close()する)
		//bindは、最後にclose()内のSelector.close()で例外終了するので、これ以降の処理は、その際に実行される
		Debug.print(this,"onLoopThread() end");
	}

	protected abstract void onSubThread(SockAccept sockAccept);

	@Override
	public void accept(final SocketChannel accept,SockServer sockServer) {
		
		//このメソッドは、bindのスレッドから重複して次々呼びだされるので、排他制御が必要
		Debug.print(this,"accept() start");
		if (count() >= multiple){
			//同時接続数を超えたのでリクエストをキャンセルします
			logger.set(LogKind.Secure, null, 9000004,String.format("count:%d/multiple:%d", count(), multiple));
		}else{
			//子ソケット作成
			final SockAccept sockAccept = new SockAccept(accept,this);
			
//			if (sockObj.getRemoteEndPoint().getAddress().toString().equals(_denyAddress)){
//			logger.set(LogKind.Secure, null, 9000016, String.format("address:%s", _denyAddress));
//			//このアドレスからのリクエストは許可されていません
//			Thread.sleep(100);
//			sockObj.close();
//		}

			synchronized (lock) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						subThread(sockAccept);
					}
				});
				t.start();
				childThreads.add(t);
			}
		
		}
		sockServer.clearBusy();//ASocketを生成できた時点で、accept()への再入を許可する
		Debug.print(this,"accept() end");
	}
	
	private void subThread(SockAccept sockAccept){
		Debug.print(this, "subThread() start");
		onSubThread(sockAccept);
		sockAccept.close(); //子スレッドは、ここでクローズされる
		Debug.print(this, "subThread() end");
	}
	
	

	private String _denyAddress = ""; //Ver5.3.5 DoS対処

	//１リクエストに対する子スレッドとして起動される
	//	public void SubThread(Object o){
	//		childCount++; //多重度のカウンタ
	//
	//		SockObj sockObj = (SockObj) o;
	//
	//		try{
	//			//***************************************************************
	//			// ACL制限のチェック
	//			//***************************************************************
	//			if (aclList != null){
	//				if (!aclList.check(new Ip(sockObj.getRemoteEndPoint().getAddress().toString()))){
	//					sockObj.close();
	//					_denyAddress = sockObj.getRemoteEndPoint().getAddress().toString();
	//					childCount--; //多重度のカウンタ
	//					return;
	//				}
	//			}
	//
	//			//クライアントのホスト名を逆引きする
	//			sockObj.resolve((boolean) oneOption.getValue("useResolve"), logger);
	//
	//
	//
	//			//_subThreadの中でSockObjは破棄する（ただしUDPの場合は、クローンなのでClose()してもsocketは破棄されない）
	//			logger.set(LogKind.Detail, sockObj, 9000002,
	//					String.format("count=%d Local=%s Remote=%s", childCount, sockObj.getLocalEndPoint(),sockObj.getRemoteEndPoint()));
	//
	//			//接続元情報
	//			//Ip remoteAddr = new Ip(sockObj.RemoteEndPoint.Address.toString());
	//			//Ver5.4.1 sockObjのプロパティ(RemoteAddr)に変更
	//			//Ip remoteAddr = new Ip(sockObj.RemoteEndPoint.Address.toString());
	//			sockObj.RemoteAddr = new Ip(sockObj.getRemoteEndPoint().Address.toString());
	//
	//			//Ver5.4.1 sockObjのプロパティ(RemoteHost)に変更
	//			//String remoteHost = sockObj.RemoteHostName;
	//
	//			//Ver5.4.1
	//			//_subThread(sockObj, new RemoteInfo(remoteHost, remoteAddr));//接続単位の処理
	//			onSubThread(sockObj); //接続単位の処理
	//
	//			sockObj.close();
	//
	//			logger.set(LogKind.Detail, sockObj, 9000003,
	//					String.format("count=%d Local=%s Remote=%s", childCount, sockObj.getLocalEndPoint(),sockObj.getRemoteEndPoint()));
	//
	//		}
	//		catch (Exception ex){
	//			logger.set(LogKind.Error, sockObj, 9000037, ex.getMessage());
	//			logger.exception(ex);
	//		}
	//		childCount--; //多重度のカウンタ
	//	}

	/*
	public Cmd WaitLine(TcpObj tcpObj){
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
	*/
}
