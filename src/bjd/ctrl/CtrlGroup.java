package bjd.ctrl;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import bjd.option.ListVal;

public class CtrlGroup extends OneCtrl {

	private JPanel border = null;
	private ListVal listVal;
	private int width;

	public CtrlGroup(String help, ListVal listVal, int width) {
		super(help);
		this.listVal = listVal;
		this.width = width;
	}

	public ListVal getListVal() {
		return listVal;
	}
	

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.GROUP;
	}

	@Override
	protected void abstractCreate(Object value) {

		int left = margin;
		int top = margin;

		// ボーダライン（groupPanel）の生成
		border = (JPanel) create(panel, new JPanel(new GridLayout(0, 1)), left, top);
		border.setBorder(BorderFactory.createTitledBorder(help));

		//グループに含まれるコントロールを描画する
		int x = left + 8;
		int y = top + 12;
		listVal.createCtrl(border, x, y);
		CtrlSize ctrlSize = listVal.getCtrlSize();

		// borderのサイズ指定
		border.setSize(width, ctrlSize.getHeight() + 25); // 横はコンストラクタ、縦は、含まれるコントロールで決まる

		// オフセット移動
		left += border.getWidth();
		top += border.getHeight();

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		//panel.setSize(left + width + margin, top + height + margin * 2);
		panel.setSize(left + margin, top + margin);
	}

	@Override
	protected void abstractDelete() {
		listVal.deleteCtrl(); //これが無いと、グループの中のコントロールが２回目以降表示されなくなる

		remove(panel, border);
		border = null;
	}

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
		if (border != null) {
			border.setEnabled(enabled);
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
		throw new UnsupportedOperationException("CtrlGroup.java abstractToText()は使用禁止");
	}

	@Override
	protected void abstractFromText(String s) {
		throw new UnsupportedOperationException("CtrlGroup.java abstractFromText()は使用禁止");
	}

	@Override
	protected void abstractClear() {
	}
	
}
