package bjd.option;

import javax.swing.JPanel;

import bjd.Kernel;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.ICtrlEventListener;
import bjd.ctrl.OneCtrl;

public abstract class OneOption implements ICtrlEventListener {

	private ListVal listVal = new ListVal();

	Kernel kernel;

	public OneOption(Kernel kernel) {
		this.kernel = kernel;
	}

	// ダイアログ作成時の処理
	public final void createDlg(JPanel mainPanel) {
		// 表示開始の基準位置
		int x = 0;
		int y = 0;
		listVal.createCtrl(mainPanel, x, y);
		listVal.setListener(this);

		// コントロールの状態を初期化するために、ダミーのイベントを発生させる
		onChange(new CtrlCheckBox("dmy"));
	}

	// ダイアログ破棄時の処理
	public final void deleteDlg() {
		listVal.deleteCtrl();
	}

	// ダイアログでOKボタンが押された時の処理
	public final boolean onOk(boolean isComfirm) {
		return listVal.readCtrl(isComfirm);
	}

	public final void add(OneVal oneVal) {
		listVal.add(oneVal);
	}

	public final Object getValue(String name) {
		// TODO DEBUG
		if (name.equals("editBrowse")) {
			return false;
		}
		// 未実装
		return null;
	}

	protected final OneCtrl getCtrl(String name) {
		OneVal oneVal = listVal.search(name);
		if (oneVal != null) {
			return oneVal.getOneCtrl();
		}
		throw new UnsupportedOperationException(String.format(
				"OneOption.java getCtrl() 設計に問題があります( %sは存在しません )", name));
	}

	protected abstract void abstractOnChange(OneCtrl oneCtrl);

	@Override
	public final void onChange(OneCtrl oneCtrl) {
		try {
			abstractOnChange(oneCtrl);
		} catch (NullPointerException e) {
			// コントロールの破棄後に、このイベントが発生した場合（この例外は無視する）
		}
	}

}
