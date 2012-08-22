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

		//�`�F�b�N�{�b�N�X�쐬
		checkBox = (JCheckBox) create(panel, new JCheckBox(), -1/* tabIndex */, left, top, 0, 0);
		checkBox.setText(help); 
		setAutoSize(checkBox); //�T�C�Y��������
        
		//�I�t�Z�b�g�ړ�
		left += checkBox.getWidth() + MARGIN;
		
        //�p�l���̃T�C�Y�ݒ�
		panel.setSize(left + MARGIN, DEFAULT_HEIGHT);
		return tabIndex;
	}

}
