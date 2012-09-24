package bjd.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

public final class Inet {

	private Inet() {
	} //デフォルトコンストラクタの隠蔽

	//**************************************************************************
	//バイナリ-文字列変換(バイナリデータをテキスト化して送受信するため使用する)
	//**************************************************************************
	public static byte[] toBytes(String str) {
		if (str == null) {
			str = "";
		}
		return str.getBytes(Charset.forName("UTF-16"));
	}

	public static String fromBytes(byte[] buf) {
		try {
			if (buf == null) {
				buf = new byte[0];
			}
			return new String(buf, "UTF-16");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	//**************************************************************************
	//行単位での切り出し
	//**************************************************************************
	//テキストバージョン
	public static ArrayList<String> getLines(String str) {
		if (str.equals("")) {
			return new ArrayList<String>();
		}
		String[] lines = str.split("\r\n");
		if (lines.length == 0) {
			lines = new String[] { "" };
		}
		return new ArrayList<>(Arrays.asList(lines));
	}

	// バイナリバージョン　(\r\nは削除しない)
	public static ArrayList<byte[]> getLines(byte[] buf) {
		ArrayList<byte[]> lines = new ArrayList<>();
		if (buf == null || buf.length == 0) {
			return lines;
		}

		int start = 0;
		for (int end = 0;; end++) {
			if (buf[end] == '\n') {
				if (1 <= end && buf[end - 1] == '\r') {
					byte[] tmp = new byte[end - start + 1]; //\r\nを削除しない
					System.arraycopy(buf, start, tmp, 0, end - start + 1); //\r\nを削除しない
					lines.add(tmp);
					start = end + 1;
				} else if (2 <= end && end + 1 < buf.length && buf[end + 1] == '\0' && buf[end - 1] == '\0' && buf[end - 2] == '\r') {
					byte[] tmp = new byte[end - start + 2]; //\r\nを削除しない
					System.arraycopy(buf, start, tmp, 0, end - start + 2); //\r\nを削除しない
					lines.add(tmp);
					start = end + 2;
				} else { //\nのみ
					byte[] tmp = new byte[end - start + 1]; //\nを削除しない
					System.arraycopy(buf, start, tmp, 0, end - start + 1); //\nを削除しない
					lines.add(tmp);
					start = end + 1;
				}
			}
			if (end >= buf.length - 1) {
				if (0 < (end - start + 1)) {
					byte[] tmp = new byte[end - start + 1]; //\r\nを削除しない
					System.arraycopy(buf, start, tmp, 0, end - start + 1); //\r\nを削除しない
					lines.add(tmp);
				}
				break;
			}
		}
		return lines;
	}

	//**************************************************************************
	//\r\nの削除
	//**************************************************************************
	//テキストバージョン
	public static byte[] trimCrlf(byte[] buf) {
		if (buf.length >= 1 && buf[buf.length - 1] == '\n') {
			int count = 1;
			if (buf.length >= 2 && buf[buf.length - 2] == '\r') {
				count++;
			}
			byte[] tmp = new byte[buf.length - count];
			//Buffer.BlockCopy(buf,0,tmp,0,buf.length - count);
			System.arraycopy(buf, 0, tmp, 0, buf.length - count);
			return tmp;
		}
		return buf;
	}

	// バイナリバージョン
	public static String trimCrlf(String str) {
		if (str.length() >= 1 && str.charAt(str.length() - 1) == '\n') {
			int count = 1;
			if (str.length() >= 2 && str.charAt(str.length() - 2) == '\r') {
				count++;
			}
			return str.substring(0, str.length() - count);
		}
		return str;
	}

	//**************************************************************************
	//サニタイズ処理(１行対応)
	//**************************************************************************
	public static String sanitize(String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("~", "%7E");
		return str;

	}

	//クライアントソケットを作成して相手先に接続する
	//        static public TcpObj Connect(ThreadBase threadBase, Kernel kernel, Logger logger, Ip ip, Int32 port, Ssl ssl) {
	//            //float fff = 0;
	//
	//            //TcpObj tcpObj = new TcpObj(kernel, logger, ip, port, fff, ssl);
	//            var tcpObj = new TcpObj(kernel, logger, ip, port, ssl);
	//
	//            Thread.Sleep(0);
	//            while (threadBase.isLife()) {
	//                if (tcpObj.State == SocketObjState.Connect) {
	//                    return tcpObj;
	//                }
	//                if (tcpObj.State == SocketObjState.Error) {
	//                    tcpObj.Close();//2009.06.01追加
	//                    return null;
	//                }
	//                if (tcpObj.State == SocketObjState.Disconnect) {
	//                    //相手から即効で切られた場合
	//                    tcpObj.Close();//2009.06.10追加
	//                    return null;
	//                }
	//                //Ver5.0.0-a11 勝負
	//                //Thread.Sleep(100);
	//                Thread.Sleep(10);
	//            }
	//            tcpObj.Close();//2009.06.01追加
	//            return null;
	//        }

	//**************************************************************************
	//指定した長さのランダム文字列を取得する（チャレンジ文字列用）
	//**************************************************************************
	public static String challengeStr(int len) {
		//return RandomStringUtils.random(len,"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		SecureRandom sr = new SecureRandom();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sr.setSeed(i);
			sb.append(sr.nextDouble());
		}
		return sb.toString();
	}

	//**************************************************************************
	//ハッシュ文字列の作成（MD5）
	//**************************************************************************
	public static String md5Str(String str) {
		if (str == null) {
			return "";
		}
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte[] bytes = md5.digest();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				sb.append(String.format("%02x", bytes[i] & 0xFF));
			}
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			Util.designProblem(e.getMessage());
		}
		Util.designProblem(String.format("md5str(\"%s\")", str));
		return null;
	}

	//**************************************************************************
	//リクエスト行がURLエンコード(%31%42など)されている場合は、その文字コードを取得する
	//TODO InetgetUrlEncoding(String str) 設計上思わしくないのでWebサーバ実装時に再度検討する
	//**************************************************************************
	public static Charset getUrlEncoding(String str) {
		String[] tmp = str.split(" ");
		if (tmp.length >= 3) {
			str = tmp[1];
		}
		byte[] buf = new byte[str.length()];
		int len = 0;
		boolean find = false;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '%') {
				find = true;
				String hex = String.format("%c%c", str.charAt(i + 1), str.charAt(i + 2));
				int n = Integer.valueOf(hex, 16);
				buf[len++] = (byte) n;
				i += 2;
			} else {
				buf[len++] = (byte) str.charAt(i);
			}
		}
		if (!find) {
			return Charset.forName("ASCII");
		}
		byte[] buf2 = new byte[len];
		//Buffer.BlockCopy(buf,0,buf2,0,len);
		System.arraycopy(buf, 0, buf2, 0, len);
		return MLang.getEncoding(buf2);
	}
}
