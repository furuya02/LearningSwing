package bjd.plugins.web;

import bjd.Kernel;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.server.OneServer;
import bjd.sock.SockObj;

public final class Server extends OneServer{
	public Server(Kernel kernel, Conf conf, OneBind oneBind) {
		super(kernel, "Web", conf, oneBind);
	}

	@Override
	public String getMsg(int messageNo) {
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
	}	
}
