package bjd.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;

import bjd.ThreadBase;

public abstract class SockBase {

	protected ISocket iSocket;
	protected String lastError = "";
    protected SockState sockState;
    protected Selector selector = null;
    protected InetSocketAddress remoteAddress = null;
    protected InetSocketAddress localAddress = null;
    

    public final String getLastEror() {
        return lastError;
    }

    //TODO とりあえず
	//public final String getRemoteHost() {
	//	return "remoteHost";
	//}

    public final InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public final InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	public final SockState getSockState() {
        return sockState;
    }
    
	protected final boolean isLife(ThreadBase threadBase) {
		if (threadBase == null) {
			return true;
		}
		return threadBase.isLife();
	}
    
    public SockBase(ISocket iSocket) {
    	this.iSocket = iSocket;
    	 sockState = SockState.Idle;

    	try {
            selector = Selector.open();
        } catch (Exception ex) {
            setError(ex.getMessage());
        }
    }
    protected final void setError(String lastError) {
        sockState = SockState.Error;
        this.lastError = lastError;
    }

	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return "";
	}
}
