package bjd.sock;

import java.nio.channels.SocketChannel;

import bjd.sock.SockServer;

public interface ISock {
	void accept(SocketChannel accept, SockServer sockServer);
}
