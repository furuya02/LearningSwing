package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

import bjd.util.TestUtil;

public final class TcpQueueTest {

	@Test
	public void a001() {
		TestUtil.dispHeader("a001 TcpQueue 入出力");

		TestUtil.dispPrompt(this, "tcpQueue = new TcpQueue()");
		TcpQueue tcpQueu = new TcpQueue();

		int len = tcpQueu.length();
		TestUtil.dispPrompt(this, String.format("tcpQueue.length = %s キューのバイト数は0", len));
		assertThat(len, is(0));

		TestUtil.dispPrompt(this, String.format("byte[] buf = tcpQueue.dequeu(100) 0のキューから100バイト取得"));
		byte[] buf = tcpQueu.dequeue(100);

		TestUtil.dispPrompt(this, String.format("buf == null　戻り値はnullになる"));
		assertNull(buf);

		TestUtil.dispPrompt(this, String.format("byte[] buf = new byte[100]{0,1,2,3....} テスト用データを作成"));
		buf = new byte[100];
		for (int i = 0; i < 100; i++) {
			buf[i] = (byte) i;
		}
		TestUtil.dispPrompt(this, String.format("int len = tcpQueue.enqueu(buf,100) 100バイトをキューに入れる"));
		len = tcpQueu.enqueue(buf, 100);

		len = tcpQueu.length();
		TestUtil.dispPrompt(this, String.format("tcpQueue.length = %s", len));
		assertThat(len, is(100));

		TestUtil.dispPrompt(this, String.format("byte[] buf = tcpQueue.dequeu(50) 50バイト、キューから取り出す"));
		buf = tcpQueu.dequeue(50);

		len = buf.length;
		TestUtil.dispPrompt(this, String.format("buf.length == %d", len));
		assertThat(len, is(50));

		TestUtil.dispPrompt(this, String.format("byte[] buf = tcpQueue.dequeu(30)　30バイト、キューから取り出す"));
		buf = tcpQueu.dequeue(30);

		len = buf.length;
		TestUtil.dispPrompt(this, String.format("buf.length == %d", len));
		assertThat(len, is(30));

		TestUtil.dispPrompt(this, String.format("buf = {50,51,52....}　取り出した内容を確認する"));
		for (int i = 0; i < 30; i++) {
			Assert.assertSame(buf[i], (byte) (50 + i));
		}

		TestUtil.dispPrompt(this, String.format("キューの残りは20バイト"));

		TestUtil.dispPrompt(this, String.format("byte[] buf = tcpQueue.dequeu(50) 残り20のキューから50を取得する"));
		buf = tcpQueu.dequeue(50);

		len = buf.length;
		TestUtil.dispPrompt(this, String.format("buf.length == %d 20バイトだけ取得できる", len));
		assertThat(len, is(20));

		len = tcpQueu.length();
		TestUtil.dispPrompt(this, String.format("tcpQueue.length = %s キューの残りは0バイトになる", len));
		assertThat(len, is(0));

	}

	@Test
	public void a002() {
		TestUtil.dispHeader("a002 TcpQueue　スペース確認");
		int max = 1048560;

		TestUtil.dispPrompt(this, "tcpQueue = new TcpQueue()");
		TcpQueue tcpQueu = new TcpQueue();

		int space = tcpQueu.getSpace();
		TestUtil.dispPrompt(this, String.format("tcpQueue.getSpqce()=%s キューの空きサイズ", space));
		assertThat(space, is(max));

		byte[] buf = new byte[max - 100];
		tcpQueu.enqueue(buf, buf.length);
		TestUtil.dispPrompt(this, String.format("tcpQueue.enqueue(buf,%d)", buf.length));

		space = tcpQueu.getSpace();
		TestUtil.dispPrompt(this, String.format("tcpQueue.getSpqce()=%s キューの空きサイズ", space));
		assertThat(space, is(100));

		int len = tcpQueu.enqueue(buf, 200);
		TestUtil.dispPrompt(this, String.format("tcpQueue.enqueue(buf,200)=%s 空きサイズを超えて格納すると失敗する(※0が返る)", len));
		assertThat(len, is(0));

	}

	@Test
	public void a003() {
		TestUtil.dispHeader("a003 TcpQueue　行取得");
		//int max = 1048560;

		TestUtil.dispPrompt(this, "tcpQueue = new TcpQueue()");
		TcpQueue tcpQueu = new TcpQueue();

		byte[] lines = new byte[] { 0x61, 0x0d, 0x0a, 0x62, 0x0d, 0x0a , 0x63};
		tcpQueu.enqueue(lines, lines.length);
		TestUtil.dispPrompt(this, "tcpQueue.enqueu(1/r/n2/r/n3) 2行と改行なしの1行で初期化");

		byte[] buf = tcpQueu.dequeueLine();
		TestUtil.dispPrompt(this, "tcpQueue.dequeuLine()=\"1/r/n\" 1行目取得");
		assertThat(buf, is(new byte[] { 0x61, 0x0d, 0x0a }));

		TestUtil.dispPrompt(this, "tcpQueue.dequeuLine()=\"2/r/n\" 2行目取得 ");
		buf = tcpQueu.dequeueLine();
		assertThat(buf, is(new byte[] { 0x62, 0x0d, 0x0a }));

		buf = tcpQueu.dequeueLine();
		TestUtil.dispPrompt(this, "tcpQueue.dequeuLine()=\"\" 3行目の取得は失敗する");
		assertThat(buf, is(new byte[0]));
		
		lines = new byte[] { 0x0d, 0x0a};
		tcpQueu.enqueue(lines, lines.length);
		TestUtil.dispPrompt(this, "tcpQueue.enqueu(/r/n) 改行のみ追加");
		
		buf = tcpQueu.dequeueLine();
		TestUtil.dispPrompt(this, "tcpQueue.dequeuLine()=\"3\" 3行目の取得に成功する");
		assertThat(buf, is(new byte[] { 0x63, 0x0d, 0x0a }));

		
	}

}
