package bjd.sock;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;


public interface ISock {
	void accept(SocketChannel accept, SockServer sockServer);
	void read(DatagramChannel channel, SockUdpServer sockUdpServer);
}
