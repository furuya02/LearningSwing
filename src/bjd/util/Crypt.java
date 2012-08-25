package bjd.util;

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
	}
}
