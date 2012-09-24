package bjd.net;

import java.nio.channels.Selector;

public abstract class SocketBase {

	protected ISocket iSocket;
	protected String lastError = "";
    protected SocketStatus socketStatus = SocketStatus.DISCONNECT;
    protected Selector selector = null;
    

    public final String getLastEror() {
        return lastError;
    }

    //TODO とりあえず
	public final String getRemoteHost() {
		return "remoteHost";
	}

    public final SocketStatus getSocketStatus() {
        return socketStatus;
    }
    
    public SocketBase(ISocket iSocket) {
    	this.iSocket = iSocket;

    	try {
            selector = Selector.open();
        } catch (Exception ex) {
            setError(ex.getMessage());
        }
    }
    protected final void setError(String lastError) {
        socketStatus = SocketStatus.ERROR;
        this.lastError = lastError;
    }
}
