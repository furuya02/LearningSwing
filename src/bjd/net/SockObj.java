package bjd.net;

import java.net.InetAddress;
import java.util.ArrayList;

import bjd.Kernel;
import bjd.RunMode;
import bjd.log.LogKind;
import bjd.log.Logger;


//Socketその他を保持するクラス(１つの接続を表現している)
public abstract class SockObj {

	
	//public Socket Socket;
	protected Logger Logger;
	protected Kernel Kernel;
	protected SocketObjState state = SocketObjState.Idle;

	private String remoteHost = "";
	private Ip remoteAddr; //{ get; set; }
	//private IPEndPoint LocalEndPoint; //{ get; set; }
	//private IPEndPoint RemoteEndPoint; // { get; set; }
	private InetAddress LocalEndPoint; //{ get; set; }
	private InetAddress RemoteEndPoint; // { get; set; }
	private InetKind InetKind; //{get;private set;}

	public String getRemoteHost() {
		return remoteHost;
	}


	public SocketObjState getState() {
		if (state == SocketObjState.Connect && !Socket.Connected) {
			state = SocketObjState.Disconnect;
		}
		return state;
	}

	//クローン
	//UDPサーバオブジェクトからコピーされた場合は、clone=trueとなり、closeは無視される
	protected boolean Clone;

	public int setSendTimeout()
		Socket.SendTimeout = 1000 * value;
	}

	//****************************************************************
	//コンストラクタ
	//****************************************************************
	protected SockObj(Kernel kernel,Logger logger,InetKind inetKind) {
		Kernel = kernel;
		Logger = logger;
		InetKind = inetKind;


		//Verr5.4.1
		//RemoteHostName = "";//接続先のホスト名
		remoteHost = "";//接続先のホスト名
		//Ver5.4.1
		remoteAddr = new Ip("0.0.0.0");
	}

	//TCPの場合 EndAccept() UDPの場合 EndReceiveFrom()
	abstract public SockObj CreateChildObj(IAsyncResult ar);

	//TCPの場合 BeginAccept() UDPの場合BeginReceiveFrom()
	abstract public void StartServer(AsyncCallback callBack);


	//【ソケットクローズ】
	virtual public void Close() {
		if (Clone) {//クローンの場合は破棄しない
			return;
		}
		state = SocketObjState.Disconnect;
		try {
			Socket.Shutdown(SocketShutdown.Both);
		} catch(Exception ex) {
			//TCPのサーバソケットをシャットダウンするとエラーになる（無視する）
		}
		if(Socket!=null)
			Socket.Close();
	}
	//【2009.01.13 追加】IPアドレスからホスト名を取得する
	public void Resolve(boolean useResolve,Logger logger) {
		if (useResolve) {
			remoteHost = "resolve error!";
			try {
				remoteHost = Kernel.DnsCache.Get(RemoteEndPoint.Address,logger);
			} catch(Exception ex) {
				logger.set(LogKind.Error, null, 9000053, ex.getMessage());
			}
		} else {
			//remoteHost = RemoteEndPoint.Address.ToString();
			remoteHost = RemoteEndPoint.getHostAddress();
		}
	}
	//バイナリデータであることが判明している場合は、noEncodeをtrueに設定する
	//これによりテキスト判断ロジックを省略できる
	protected void Trace(TraceKind traceKind,byte [] buf,boolean noEncode) {

		if (buf == null || buf.length == 0){
			return;
		}
		if (Kernel.getRunMode() == RunMode.Remote){
			return;//リモートクライアントの場合は、ここから追加されることはない
		}
		//Ver5.0.0-a22 サービス起動の場合は、このインスタンスは生成されていない
		boolean enableDlg = Kernel.getTraceDlg() != null && Kernel.getTraceDlg().isVisible();
		if (!enableDlg && Kernel.getRemoteServer()==null) {
			//どちらも必要ない場合は処置なし
			return;
		}

		boolean isText = false;//対象がテキストかどうかの判断
		Encoding encoding = null;

		if(!noEncode) {//エンコード試験が必要な場合
			try {
				encoding = MLang.GetEncoding(buf);
			} catch(Exception ex) {
				encoding = null;
			}
			if(encoding != null) {
				//int codePage = encoding.CodePage;
				if(encoding.CodePage == 20127 || encoding.CodePage == 65001 || encoding.CodePage == 51932 || encoding.CodePage == 1200 || encoding.CodePage == 932 || encoding.CodePage == 50220) {
					//"US-ASCII" 20127
					//"Unicode (UTF-8)" 65001
					//"日本語(EUC)" 51932
					//"Unicode" 1200
					//"日本語(シフトJIS)" 932
					//日本語(JIS) 50220
					isText = true;
				}
			}
		}

		ArrayList<String> ar = new ArrayList<>();
		if (isText){
			var lines = Inet.GetLines(buf);
			ar.AddRange(lines.Select(line => encoding.GetString(Inet.TrimCrlf(line))));
		}
		else {
			ar.Add(noEncode	? String.format("binary %d byte", buf.length)
							: String.format("Binary %d byte", buf.length));
		}
		for (String str : ar) {
			Ip ip = new Ip(RemoteEndPoint.getHostAddress());

			if(enableDlg) {//トレースダイアログが表示されてい場合、データを送る
				Kernel.getTraceDlg().AddTrace(traceKind,str,ip);
			}
			if(Kernel.getRemoteServer()!=null) {//リモートサーバへもデータを送る（クライアントが接続中の場合は、クライアントへ送信される）
				Kernel.getRemoteServer().AddTrace(traceKind,str,ip);
			}
		}
	}
}


