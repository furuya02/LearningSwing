package bjd.ctrl;
/**
 * 
 * 各Ctrlオブジェクトのサイズを表現するクラス
 * 
 */
public class CtrlSize {
	private int width;
	private int height;

	public CtrlSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
