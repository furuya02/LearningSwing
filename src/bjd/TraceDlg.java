package bjd;

import java.awt.Dialog.ModalityType;

import javax.swing.JFrame;

import bjd.util.IDispose;

public class TraceDlg extends Dlg implements IDispose{

	private static int width = 300;
	private static int height = 300;
	private Kernel kernel;
	public TraceDlg(Kernel kernel,JFrame frame) {
		super(frame, width,height);
		this.kernel = kernel;
	}

	@Override
	public void dispose() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public void Open() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	protected boolean onOk() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}
