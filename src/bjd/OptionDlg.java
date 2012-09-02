package bjd;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import bjd.option.OneOption;

@SuppressWarnings("serial")
public class OptionDlg extends Dlg {

	private static final int DLG_WIDTH = 600;
	private static final int DLG_HEIGHT = 600;
	private OneOption oneOption;

	public OptionDlg(JFrame frame, OneOption oneOption) {
		super(frame, DLG_WIDTH, DLG_HEIGHT);

		this.oneOption = oneOption;

		//ダイアログ作成時の処理
		oneOption.createDlg(mainPanel);
		
		
//		JList listBox = new JList(new String[]{"1","2","3","4","5","6","7","8","9","10"});
//		JScrollPane srl = new JScrollPane(listBox);
//		srl.setSize(100,100);
//		mainPanel.add(srl);

		/*
		 * //メニューの項目名をダイアログのタイトルにする var text =
		 * kernel.Jp?oneOption.JpMenu:oneOption.EnMenu;
		 * 
		 * var index = text.LastIndexOf(','); Text = index != 0 ?
		 * text.Substring(index+1) : text; //(&R)のようなショートカット指定を排除する index =
		 * Text.IndexOf('('); if (0 <= index) { Text = Text.Substring(0, index);
		 * } //&を排除する Text = Util.SwapChar('&','\b',Text);
		 * 
		 * _oneOption = oneOption; oneOption.DlgCreate(panelMain);
		 * 
		 * buttonCancel.Text = (kernel.Jp) ? "キャンセル" : "Cancel";
		 */
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		//ダイアログ破棄時の処理
		oneOption.deleteDlg();
	}

	//ダイアログでOKボタンが押された時の処理
	@Override
	protected boolean onOk() {
		boolean isComfirm = true; // コントロールのデータが全て正常に読めるかどうかの確認(エラーの場合は、ポップアップ表示)
		if (!oneOption.onOk(isComfirm)) {
			return false;
		}
		oneOption.onOk(false); //値の読み込み
		return true;
	}
}
