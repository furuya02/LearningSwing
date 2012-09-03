package bjd.ctrl;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import bjd.option.ListVal;

public class CtrlPage extends OneCtrl {

	private JPanel page = null;
	private ListVal listVal;

	public CtrlPage(String help, ListVal listVal) {
		super(help);
		this.listVal = listVal;
	}

	public ListVal getListVal() {
		return listVal;
		
	}
	

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.PAGE;
	}

	@Override
	protected void abstractCreate(Object value) {

		int left = margin;
		int top = margin;

		// パネル（Panel）の生成
		page = (JPanel) create(panel, new JPanel(), left, top);

		//グループに含まれるコントロールを描画する
		listVal.createCtrl(page, left,top);
		CtrlSize ctrlSize = listVal.getCtrlSize();

		// borderのサイズ指定
		//border.setSize(width, ctrlSize.getHeight() + 25); // 横はコンストラクタ、縦は、含まれるコントロールで決まる

		// オフセット移動
		left += page.getWidth();
		top += page.getHeight();

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, top + margin);
	}

	@Override
	protected void abstractDelete() {
		listVal.deleteCtrl(); //これが無いと、グループの中のコントロールが２回目以降表示されなくなる

		remove(panel, page);
		page = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		listVal.readCtrl(false);
		return 0; //nullを返すと無効値になってしまうのでダミー値(0)を返す
	}

	@Override
	protected void abstractWrite(Object value) {

	}
	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (page != null) {
			page.setEnabled(enabled);
		}
	}
	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	// 必要なし
	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		return true;
	}

	@Override
	protected String abstractToText() {
		throw new UnsupportedOperationException("CtrlPage.java abstractToText()は使用禁止");
	}

	@Override
	protected void abstractFromText(String s) {
		throw new UnsupportedOperationException("CtrlPage.java abstractFromText()は使用禁止");
	}

	@Override
	protected void abstractClear() {
	}
	
}
