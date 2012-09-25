package bjd.util;

public final class Debug {

	public static void print(Object o, String str) {
		System.out.println(String.format("[%3d] %s %s", Thread.currentThread().getId(), o.getClass().getSimpleName(), str));
	}
}
