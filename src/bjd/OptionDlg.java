package bjd;

import javax.swing.JFrame;
import javax.swing.JCheckBox;

import bjd.option.OneOption;

@SuppressWarnings("serial")
public class OptionDlg extends Dlg {

	private static final int DLG_WIDTH = 500;
	private static final int DLG_HEIGHT = 400;

	public OptionDlg(JFrame frame, OneOption oneOption) {
		super(frame, DLG_WIDTH, DLG_HEIGHT);

		oneOption.createDlg(mainPanel);

		// getContentPane()

		JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
		// chckbxNewCheckBox.setBounds(8, 6, 103, 21);
		chckbxNewCheckBox.setSize(120, 21);
		chckbxNewCheckBox.setLocation(30, 10);
		// getContentPane().add(chckbxNewCheckBox);
		mainPanel.add(chckbxNewCheckBox);

		/*
		 * //���j���[�̍��ږ����_�C�A���O�̃^�C�g���ɂ��� var text =
		 * kernel.Jp?oneOption.JpMenu:oneOption.EnMenu;
		 * 
		 * var index = text.LastIndexOf(','); Text = index != 0 ?
		 * text.Substring(index+1) : text; //(&R)�̂悤�ȃV���[�g�J�b�g�w���r������ index =
		 * Text.IndexOf('('); if (0 <= index) { Text = Text.Substring(0, index);
		 * } //&��r������ Text = Util.SwapChar('&','\b',Text);
		 * 
		 * _oneOption = oneOption; oneOption.DlgCreate(panelMain);
		 * 
		 * buttonCancel.Text = (kernel.Jp) ? "�L�����Z��" : "Cancel";
		 */

	}
}
