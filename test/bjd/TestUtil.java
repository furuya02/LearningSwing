package bjd;

public final class TestUtil {
	private TestUtil() {
		//�f�t�H���g�R���X�g���N�^�̉B��
	}
	
	public static void dispHeader(String msg) {
		System.out.println("\n----------------------------------------------------");
		System.out.printf("%s\n", msg);
		System.out.println("----------------------------------------------------");
	}
	
	public static void dispPrompt(Object o) {
		System.out.printf("%s> ", o.getClass().getName());
	}
}

