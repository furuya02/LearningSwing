package sample;

import bjd.Kernel;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.option.OneOption;
import bjd.option.OptionSample;
import bjd.server.OneServer;
import bjd.sock.SockObj;
import bjd.sock.SockTcp;

public class Server extends OneServer {

	public Server(Kernel kernel, String nameTag, Conf conf, OneBind oneBind) {
		super(kernel, nameTag, conf, oneBind);

	}
	public Server(){
		super(new Kernel(), "nameTag",null,null);
	}

	//	//リモート操作（データの取得）Toolダイログとのデータ送受
	//	override public String Cmd(String cmdStr) { return ""; }

	@Override
	public String getMsg(int messageNo) {
        switch (messageNo) {
			case 1:
				return kernel.getJp() ? "日本語" : "English"; //この形式でログ用のメッセージ追加できます。
        }
        return null;
	}

	@Override
	protected void onStopServer() {

	}

	@Override
	protected boolean onStartServer() {
		return true;
	}

	@Override
	protected void onSubThread(SockObj sockObj) {
		//UDPサーバの場合は、UdpObjで受ける
		SockTcp sockTcp = (SockTcp) sockObj;

		//オプションから「sampleText」を取得する
		String sampleText = (String) getConf().get("sampleText");

		int timeout = (int) getConf().get("timeout");
		//１行受信
		byte[] buf = sockTcp.recv(sockTcp.length(), timeout);
		//.AsciiRecv(30, OperateCrlf.No,this);

		//１行送信
		sockTcp.send(buf);
		//.AsciiSend(str, OperateCrlf.No);

	}
}
