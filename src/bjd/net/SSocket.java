package bjd.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

//サーバソケット
public final class SSocket extends SocketBase {
//    private static final int BUF_SIZE = 3000;
    private ServerSocketChannel serverChannel = null;
    
    private Ip bindIp;
    private int port;
    private int multiple;
   
    public SSocket(Ip bindIp, int port, int multiple, ISocket iSocket) {
    	super(iSocket);
        this.bindIp = bindIp;
        this.port = port;
        this.multiple = multiple;

        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
        } catch (Exception ex) {
            setError(ex.getMessage());
        }
    }
    
    public void bind() {
        System.out.println(String.format("SSocket.bind() start ID=%d",Thread.currentThread().getId()));
        try {
            serverChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port), multiple);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            //TODO debug
            System.out.println(String.format("NonBlockingChannelEchoServerが起動しました(port=%d) ID=%d", serverChannel.socket().getLocalPort(),Thread.currentThread().getId()));
            while (selector.select() > 0) {
                for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                    	iSocket.accept(new ASocket(serverChannel.accept(), iSocket));
                        //doAccept((ServerSocketChannel) key.channel());
                    //} else if (key.isReadable()) {
                    //    doRead((SocketChannel) key.channel());
                    }
                }
            }  
			System.out.println(String.format("end while(selector.select()>0)"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(String.format("SSocket.bind() end ID=%d",Thread.currentThread().getId()));
    }
    
//    private void doAccept(ServerSocketChannel serverChannel) {
//        try {
//            SocketChannel channel = serverChannel.accept();
//            String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
//            System.out.println(remoteAddress + ":[接続されました]");
//            channel.configureBlocking(false);
//            channel.register(selector, SelectionKey.OP_READ);
// 
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            socketStatus = SocketStatus.ERROR;
//            lastError = ex.getMessage();
//        }
//    }
 
    private void doRead(SocketChannel channel) {
        ByteBuffer buf = ByteBuffer.allocate(3000);
        Charset charset = Charset.forName("UTF-8");
        String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
        try {
            if (channel.read(buf) < 0) {
                return;
            }
            buf.flip();
            System.out.print(remoteAddress + ":" + charset.decode(buf).toString());
            buf.flip();
            channel.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(remoteAddress + ":[切断しました]");
            try {
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                socketStatus = SocketStatus.ERROR;
                lastError = ex.getMessage();
            }
        }
    }
 
    
    public void close() {
        System.out.println(String.format("SSocket.close() start"));
        if (serverChannel != null && serverChannel.isOpen()) {
            try {
                System.out.println("NonBlockingChannelEchoServerを停止します。");
                selector.wakeup();
                serverChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace(); //エラーは無視する
            }
        }        
        System.out.println(String.format("SSocket.close() end"));
    }

}
