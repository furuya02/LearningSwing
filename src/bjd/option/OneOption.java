package bjd.option;

import javax.swing.JPanel;


public class OneOption {

	private ListVal listVal = new ListVal();

	public void createDlg(JPanel mainPanel) {
		//�\���J�n�̊�ʒu
		int x = 0;
		int y = 0;
		int tabIndex = 0;
		listVal.createCtrl(mainPanel, x, y, tabIndex);
	}

	public void add(OneVal oneVal) {
		listVal.add(oneVal);
	}
}
