package bjd.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import bjd.util.Debug;

//サーバからacceptされたソケット
public final class ASocket extends SocketBase {

	private SocketChannel channel = null;
    
    public ASocket(SocketChannel channel, ISocket iSocket) {
    	super(iSocket);

    	Debug.print(this,"AScoket() start");
    	
    	this.channel = channel;
    	
		if (socketStatus == socketStatus.ERROR) {
    		return;
    	}
		
		String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
		Debug.print(this,String.format("接続されました %s",remoteAddress));

		try {
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception ex) {
			setError(ex.getMessage());
		}
		
    	Debug.print(this,"AScoket() end");
    }
    public void close() {
        Debug.print(this,"close() start");
        if (channel != null && channel.isOpen()) {
            try {
                selector.wakeup();
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace(); //エラーは無視する
            }
        }        
        Debug.print(this,"close() end");
    }
}
