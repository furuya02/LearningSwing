package bjd.ctrl;

import javax.swing.JLabel;
import javax.swing.JTextField;

//CtrlFile�y��CtrlFolder�̐e�N���X
public abstract class CtrlBrowse extends OneCtrl {

	private JTextField textField = null;
	private int width;

	public CtrlBrowse(String help, int width) {
		super(help);
		this.width = width;
	}

	@Override
	public int abstractCreate(int tabIndex) {
		int left = MARGIN;
		int top = MARGIN;

		// ���x���̍쐬(top��+3�́A��̃e�L�X�g�{�b�N�X�Ƃ̍�������)
		JLabel label = (JLabel) create(panel, new JLabel(), -1/* tabIndex */, left, top + 3, 0, 0);
		label.setText(help);
		setAutoSize(label); // �T�C�Y��������
		// label.setBorder(new LineBorder(Color.RED, 2, true)); //Debug �Ԙg

		// �I�t�Z�b�g�ړ�
		left += label.getWidth() + MARGIN;

		// �e�L�X�g�{�b�N�X�̔z�u
		textField = (JTextField) create(panel, new JTextField(), -1/* tabIndex */, left, top, 0, 0);
		textField.setSize(width, 23);
		textField.setEditable(false); // �ǂݎ���p
		// var op = _kernel.ListOption.Get("Basic");
		// var editBrowse = (bool)op.GetValue("editBrowse");
		// _textBox.ReadOnly = !(editBrowse);

		// �I�t�Z�b�g�ړ�
		left += textField.getWidth() + MARGIN;
      	
//      	//�{�^���̔z�u
//        _button = (Button)Create(Panel, new Button(), tabIndex++, left, top, 60, DefaultH);
//        _button.Text = _kernel.Jp ? "�Q��" : "Browse";
//        _button.Click += ButtonClick;
//
//        //�p�l���̃T�C�Y�ݒ�
//        Panel.Width = left + _button.Width + Margin;
//        Panel.Height = DefaultH;
        
        //�p�l���̃T�C�Y�ݒ�
		panel.setSize(left + MARGIN, DEFAULT_HEIGHT);
		
        return tabIndex;
	}
}
