package bjd.net;

import java.nio.channels.SocketChannel;

public interface ISocket {
	public void accept(SocketChannel accept);
	public boolean isAcceptActive();
}
