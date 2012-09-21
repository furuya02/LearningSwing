package bjd.util;

import java.nio.charset.Charset;

public class MLang {

	private MLang() {
		//デフォルトコンストラクタの隠蔽
	}
	
	static public Charset GetEncoding(String fileName) {
		using (var fs = new FileStream(fileName, FileMode.Open, FileAccess.Read, FileShare.ReadWrite, 1024)){
			byte [] bs = new byte[fs.Length];
			fs.Read(bs, 0, bs.Length);
			fs.Close();
			return bs.length == 0 ? Charset.forName("ASCII") : GetEncoding(bs);
		}
	}

	static public String GetString(byte[] buf) {

		int len = buf.length;
		if (len == 0) {
			return "";
		}
		Charset charset = GetEncoding(buf);
		return new String(buf,charset);

	}

	//雅階凡の C# プログラミング
	//文字コードの判定 を参考にさせて頂きました。
	//http://www.geocities.jp/gakaibon/tips/csharp2008/charset-check.html
	public static Charset GetEncoding(byte[] bytes) {

		int len = bytes.length;
		if (len > 1500)
			len = 1500;

		//【ASCIIコードかどうかの判定】
		boolean isAscii = true;

		//【日本語JISかどうかの判定】
		boolean isJis = false;
		for (int i = 0; i < len; i++) {
			if (bytes[i] > 0x7F) {
				isAscii = false;
				break; //範囲外
			}
			//ESCパターンの出現を確認できたら、ISO-2022-JPと判断する
			if (bytes[i] == 0x1b) {
				if (i < len - 2) {
					byte b2 = bytes[i + 1];
					byte b3 = bytes[i + 2];
					if (b2 == 0x28) {
						// ESC(B / ESC(J / ESC(I
						if (b3 == 0x42 || b3 == 0x4A || b3 == 0x49) {
							isJis = true;
							break;
						}

					} else if (b2 == 0x24) {
						// ESC$@ /  ESC$B 
						if (b3 == 0x40 || b3 == 0x42) {
							isJis = true;
							break;
						}
					}
				} else if (i < len - 3) {
					byte b2 = bytes[i + 1];
					byte b3 = bytes[i + 2];
					byte b4 = bytes[i + 3];
					if (b2 == 0x24 && b3 == 0x28 && b4 == 0x44) {// ESC$(D 
						isJis = true;
						break;
					}
				} else if (i < len - 5) {
					byte b2 = bytes[i + 1];
					byte b3 = bytes[i + 2];
					byte b4 = bytes[i + 3];
					byte b5 = bytes[i + 4];
					byte b6 = bytes[i + 5];
					if (b2 == 0x26 && b3 == 0x40 && b4 == 0x1B && b5 == 0x24 && b6 == 0x42) {// ESC&@ESC$B 
						isJis = true;
						break;
					}
				}
			}
		}
		if (isJis) {
			//return Encoding.GetEncoding(50220);
			return Charset.forName("ISO-2022-JP");
		}

		//【ASCIIコードかどうかの判定】
		//bool isAscii=true;
		//for(int i = 0;i < len;i++) {
		//    if(bytes[i] > 0x7F) {//すべてが0x1f以下の場合は、ASCIIと判断する
		//        isAscii=false;
		//        break;
		//    }
		//}
		if (isAscii) {
			//return Encoding.ASCII;
			return Charset.forName("ASCII");
		}

		//【Shjift-JISの可能性と2バイトコードの出現数カウント】
		boolean isSjis = true;
		int sjis = 0;
		for (int i = 0; i < len; i++) {
			byte b1 = bytes[i];
			if (b1 <= 0x7F) {//ASCII

			} else if (0xA1 <= b1 && b1 <= 0xDF) {//半角カタカナ

			} else if (i < len - 1) {
				byte b2 = bytes[i + 1];
				//第1バイト: 0x81～0x9F、0xE0～0xFC 第2バイト: 0x40～0x7E、0x80～0xFC
				if (((b1 >= 0x81 && b1 <= 0x9F) || (b1 >= 0xE0 && b1 <= 0xFC)) && ((b2 >= 0x40 && b2 <= 0x7E) || (b2 >= 0x80 && b2 <= 0xFC))) {
					i++;
					sjis++;
				} else {
					isSjis = false;
					break;
				}
			}
		}
		if (!isSjis) {//Shift-JISの可能性なし
			sjis = -1;
		}

		//【EUCの可能性と2バイトコードの出現数カウント】
		boolean isEuc = true;
		int euc = 0;
		for (int i = 0; i < len; i++) {
			byte b1 = bytes[i];
			if (b1 <= 0x7F) {//ASCII

			} else if (i < len - 1) {
				byte b2 = bytes[i + 1];
				if ((b1 >= 0xA1 && b1 <= 0xFE) && (b2 >= 0xA1 && b2 <= 0xFE)) { //漢字
					i++;
					euc++;
				} else if ((b1 == 0x8E) && (b2 >= 0xA1 && b2 <= 0xDF)) { //半角カタカナ
					i++;
					euc++;
				} else if (i < len - 2) {
					byte b3 = bytes[i + 2];
					if ((b1 == 0x8F) && (b2 >= 0xA1 && b2 <= 0xFE) && (b3 >= 0xA1 && b3 <= 0xFE)) { // 補助漢字
						i += 2;
						euc++;
					} else {
						isEuc = false;
						break;
					}
				} else {
					isEuc = false;
					break;
				}
			}
		}
		if (!isEuc)//EUCの可能性なし
			euc = -1;

		//【UTF8の可能性と2バイトコードの出現数カウント】
		boolean isUtf8 = true;
		int utf8 = 0;
		for (int i = 0; i < len; i++) {
			byte b1 = bytes[i];
			if (b1 <= 0x7F) {//ASCII
			} else if (i < len - 1) {
				byte b2 = bytes[i + 1];
				if ((b1 >= 0xC0 && b1 <= 0xDF) && (b2 >= 0x80 && b2 <= 0xBF)) { // 2 バイト 文字
					i += 1;
					utf8++;
				} else if (i < len - 2) {
					byte b3 = bytes[i + 2];
					if (b1 == 0xEF && b2 == 0xBB && b3 == 0xBF) {
						i += 2;
						utf8 += 2;
					} else if ((b1 >= 0xE0 && b1 <= 0xEF) && (b2 >= 0x80 && b2 <= 0xBF) && (b3 >= 0x80 && b3 <= 0xBF)) { // 3バイト文字
						i += 2;
						utf8 += 2;
					} else if (i < len - 3) {
						byte b4 = bytes[i + 3];
						if ((b1 >= 0xF0 && b1 <= 0xF7) && (b2 >= 0x80 && b2 <= 0xBF) && (b3 >= 0x80 && b3 <= 0xBF) && (b4 >= 0x80 && b4 <= 0xBF)) { // 4バイト文字
							i += 3;
							utf8 += 3;
						} else {
							isUtf8 = false;
							break;
						}
					} else {
						isUtf8 = false;
						break;
					}
				}
			}
		}
		if (!isUtf8) {//UTF8の可能性なし
			utf8 = -1;
		}

		if (isSjis) {
			if (sjis > euc && sjis > utf8) {
				return Charset.forName("SHIFT-JIS");
				//return Encoding.GetEncoding(932);
			}
		}
		if (isEuc) {
			if (euc > sjis && euc > utf8) {
				//return Encoding.GetEncoding(51932);
				return Charset.forName("EUC");
			}
		}
		if (isUtf8) {
			if (utf8 > sjis && utf8 > euc) {
				//return Encoding.GetEncoding(65001);
				return Charset.forName("UTF-8");
			}
		}
		return null;

	}
}
