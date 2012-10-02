package bjd.sock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.junit.Test;

import bjd.Kernel;
import bjd.ThreadBase;
import bjd.net.Ip;
import bjd.sock.SockClientTest.EchoServer;
import bjd.util.Debug;
import bjd.util.TestUtil;

public class UdpClientTest {
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

			while (isLife()) {
				try {
					buf.clear();
					int len = channel.read(buf);
					if (len < 0) {
						System.out.println(String.format("read()<0 => close"));
						channel.close();
						break;
					}
					Debug.print(this, String.format("recv=%d", len));
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
		TestUtil.dispHeader("a001 Echoサーバ(UDP)");

		String addr = "127.0.0.1";
		int port = 9990;

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();
		
		for(int i=0;i<10;i++){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			System.out.println(String.format("[%d]",i));
		}
		
		System.out.println("echoServer.dispose()");
		echoServer.dispose();
		
	}

}
