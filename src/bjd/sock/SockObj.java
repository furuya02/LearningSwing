package bjd.sock;

import java.net.InetSocketAddress;
import java.net.Socket;

import bjd.log.LogKind;
import bjd.log.Logger;


//Socketその他を保持するクラス(１つの接続を表現している)
public abstract class SockObj {
	public Socket socket = null;
	protected String lastError = "";
    
	protected SockState sockState  = SockState.Idle;
	
	//****************************************************************
	//プロパティ
	//****************************************************************
	private String remoteHost;
    private InetSocketAddress remoteAddress = null;
    private InetSocketAddress localAddress = null;

    public final String getLastEror() {
        return lastError;
    }

    protected final void setError(String lastError) {
        sockState = SockState.Error;
        this.lastError = lastError;
    }

    public String getRemoteHost() {
		return remoteHost;
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	public SockState getSockState() {
//		if (sockState == SockState.Connect && !socket.isConnected()) {
//			sockState = SockState.Disconnect;
//		}
		return sockState;
	}
    protected final void set(SockState sockState, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
    	this.sockState = sockState;
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	//クローン
	//UDPサーバオブジェクトからコピーされた場合は、clone=trueとなり、closeは無視される
	protected boolean clone;

	public int setSendTimeout() {
//			Socket.SendTimeout = 1000 * value;
		return 0;
	}

	//****************************************************************
	//コンストラクタ
	//****************************************************************
	protected SockObj() {
		remoteHost = "";//接続先のホスト名
	}

	//TCPの場合 EndAccept() UDPの場合 EndReceiveFrom()
	//abstract public SockObj CreateChildObj(IAsyncResult ar);

	//TCPの場合 BeginAccept() UDPの場合BeginReceiveFrom()
	//abstract public void StartServer(AsyncCallback callBack);


	//【ソケットクローズ】
	public void close() {
		if (!clone) { //クローンの場合は破棄しない
			sockState = SockState.Disconnect;
			if(socket!=null){
				try {
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
			}
		}
	}

	public void resolve(boolean useResolve,Logger logger) {
		if (useResolve) {
			remoteHost = "resolve error!";
			try {
				//remoteHost = kernel.DnsCache.Get(RemoteEndPoint.Address,logger);
			} catch(Exception ex) {
				logger.set(LogKind.Error, (SockObj)null, 9000053,ex.getMessage());
			}
		} else {
			remoteHost = remoteAddress.getAddress().toString(); 
		}
	}
	//バイナリデータであることが判明している場合は、noEncodeをtrueに設定する
	//これによりテキスト判断ロジックを省略できる
//	protected void Trace(TraceKind traceKind,byte [] buf,boolean noEncode) {
//
//		if (buf == null || buf.length == 0){
//			return;
//		}
//
//		if (kernel.getRunMode() == RunMode.Remote){
//			return;//リモートクライアントの場合は、ここから追加されることはない
//		}
//
//		//Ver5.0.0-a22 サービス起動の場合は、このインスタンスは生成されていない
//		boolean enableDlg = kernel.TraceDlg != null && kernel.TraceDlg.Visible;
//		if (!enableDlg && kernel.RemoteServer==null) {
//			//どちらも必要ない場合は処置なし
//			return;
//		}
//
//		boolean isText = false;//対象がテキストかどうかの判断
//		Encoding encoding = null;
//
//		if(!noEncode) {//エンコード試験が必要な場合
//			try {
//				encoding = MLang.GetEncoding(buf);
//			} catch {
//				encoding = null;
//			}
//			if(encoding != null) {
//				//int codePage = encoding.CodePage;
//				if(encoding.CodePage == 20127 || encoding.CodePage == 65001 || encoding.CodePage == 51932 || encoding.CodePage == 1200 || encoding.CodePage == 932 || encoding.CodePage == 50220) {
//					//"US-ASCII" 20127
//					//"Unicode (UTF-8)" 65001
//					//"日本語(EUC)" 51932
//					//"Unicode" 1200
//					//"日本語(シフトJIS)" 932
//					//日本語(JIS) 50220
//					isText = true;
//				}
//			}
//		}
//
//		ArrayList<String> ar = new ArrayList<String>();
//		if (isText){
//			var lines = Inet.GetLines(buf);
//			ar.AddRange(lines.Select(line => encoding.GetString(Inet.TrimCrlf(line))));
//		}
//		else {
//			ar.Add(noEncode
//					? String.Format("binary {0} byte", buf.length)
//							: String.Format("Binary {0} byte", buf.length));
//		}
//		for (String str : ar) {
//			Ip ip = new Ip(remoteAddress.getAddress().ToString());
//
//			if(enableDlg) {//トレースダイアログが表示されてい場合、データを送る
//				kernel.TraceDlg.AddTrace(traceKind,str,ip);
//			}
//			if(kernel.RemoteServer!=null) {//リモートサーバへもデータを送る（クライアントが接続中の場合は、クライアントへ送信される）
//				kernel.RemoteServer.AddTrace(traceKind,str,ip);
//			}
//		}
//
//	}

}
