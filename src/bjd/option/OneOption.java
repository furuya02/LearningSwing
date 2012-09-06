package bjd.option;

import javax.swing.JPanel;


public class OneOption {

	private ListVal listVal = new ListVal();

	//ダイアログ作成時の処理
	public void createDlg(JPanel mainPanel) {
		//表示開始の基準位置
		int x = 0;
		int y = 0;
		listVal.createCtrl(mainPanel, x, y);
	}
	//ダイアログ破棄時の処理
	public void deleteDlg() {
		listVal.deleteCtrl();
	}
	//ダイアログでOKボタンが押された時の処理 
	public boolean onOk(boolean isComfirm) {
		return listVal.readCtrl(isComfirm);
	}
	public void add(OneVal oneVal) {
		listVal.add(oneVal);
	}
	public Object getValue(String name) {
		//TODO DEBUG
		if (name.equals("editBrowse")) {
			return false;
		}
		//未実装
		return null;
	}
}
