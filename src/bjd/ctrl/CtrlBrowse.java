package bjd.ctrl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import bjd.Kernel;
import bjd.RunMode;
import bjd.net.Ip;
import bjd.option.OneOption;

//CtrlFile及びCtrlFolderの親クラス
public abstract class CtrlBrowse extends OneCtrl{
	
	private JLabel label = null;
	private JTextField textField = null;
	private JButton button = null;
	private int digits;
	private Kernel kernel;

	public CtrlBrowse(String help, int digits, Kernel kernel) {
		super(help);
		this.digits = digits;
		this.kernel = kernel;
	}


	@Override
	public int abstractCreate(int tabIndex) {
		int left = MARGIN;
		int top = MARGIN;


		// ラベルの作成(topの+3は、後のテキストボックスとの高さ調整)
		label = (JLabel) create(panel, new JLabel(help), -1/* tabIndex */, left, top + 3, 0, 0);
		left += label.getWidth() + MARGIN; // オフセット移動

		// テキストボックスの配置
		textField = (JTextField) create(panel, new JTextField(digits), -1/* tabIndex */, left, top, 0, 0);
		OneOption op = kernel.getListOption().get("Basic");
		boolean editBrowse = (boolean) op.getValue("editBrowse");
		if (!editBrowse) {
			textField.setEditable(false); // 読み取り専用
		}


		
		left += textField.getWidth() + MARGIN; // オフセット移動
		
		//ボタンの配置(topの-2は、前のテキストボックスとの高さ調整)
		String buttonText = kernel.getJp() ? "参照" : "Browse";
		button = (JButton) create(panel, new JButton(buttonText), -1/* tabIndex */, left, top - 2, 0, 0);
		
		final CtrlType ctrlType = this.getCtrlType(); 

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (kernel.getRunMode() == RunMode.Remote) {
					//TODO
					//String resultStr = _kernel.RemoteClient.ShowBrowseDlg(_browseType);
					//if (resultStr != null) {
					//   _textBox.Text = resultStr;
					//}
					return;
				}
				JFileChooser dlg = new JFileChooser();
				if (ctrlType == CtrlType.FOLDER) {
					dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
				if (dlg.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = dlg.getSelectedFile();
					textField.setText(file.getPath());
				}
			}
		});

		
		left += button.getWidth() + MARGIN; // オフセット移動
        
        //パネルのサイズ設定
		panel.setSize(left + MARGIN, DEFAULT_HEIGHT);
		
        return tabIndex;
	}
	@Override
	public int abstractDelete() {
		panel.remove(label);
		label = null;
		panel.remove(textField);
		textField = null;
		panel.remove(button);
		button = null;
		return 0;
	}	
}
