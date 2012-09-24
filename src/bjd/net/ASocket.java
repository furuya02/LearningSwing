package bjd.net;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

//サーバからacceptされたソケット
public final class ASocket extends SocketBase {

	private SocketChannel channel = null;
    
    public ASocket(SocketChannel channel, ISocket iSocket) {
    	super(iSocket);
    	
    	this.channel = channel;
    	
		if (socketStatus == socketStatus.ERROR) {
    		return;
    	}
		
		String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
		System.out.println(remoteAddress + ":[接続されました]");

		try {
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception ex) {
			setError(ex.getMessage());
		}
    }
}
