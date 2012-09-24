package bjd.util;

public final class Debug {
    
    public static void print(String str) {
        System.out.println(String.format("[ThreadId=%d] %s", Thread
                .currentThread().getId(), str));
    }
}
