package bjd.sock;

//public abstract class SockObj {
//
//    private Socket socket = null;
//    private SockState state = SockState.Idle;
//    private boolean clone = false;
//    
//    public final SockState getState() {
//        if (state == SockState.Connect && !socket.isConnected()) {
//            state = SockState.Disconnect;
//        }
//        return state;
//    }
//    
//    
//    //【ソケットクローズ】 (オーバーライド可能)
//    public final void close() {
//        if (clone) { //クローンの場合は破棄しない
//            return;
//        }
//        state = SockState.Disconnect;
//		if (socket != null) {
//            try {
//                socket.shutdownInput();
//                socket.shutdownOutput();
//                socket.close();
//            } catch (Exception ex) {
//                //シャットダウンのエラーは無視するs
//                ex.printStackTrace();
//            }
//            socket = null;
//        }
//    }
//}

//Socketその他を保持するクラス(１つの接続を表現している)
/*
public abstract class SockObj {


	protected Logger logger;
	protected Kernel kernel;
	protected InetSocketAddress localEndPoint; //{ get; set; }
	protected InetSocketAddress remoteEndPoint; // { get; set; }
	protected InetKind inetKind; //{get;private set;}
	protected boolean clone; //クローン UDPサーバオブジェクトからコピーされた場合は、clone=trueとなり、closeは無視される
	private String remoteHost = "";
	private Ip remoteAddr;

	public final InetSocketAddress getLocalEndPoint() {
		return localEndPoint;
	}

	public final InetSocketAddress getRemoteEndPoint() {
		return remoteEndPoint;
	}

	public final String getRemoteHost() {
		return remoteHost;
	}

	public final SocketObjState getState() {
		if (state == SocketObjState.Connect && !socket.isConnected()) {
			state = SocketObjState.Disconnect;
		}
		return state;
	}

	public final void setSendTimeout() {
		//socket.SendTimeout = 1000 * value;
	}

	//****************************************************************
	//コンストラクタ
	//****************************************************************
	protected SockObj(Kernel kernel, Logger logger, InetKind inetKind) {
		this.kernel = kernel;
		this.logger = logger;
		this.inetKind = inetKind;

		//RemoteHostName = "";//接続先のホスト名
		remoteHost = ""; //接続先のホスト名
		remoteAddr = new Ip("0.0.0.0");

	}

	//TCPの場合 EndAccept() UDPの場合 EndReceiveFrom()
	public abstract SockObj createChildObj(IAsyncResult ar);

	//TCPの場合 BeginAccept() UDPの場合BeginReceiveFrom()
	public abstract void startServer(AsyncCallback callBack);

	//【2009.01.13 追加】IPアドレスからホスト名を取得する
	public final void resolve(boolean useResolve, Logger logger) {
		if (useResolve) {
			remoteHost = "resolve error!";
			try {
				remoteHost = kernel.getDnsCache().get(remoteEndPoint.getAddress(), logger);
			} catch (Exception ex) {
				logger.set(LogKind.Error, null, 9000053, ex.getMessage());
			}
		} else {
			//remoteHost = RemoteEndPoint.Address.ToString();
			remoteHost = remoteEndPoint.getAddress().toString();
		}
	}

	//【ソケットクローズ】 (オーバーライド可能)
	public void close() {
		if (clone) { //クローンの場合は破棄しない
			return;
		}
		state = SocketObjState.Disconnect;
		try {
			//socket.Shutdown(SocketShutdown.Both);
			socket.shutdownInput();
			socket.shutdownOutput();
		} catch (Exception ex) { //TCPのサーバソケットをシャットダウンするとエラーになる（無視する）
			ex.printStackTrace();
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//バイナリデータであることが判明している場合は、noEncodeをtrueに設定する
	//これによりテキスト判断ロジックを省略できる
	protected final void trace(TraceKind traceKind, byte[] buf, boolean noEncode) {

		if (buf == null || buf.length == 0) {
			return;
		}
		if (kernel.getRunMode() == RunMode.Remote) {
			return; //リモートクライアントの場合は、ここから追加されることはない
		}
		//Ver5.0.0-a22 サービス起動の場合は、このインスタンスは生成されていない
		boolean enableDlg = kernel.getTraceDlg() != null && kernel.getTraceDlg().isVisible();
		if (!enableDlg && kernel.getRemoteServer() == null) {
			//どちらも必要ない場合は処置なし
			return;
		}

		boolean isText = false; //対象がテキストかどうかの判断
		Charset charset = null;

		if (!noEncode) { //エンコード試験が必要な場合
			try {
				charset = MLang.getEncoding(buf);
			} catch (Exception ex) {
				charset = null;
			}
			if (charset != null) {
				//int codePage = encoding.CodePage;
				switch (charset.name()) {
					case "ISO-2022-JP":
					case "US-ASCII":
					case "UTF-8":
					case "UTF-16":
					case "EUC-JP":
					case "Shift_JIS":
						isText = true;
						break;
					default:
						break;
				}
			}
		}

		ArrayList<String> ar = new ArrayList<>();
		if (isText) {
			for (byte[] line : Inet.getLines(buf)) {
				ar.add(new String(Inet.trimCrlf(line), charset));
			}
		} else {
			ar.add(noEncode ? String.format("binary %d byte", buf.length) : String.format("Binary %d byte", buf.length));
		}
		for (String str : ar) {
			Ip ip = new Ip(remoteEndPoint.getAddress().getHostAddress());

			if (enableDlg) { //トレースダイアログが表示されてい場合、データを送る
				kernel.getTraceDlg().addTrace(traceKind, str, ip);
			}
			if (kernel.getRemoteServer() != null) { //リモートサーバへもデータを送る（クライアントが接続中の場合は、クライアントへ送信される）
				kernel.getRemoteServer().addTrace(traceKind, str, ip);
			}
		}
	}
}
*/