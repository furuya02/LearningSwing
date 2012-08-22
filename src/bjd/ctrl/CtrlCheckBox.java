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
		checkBox = (JCheckBox) create(panel, new JCheckBox(), -1/* tabIndex */, left, top, 0, 0);
		checkBox.setText(help); 
		setAutoSize(checkBox); //サイズ自動調整
        
		//オフセット移動
		left += checkBox.getWidth() + MARGIN;
		
        //パネルのサイズ設定
		panel.setSize(left + MARGIN, DEFAULT_HEIGHT);
		return tabIndex;
	}

}
