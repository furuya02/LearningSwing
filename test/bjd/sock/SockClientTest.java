package bjd.sock;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import junit.framework.Assert;

import org.junit.Test;

import bjd.Kernel;
import bjd.ThreadBase;
import bjd.net.Ip;
import bjd.net.Ssl;
import bjd.util.Debug;
import bjd.util.TestUtil;

public final class SockClientTest {

	class EchoServer extends ThreadBase implements ISock {
		SockServer sockServer;

		public EchoServer(String addr, int port) {
			super(new Kernel(), "NAME");
			sockServer = new SockServer(this, new Ip(addr), port, 1);
		}

		@Override
		public String getMsg(int no) {
			return null;
		}

		@Override
		protected boolean onStartThread() {
			return true;
		}

		@Override
		protected void onStopThread() {
			sockServer.close();
		}

		@Override
		protected void onRunThread() {
			sockServer.bind(this);
		}

		@Override
		public void accept(SocketChannel channel, SockServer sockServer) {
			sockServer.clearBusy();
			System.out.println(String.format("accept"));

			ByteBuffer buf = ByteBuffer.allocate(4000);

			while (true) {
				try {
					buf.clear();
					int len = channel.read(buf);
					if (len < 0) {
						System.out.println(String.format("read()<0 => close"));
						channel.close();
						break;
					}
					//Debug.print(this, String.format("recv=%d", len));
					buf.flip();
					channel.write(buf);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	
	@Test
	public void a001() {

		TestUtil.dispHeader("a001 Echoサーバに送信して、tcpQueueにたまったデータ（length）を確認する");
		
		String addr = "127.0.0.1";
		int port = 9999;

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();
		
		int timeout = 100;
		Ssl ssl = null;
		SockClient sockClient = new SockClient(new Ip(addr), port, timeout, ssl);

		int max = 10000;
		byte[] tmp = new byte[max];

		for (int i = 0; i < 10; i++) {
			sockClient.send(tmp);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(String.format("send(%dbyte) sockClient.length=%d", max, sockClient.length()));
			Assert.assertEquals((i + 1) * max, sockClient.length());
		}
		sockClient.close();
		echoServer.stop();
		System.out.println(String.format("finich"));
		
	}
	
	

}
