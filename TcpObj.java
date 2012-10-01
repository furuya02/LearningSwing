package bjd.net;

import java.net.InetSocketAddress;

import bjd.Kernel;
import bjd.ThreadBase;
import bjd.TraceKind;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.util.Bytes;

public class TcpObj extends SockObj {
	//受信用バッファ（接続完了後に[BeginReceive()の中で]確保される）
	protected TcpQueue tcpQueue;
	private byte[] tcpBuffer;//１行処理のためのテンポラリバッファ
	private Ssl ssl;
	private OneSsl oneSsl;
	
	private Object lock = new Object();

	//最後にLineSendで送信した文字列
	String lastLineSend = "";
	public String getLastLineSend() {
		return lastLineSend;
	}

	//【コンストラクタ（クライアント用）】
	public TcpObj(Kernel kernel, Logger logger, Ip ip, int port, Ssl ssl){
		super(kernel, logger, ip.getInetKind()) {
		//SSL通信を使用する場合は、このオブジェクトがセットされる 通常の場合は、null
		this.ssl = ssl;

		socket = new Socket((ip.InetKind == InetKind.V4) ? AddressFamily.InterNetwork : AddressFamily.InterNetworkV6, SocketType.Stream, ProtocolType.Tcp);
		//try{
			socket.BeginConnect(ip.getInetAddress(), port, CallbackConnect, this);
		//} catch {
		//	state = SocketObjState.Disconnect;
		//}
		//ここまでくると接続が完了している  BeginReceive();//接続完了処理（受信待機開始）
	}

	////通常のサーバでは、このファンクションを外部で作成する
	//void callbackConnect(IAsyncResult ar) {
	//    if (socket.Connected) {
	//        socket.EndConnect(ar);
	//        //ここまでくると接続が完了している
	//        if (ssl != null) {//SSL通信の場合は、SSLのネゴシエーションが行われる
	//            oneSsl = ssl.CreateClientStream(socket);
	//            if (oneSsl == null) {
	//                state = SOCKET_OBJ_STATE.ERROR;
	//                return;
	//            }
	//        }
	//        BeginReceive();//接続完了処理（受信待機開始）
	//    } else {
	//        state = SOCKET_OBJ_STATE.ERROR;
	//    }
	//}
	//通常のサーバでは、このファンクションを外部で作成する
	void CallbackConnect(IAsyncResult ar) {
		if (socket.isConnected()) {
			socket.EndConnect(ar);
			//ここまでくると接続が完了している
			if (ssl != null) { //SSL通信の場合は、SSLのネゴシエーションが行われる
				//Ver5.3.6 ssl.CreateClientStream()の例外をトラップ
				try {
					oneSsl = ssl.createClientStream(socket);
				} catch (Exception ex) {
					logger.set(LogKind.Error, this, 9000057, ex.getMessage());//Ver5.3.6 例外表示
					oneSsl = null;
				}
				if (oneSsl == null) {
					state = SocketObjState.Error;
					return;
				}
			}
			BeginReceive();//接続完了処理（受信待機開始）
		} else {
			state = SocketObjState.Error;
		}
	}

	//【コンストラクタ（サーバ用）】bind/listen
	public TcpObj(Kernel kernel, Logger logger, Ip ip, int port, int listenMax, Ssl ssl){
		super(kernel, logger, ip.getInetKind());
		//SSL通信を使用する場合は、このオブジェクトがセットされる
		//通常の場合は、null
		this.ssl = ssl;
		if (ssl != null && !ssl.getStatus()) { //SSLの初期化に失敗している
			state = SocketObjState.Error;
			logger.set(LogKind.Error, null, 9000028, "");
			return;
		}
		try {
			//socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
			socket = new Socket((ip.InetKind == InetKind.V4) ? AddressFamily.InterNetwork : AddressFamily.InterNetworkV6, SocketType.Stream, ProtocolType.Tcp);
		} catch (Exception ex) {
			state = SocketObjState.Error;
			logger.set(LogKind.Error, null, 9000035,"");//Socket生成でエラーが発生しました。[TCP]
			logger.exception(ex);
			return;
		}

		try {
			socket.bind(new InetSocketAddress(ip.getInetAddress(), port));
		} catch (Exception ex) {
			state = SocketObjState.Error;
			logger.set(LogKind.Error, null, 9000009, "");//Socket.Bind()でエラーが発生しました。[TCP]
			logger.exception(ex);
			return;
		}
		try {
			socket.Listen(listenMax);
		} catch (Exception ex) {
			state = SocketObjState.Error;
			logger.set(LogKind.Error, null, 9000010,"");//Socket.Listen()でエラーが発生しました。[TCP]
			logger.exception(ex);
			return;
		}
		localEndPoint = (InetSocketAddress) socket.getLocalSocketAddress();
		//準備完了
		//StartServerを実行すると待ち受け状態になる
	}

	//サーバでAcceptしたsocketから初期化される、子ソケット
	public TcpObj(Kernel kernel, Logger logger, InetKind inetKind, Socket socket, Ssl ssl){
		super(kernel, logger, inetKind);
		this.ssl = ssl;

		this.socket = socket;

		//既に接続を完了している
		if (ssl != null) { //SSL通信の場合は、SSLのネゴシエーションが行われる
			oneSsl = ssl.CreateServerStream(socket);
			if (oneSsl == null) {
				state = SocketObjState.Error;
				return;
			}
		}
		BeginReceive();//接続完了処理（受信待機開始）
	}


	@Override
	public void startServer(AsyncCallback callBack) {
		//待機開始
		Socket.BeginAccept(callBack ?? AcceptFunc, this);
	}

	@Override
	public SockObj createChildObj(IAsyncResult ar) {
		try {
			return new TcpObj(kernel, logger, inetKind, socket.EndAccept(ar), ssl);
		} catch (Exception ex) {
			ex.printStackTrace();
			//ソケットがクローズされてからもここへ到達する可能性が有るため
			return null;
		}
	}

	//通常のサーバでは、このファンクションを外部で作成する
	void AcceptFunc(IAsyncResult ar) {
		try { //Ver5.1.3-b5
			//自分自身を複製するため、いったん別のSocketで受け取る必要がある
			Socket newSocket = socket.EndAccept(ar);
			//socket.Shutdown(SocketShutdown.Both);これはエラーになる
			socket.close();

			//新しいソケットで置き換える
			socket = newSocket;

			if (ssl != null) { //SSL通信の場合は、SSLのネゴシエーションが行われる
				oneSsl = ssl.createClientStream(socket);
				if (oneSsl == null) {
					state = SocketObjState.Error;
					return;
				}
			}
			BeginReceive();//接続完了処理（受信待機開始）
		} catch (Exception ex) {
			ex.printStackTrace();
			state = SocketObjState.Error;
		}
	}

	//接続完了処理（受信待機開始）
	private void BeginReceive() {
		//受信バッファは接続完了後に確保される
		tcpQueue = new TcpQueue();
		tcpBuffer = new byte[tcpQueue.getSpace()];//キューが空なので、Spaceはバッファの最大サイズになっている

		try {
			localEndPoint = (InetSocketAddress) socket.getLocalSocketAddress();
			remoteEndPoint = (InetSocketAddress) socket.getRemoteSocketAddress();
		} catch (Exception ex) {
			ex.printStackTrace();
			state = SocketObjState.Error;
			return;
		}

		//if (ssl != null) {//SSL通信の場合は、SSLのネゴシエーションが行われる
		//    oneSsl = new OneSsl(socket,ssl.TargetServer);
		//    if (oneSsl == null) {
		//        state = SOCKET_OBJ_STATE.ERROR;
		//        return;
		//    }
		//}
		state = SocketObjState.Connect;

		//受信待機の開始(oneSsl!=nullの場合、受信バイト数は0に設定する)
		//socket.BeginReceive(tcpBuffer, 0, (oneSsl != null) ? 0 : tcpQueue.Space, SocketFlags.None, new AsyncCallback(EndReceive), this);
		try {
			if (ssl != null) {
				oneSsl.BeginRead(tcpBuffer, 0, tcpQueue.getSpace(), EndReceive, this);
			} else {
				socket.BeginReceive(tcpBuffer, 0, tcpQueue.getSpace(), SocketFlags.None, EndReceive, this);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			state = SocketObjState.Error;
		}
	}

	//受信処理・受信待機
	public void EndReceive(IAsyncResult ar) {
		if (ar == null) { //受信待機
			while ((tcpQueue.getSpace()) == 0) {
				Thread.sleep(10);//他のスレッドに制御を譲る  
				if (state != SocketObjState.Connect){
					state = SocketObjState.Disconnect;
					return;
				}
			}
		} else { //受信完了
			synchronized (lock){ //ポインタを移動する場合は、排他制御が必要
				try{
					int bytesRead = oneSsl != null ? oneSsl.EndRead(ar) : socket.EndReceive(ar);
					if (bytesRead == 0){
						//  切断されている場合は、0が返される?
						if (ssl == null){
							state = SocketObjState.Disconnect;
							return;
						}
						Thread.sleep(10);//Ver5.0.0-a19
					}else if (bytesRead < 0) {
						//エラー発生
						state = SocketObjState.Disconnect;
						return;
					} else {
						tcpQueue.enqueue(tcpBuffer, bytesRead);//キューへの格納
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				//受信待機のままソケットがクローズされた場合は、ここにくる
				//エラー発生
				state = SocketObjState.Disconnect;
				return;
			}
		}

		if (tcpQueue.getSpace() == 0){
			//バッファがいっぱい 空の受信待機をかける
			EndReceive(null);
		}else{
			//受信待機の開始
			try {
				if (oneSsl != null) {
					oneSsl.BeginRead(tcpBuffer, 0, tcpQueue.getSpace(), EndReceive, this);
				} else {
					socket.BeginReceive(tcpBuffer, 0, tcpQueue.getSpace(), SocketFlags.None, EndReceive, this);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				//切断されている
				state = SocketObjState.Disconnect;
				return;
			}
		}
		return;
		//close();クローズは外部から明示的に行う
	}



	//内部でASCIIコードとしてエンコードする１行送信
	//LineSend()のオーバーライドバージョン
	public int AsciiSend(String str, OperateCrlf operateCrlf) {
		lastLineSend = str;
		var buf = Encoding.ASCII.GetBytes(str);
		return LineSend(buf, operateCrlf);
	}

	//AsciiSendを使用したいが、文字コードがASCII以外の可能性がある場合、こちらを使用する
	public int SjisSend(String str, OperateCrlf operateCrlf) {
		lastLineSend = str;
		var buf = Encoding.GetEncoding("shift-jis").GetBytes(str);
		return LineSend(buf, operateCrlf);
	}

	public int LineSend(byte[] buf, OperateCrlf operateCrlf) {
		if (operateCrlf == OperateCrlf.Yes) {
			buf = Bytes.create(buf, new byte[] { 0x0d, 0x0a });
		}

		//noEncode = false;//テキストである事が分かっている
		trace(TraceKind.Send, buf, false);//トレース表示

		//int offset = 0;
		try {
			return oneSsl != null ? oneSsl.Write(buf, buf.length) : socket.Send(buf, SocketFlags.None);
		} catch(Exception ex) {
			return -1;
		}
	}

	public int Length() {
		return tcpQueue.length();
	}

	// 【１行受信】
	//切断されている場合、nullが返される
	public String AsciiRecv(int timeout, OperateCrlf operateCrlf,ILife iLife) {
		var buf = LineRecv(timeout, operateCrlf, iLife);
		return buf == null ? null : Encoding.ASCII.GetString(buf);
	}

	// 【１行受信】
	//切断されている場合、nullが返される
	public byte[] LineRecv(int timeout, OperateCrlf operateCrlf, ILife iLife) {
		Socket.ReceiveTimeout = timeout * 1000;

		var breakTime = DateTime.Now.AddSeconds(timeout);

		while (iLife.isLife()) {
			//Ver5.1.6
			if (tcpQueue.Length == 0)
				Thread.Sleep(100);
			var buf = tcpQueue.DequeueLine();
			//noEncode = false;//テキストである事が分かっている
			trace(TraceKind.Recv, buf, false);//トレース表示
			if (buf.Length != 0) {
				if (operateCrlf == OperateCrlf.Yes) {
					buf = Inet.TrimCrlf(buf);
				}
				return buf;
			}
			//【2009.01.12 追加】
			if (!Socket.Connected) {
				state = SocketObjState.Disconnect;
				return null;
			}
			if (state != SocketObjState.Connect)
				return null;
			if (DateTime.Now > breakTime)
				return null;
			//Thread.Sleep(100);//<=これ待ちすぎ？Ver5.0.0-b22
			Thread.Sleep(1);//
		}
		return null;
	}


	//【バイナリ受信】
	public boolean RecvBinary(String fileName, ref boolean life) {
		final int max = 65535; //処理するブロックサイズ
		boolean result = false;

		//トレース表示
		var sb = new StringBuilder();
		sb.Append(string.format("RecvBinaryFile(%s) ", fileName));

		var fs = new FileStream(fileName, FileMode.Create);
		var bw = new BinaryWriter(fs);
		fs.Seek(0, SeekOrigin.Begin);

		try {
			while (life) {
				Thread.Sleep(0);
				// キューから取得する
				var buffer = tcpQueue.Dequeue(max);
				if (buffer == null) {
					//logger.Set(LogKind.Debug, 9000011,"");//"tcpQueue().Dequeue()=null"
					if (state != SocketObjState.Connect) {
						//logger.Set(LogKind.Debug,9000012,"");//"tcpQueue().Dequeue() SocektObjState != SOCKET_OBJ_STATE.CONNECT break"
						result = true;
						break;
					}
					//Thread.Sleep(100);
					Thread.Sleep(1);
				} else {
					//logger.Set(LogKind.Debug, 9000013 , string.format("buffer.Length=%dbyte", buffer.Length));//"tcpQueue().Dequeue()";
					bw.Write(buffer, 0, buffer.Length);

					//トレース表示
					sb.Append(string.format("Binary=%dbyte ", buffer.Length));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		bw.Flush();
		bw.Close();
		fs.Close();

		//noEncode = true バイナリである事が分かっている
		trace(TraceKind.Recv, Encoding.ASCII.GetBytes(sb.ToString()), true);//トレース表示
		return result;
	}

	////【バイナリ送信】
	public boolean SendBinaryFile(String fileName, /*ILife iLife これいるのか*/) {
		//トレース表示
		var sb = new StringBuilder();
		sb.Append(string.format("SendBinaryFile(%s) ", fileName));

		var buffer = new byte[3000000];
		var result = false;
		if (File.Exists(fileName)) {
			try {
				using (var fs = new FileStream(fileName, FileMode.Open, FileAccess.Read, FileShare.Read)) {
					using (var br = new BinaryReader(fs)) {
						fs.Seek(0, SeekOrigin.Begin);
						var offset = 0L;
						while (life) {
							var len = fs.Length - offset;
							if (len == 0) {
								result = true;
								break;
							}
							if (len > buffer.Length) {
								len = buffer.Length;
							}
							len = br.Read(buffer, 0, (int)len);

							//トレース表示
							sb.Append(string.format("Binary=%dbyte ", len));

							try {
								if (oneSsl != null) {
									oneSsl.Write(buffer, (int)len);
								} else {
									Socket.Send(buffer, 0, (int)len, SocketFlags.None);
								}
							} catch (Exception ex) {
								logger.set(LogKind.Error, null, 9000014,"");//"SendBinaryFile(string fileName) socket.Send()"
								logger.exception(ex);
								break;
							}

							offset += len;
							fs.Seek(offset, SeekOrigin.Begin);
						}
						br.Close();
					}
					fs.Close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.set(LogKind.Error, null, 9000050, ex.getMessage());
			}
		}
		//noEncode = true;//バイナリである事が分かっている
		trace(TraceKind.Send, Encoding.ASCII.GetBytes(sb.ToString()), true);//トレース表示
		return result;
	}

	//【送信】(トレースなし)
	//リモートサーバがトレース内容を送信するときに更にトレースするとオーバーフローするため
	//RemoteObj.Send()では、こちらを使用する
	public int SendNoTrace(byte[] buffer) {
		try {
			if (oneSsl != null) {
				return oneSsl.Write(buffer, buffer.length);
			}
			if (socket.isConnected())
				return socket.Send(buffer, 0, buffer.length, SocketFlags.None);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.set(LogKind.Error, this, 9000046, String.format("Length=%d %s", buffer.length, ex.getMessage()));
		}
		return -1;
	}

	//【送信】バイナリ
	public int SendNoEncode(byte[] buf) {
		//バイナリであるのでエンコード処理は省略される
		trace(TraceKind.Send, buf, true);//noEncode = true バイナリであるのでエンコード処理は省略される
		//実際の送信処理にテキストとバイナリの区別はない
		return SendNoTrace(buf);
	}

	//【送信】テキスト（バイナリかテキストかが不明な場合もこちら）
	public int SendUseEncode(byte[] buf) {
		//テキストである可能性があるのでエンコード処理は省略できない
		trace(TraceKind.Send, buf, false);//noEncode = false テキストである可能性があるのでエンコード処理は省略できない
		//実際の送信処理にテキストとバイナリの区別はない
		return SendNoTrace(buf);
	}

	public byte[] Recv(int len, int timeout) {
		var dt = DateTime.Now.AddSeconds(timeout);
		byte[] buffer = new byte[0];
		try {
			if (len <= tcpQueue.length()) {
				// キューから取得する
				buffer = tcpQueue.dequeue(len);
			} else {
				while (true) {
					Thread.sleep(0);
					if (0 < tcpQueue.length()) {
						//size=受信が必要なバイト数
						int size = len - buffer.length;
						//受信に必要なバイト数がバッファにない場合
						if (size > tcpQueue.length()){
							size = tcpQueue.length();//とりあえずバッファサイズ分だけ受信する
						}
						byte[] tmp = tcpQueue.dequeue(size);
						buffer = Bytes.create(buffer, tmp);
						if (len <= buffer.length) {
							break;
						}
					} else {
						if (state != SocketObjState.Connect) {
							return null;
						}
						//Thread.Sleep(300);
						Thread.sleep(10);//Ver5.0.0-a19
					}
					if (dt < DateTime.Now) {
						buffer = tcpQueue.dequeue(len);//タイムアウト
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		trace(TraceKind.Recv, buffer, false);//noEncode = false;テキストかバイナリかは不明
		return buffer;
	}

	//【ソケットクローズ】
	@Override
	public void close() {
		if (oneSsl != null) {
			oneSsl.close();
		}
		super.close();
	}
}
