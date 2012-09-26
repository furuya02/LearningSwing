package bjd.net;

import java.nio.channels.Selector;

public abstract class SockBase {

	protected ISocket iSocket;
	protected String lastError = "";
    protected SockState sockState = SockState.Disconnect;
    protected Selector selector = null;
    

    public final String getLastEror() {
        return lastError;
    }

    //TODO とりあえず
	public final String getRemoteHost() {
		return "remoteHost";
	}

    public final SockState getSockState() {
        return sockState;
    }
    
    public SockBase(ISocket iSocket) {
    	this.iSocket = iSocket;

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
}
