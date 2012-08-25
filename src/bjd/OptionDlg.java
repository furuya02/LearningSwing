package bjd;

import javax.swing.JFrame;
import javax.swing.JCheckBox;

import bjd.option.OneOption;

@SuppressWarnings("serial")
public class OptionDlg extends Dlg {

	private static final int DLG_WIDTH = 600;
	private static final int DLG_HEIGHT = 400;

	public OptionDlg(JFrame frame, OneOption oneOption) {
		super(frame, DLG_WIDTH, DLG_HEIGHT);

		oneOption.createDlg(mainPanel);

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
}
