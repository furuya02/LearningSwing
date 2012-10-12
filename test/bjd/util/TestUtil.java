package bjd.util;

public final class TestUtil {
	private TestUtil() {
		//デフォルトコンストラクタの隠蔽
	}

	public static void dispHeader(String msg) {

		System.out.println("\n----------------------------------------------------");
		System.out.printf("%s\n", msg);
		System.out.println("----------------------------------------------------");
	}

	public static void dispPrompt(Object o) {
		System.out.printf("%s> ", o.getClass().getName());
	}

	public static void dispPrompt(Object o, String msg) {
		System.out.printf("%s> %s\n", o.getClass().getName(), msg);
	}

	//************************************************
	//コンソール出力用
	//************************************************
	public static String toString(byte[] buf) {
		StringBuilder sb = new StringBuilder();
		if (buf == null) {
			sb.append("null");
		} else {
			for (byte b : buf) {
				sb.append(String.format("0x%02x ", b & 0xFF));
			}
		}
		return sb.toString();
	}

	public static String toString(String str) {
		str = str.replaceAll("\r", "/r");
		str = str.replaceAll("\n", "/n");
		return str;
	}
}
