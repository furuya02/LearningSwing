package bjd;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Hex;

public final class Crypt {

	private Crypt() {
		// デフォルトコンストラクタの隠蔽
	}

	private static String key = "ABCDEFGHIJKLMNOPQRSTUVWX"; //キー(24バイト)
	
	public static String encrypt(String str) {
		try {
			SecretKeyFactory keyFac = SecretKeyFactory.getInstance("DESede");
			DESedeKeySpec keySpec = new DESedeKeySpec(key.getBytes());
			SecretKey secKey = keyFac.generateSecret(keySpec);

			Cipher encoder = Cipher.getInstance("DESede");
			encoder.init(Cipher.ENCRYPT_MODE, secKey);
			byte[] b = encoder.doFinal(str.getBytes());
			return Hex.encodeHexString(b);
		} catch (Exception ex) {
			return "ERROR";
		}
	}

	public static String decrypt(String str) {
		if (str == null || str.equals("")) {
			return "ERROR";
		}
	try {
			SecretKeyFactory keyFac = SecretKeyFactory.getInstance("DESede");
			DESedeKeySpec keySpec = new DESedeKeySpec(key.getBytes());
			SecretKey secKey = keyFac.generateSecret(keySpec);

			Cipher decoder = Cipher.getInstance("DESede");
			decoder.init(Cipher.DECRYPT_MODE, secKey);

			byte[] b = Hex.decodeHex(str.toCharArray());
			return new String(decoder.doFinal(b));
		} catch (Exception ex) {
			return "ERROR";
		}
		//System.out.println(new String(decoder.doFinal(b)));	}
	}
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
