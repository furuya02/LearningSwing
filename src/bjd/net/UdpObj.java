package bjd.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import bjd.Kernel;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.util.Bytes;

public final class UdpObj extends SockObj {
	private byte[] udpBuffer;
	private final int udpBufferSize = 65515;

	//【コンストラクタ（サーバ用）】bind　（not クローン）
	//listenMaxはコンストラクタに変化を与えるための擬似パラメータ（未使用）
	public UdpObj(Kernel kernel, Logger logger, Ip ip, int port, int listenMax){
		super(kernel, logger, ip.getInetKind());

		udpBuffer = new byte[udpBufferSize];//１パケットの最大サイズで受信待ちにする
		try {
			socket = new Socket((ip.getInetKind() == InetKind.V4) ? AddressFamily.InterNetwork : AddressFamily.InterNetworkV6, SocketType.Dgram, ProtocolType.Udp);
		} catch (Exception e) {
			state = SocketObjState.Error;
			logger.set(LogKind.Error, null, 9000036, "");//Socket生成でエラーが発生しました。[UDP]
			logger.exception(ex);
			return;
		}

		try {
			//socket.bind(new IPEndPoint(ip.IPAddress, port));
			socket.bind(new InetSocketAddress(ip.getInetAddress(),port));
		} catch (Exception ex) {
			state = SocketObjState.Error;
			logger.set(LogKind.Error, null, 9000006,"");//Socket.Bind()でエラーが発生しました。[UDP]
			logger.exception(ex);
			return;
		}
		localEndPoint = (InetSocketAddress) socket.getLocalSocketAddress();
		//localEndPoint = (IPEndPoint)Socket.LocalEndPoint;
	}

	//【コンストラクタ（クライアント用）】（not クローン）
	public UdpObj(Kernel kernel, Logger logger, Ip ip, int port){
		super(kernel, logger, ip.getInetKind();

		udpBuffer = new byte[udpBufferSize];//１パケットの最大サイズで受信待ちにする
		//socket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
		socket = new Socket((ip.getInetKind() == InetKind.V4) ? AddressFamily.InterNetwork : AddressFamily.InterNetworkV6, SocketType.Dgram, ProtocolType.Udp);
		remoteEndPoint = new  InetSocketAddress(ip.getInetAddress(), port);
	}

	//受け取ったデータとEndPointでクローンオブジェクトを生成する（クローン）
	public UdpObj(Kernel kernel, Logger logger, InetKind inetKind, Socket socket, IPEndPoint remoteEndPoint, byte[] udpBuffer){
		super(kernel, logger, inetKind);
		//サーバオブジェクトからコピーされた場合は、clone=trueとなり、closeは無視される
		clone = true;
		state = SocketObjState.Connect;
		this.socket = socket;
		udpBuffer = udpBuffer;//受信が完了しているバッファ
		localEndPoint = (InetSocketAddress) socket.getLocalSocketAddress();
		remoteEndPoint = remoteEndPoint;
	}


	@Override
	public void startServer(AsyncCallback callBack) {
		int retry = 10;
		state = SocketObjState.Idle;

		//待機開始
		if (callBack == null) {
			//UDPの場合、callBack==nullは許可していない
			state = SocketObjState.Error;
			//設計ミス
			logger.set(LogKind.Error, null, 9000007, "");//callBack関数が指定されていません[UDP]
		} else {
			//Ver5.0.0-a9
			while(0 <= retry){
				//InetSocketAddress ep = (InetSocketAddress)new InetSocketAddress((inetKind == InetKind.V4) ? IPAddress.Any : IPAddress.IPv6Any, 0);
				remoteEndPoint = (InetSocketAddress)new InetSocketAddress((inetKind == InetKind.V4) ? InetAddress.getByName("0.0.0.0") : InetAddress.getByName("::"), 0);
				try {
					Socket.BeginReceiveFrom(udpBuffer, 0, udpBuffer.length, SocketFlags.None, ref ep, callBack, this);
				} catch (Exception ex) {
					logger.set(LogKind.Error, null, 9000008,"");//BeginReceiveFrom()でエラーが発生しました[UDP]
					logger.exception(ex);
					retry--;
					continue;
				}
				break;
			}
			//remoteEndPoint = ep;
		}
	}

	@Override
	public SockObj createChildObj(IAsyncResult ar) {
		try {
			//いったん自分自身で受信データを受け取る
			InetSocketAddress ep = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0);
			int len = Socket.EndReceiveFrom(ar, ref ep);

			//受け取ったデータとEndPointでクローンを生成する
			return new UdpObj(kernel, logger, inetKind, socket, ep, Bytes.create(udpBuffer, len));
			//} catch (Exception e) {
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void SendTo(byte[] sendBuffer) {
		if (remoteEndPoint.AddressFamily == AddressFamily.InterNetwork) {
			byte [] addrBytes = remoteEndPoint.getAddress().getAddress();
			if (addrBytes[0] == 0xff && addrBytes[1] == 0xff && addrBytes[2] == 0xff && addrBytes[3] == 0xff) {
				// ブロードキャストはこのオプション設定が必要
				try {
					socket.setSocketOption(SocketOptionLevel.Socket, SocketOptionName.Broadcast, 1);
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
			}
			socket.SendTo(sendBuffer, sendBuffer.length, SocketFlags.None, remoteEndPoint);
		} else { //IPv6
			socket.SendTo(sendBuffer, sendBuffer.length, SocketFlags.None, remoteEndPoint);
		}
	}

	public boolean ReceiveFrom(int timeout) {
		//socket.ReceiveTimeout = timeout;
		try {
			// EndPoint ep = (EndPoint)new IPEndPoint(IPAddress.Any, 0);
			InetSocketAddress ep = remoteEndPoint;
			byte [] tmp = new byte[1620];
			int l = socket.ReceiveFrom(tmp, ref ep);
			udpBuffer = new byte[l];
			
			System.arraycopy(tmp, 0, udpBuffer, 0, l);

			remoteEndPoint = ep;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}