package bjd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

public class Crypt {
/*
	private Crypt() {
	}// デフォルトコンストラクタの隠蔽

	static byte[] _key;
	static byte[] _iv;

	static void init() {
		final String password = "password";
		_key = new byte[32];
		_iv = new byte[16];
		int len = password.length();
		for (int i = 0; i < 32; i++) {
			_key[i] = (byte) password.charAt(i % len);
		}
		for (int i = 0; i < 16; i++) {
			_iv[i] = (byte) password.charAt(i % len);

		}
	}

	static public String encrypt(String str) {

		init();

		try {
			byte[] src = str.getBytes();
			// byte[] src = Encoding.Unicode.GetBytes(str);

			var aes = new RijndaelManaged();
			var ms = new MemoryStream();
			var cs = new CryptoStream(ms, aes.CreateEncryptor(_key, _iv), CryptoStreamMode.Write);
			cs.Write(src, 0, src.Length);
			cs.FlushFinalBlock();
			byte[] dest = ms.ToArray();

			String encodedString = encodeString(dest);
			// return Convert.ToBase64String(dest);

		} catch (Exception ex) {
			return "ERROR";
		}
	}

	static public String decrypt(String str) {

		init();

		try {
			var src = Convert.FromBase64String(str);

			var aes = new RijndaelManaged();
			var ms = new MemoryStream();
			var cs = new CryptoStream(ms, aes.CreateDecryptor(_key, _iv), CryptoStreamMode.Write);
			cs.Write(src, 0, src.Length);
			cs.FlushFinalBlock();
			var dest = ms.ToArray();

			return Encoding.Unicode.GetString(dest);

		} catch (Exception ex) {
			return null;
		}
	}
	*/
}
