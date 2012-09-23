package bjd.net;

import static org.junit.Assert.*;

import java.nio.charset.Charset;

import org.junit.Test;

public class SockObjTest {

	@Test
	public void test() {
		Charset c = Charset.forName("ISO-2022-JP");
		String s = c.name();
		
		c = Charset.forName("ASCII");
		s = c.name();
		
		c = Charset.forName("UTF-8");
		s = c.name();

		c = Charset.forName("EUC-JP");
		s = c.name();
		
		c = Charset.forName("SHIFT-JIS");
		s = c.name();

		c = Charset.forName("UNICODE");
		s = c.name();
		
		c = Charset.forName("SJIS");
		s = c.name();

		//if (charset.name().CodePage == 20127 || charset.CodePage == 65001 || charset.CodePage == 51932 || charset.CodePage == 1200 || charset.CodePage == 932 || charset.CodePage == 50220) {
			//"US-ASCII" 20127
			//"Unicode (UTF-8)" 65001
			//"日本語(EUC)" 51932
			//"Unicode" 1200
			//"日本語(シフトJIS)" 932
			//日本語(JIS) 50220
			//isText = true;
		//}

		
		fail("まだ実装されていません");
	}

}
