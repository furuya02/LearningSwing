package bjd.option;

import javax.swing.JPanel;


public class OneOption {

	private ListVal listVal = new ListVal();

	public void createDlg(JPanel mainPanel) {
		//表示開始の基準位置
		int x = 0;
		int y = 0;
		int tabIndex = 0;
		listVal.createCtrl(mainPanel, x, y, tabIndex);
	}

	public void add(OneVal oneVal) {
		listVal.add(oneVal);
	}
}
