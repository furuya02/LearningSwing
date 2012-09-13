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
}

