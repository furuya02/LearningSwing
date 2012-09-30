package bjd.sock;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;

import bjd.ThreadBase;

public abstract class SockBase {

	protected ISock iSock;
	protected String lastError = "";
    protected Selector selector = null;
    private SockState sockState;
    private InetSocketAddress remoteAddress = null;
    private InetSocketAddress localAddress = null;

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
	
	protected final void set(SockState sockState, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		this.sockState = sockState;
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	protected final boolean isLife(ThreadBase threadBase) {
		if (threadBase == null) {
			return true;
		}
		return threadBase.isLife();
	}
    
    public SockBase(ISock iSock) {
    	this.iSock = iSock;
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
