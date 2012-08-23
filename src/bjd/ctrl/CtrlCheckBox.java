package bjd.ctrl;


import javax.swing.JCheckBox;


public class CtrlCheckBox extends OneCtrl {
	
	private JCheckBox checkBox = null;
	
	public CtrlCheckBox(String help) {
		super(help);
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.CHECKBOX;
	}

	@Override
	public int abstractCreate(int tabIndex) {
		int left = MARGIN;
		int top = MARGIN;

		//チェックボックス作成
		checkBox = (JCheckBox) create(panel, new JCheckBox(help), -1/* tabIndex */, left, top, 0, 0);
		left += checkBox.getWidth() + MARGIN; //オフセット移動
		
        //パネルのサイズ設定
		panel.setSize(left + MARGIN, DEFAULT_HEIGHT);
		return tabIndex;
	}

	@Override
	public int abstractDelete() {
		panel.remove(checkBox);
		checkBox = null;
		return 0;
	}

}
