package bjd.ctrl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bjd.Kernel;
import bjd.RunMode;
import bjd.option.OneOption;

//CtrlFile及びCtrlFolderの親クラス
public abstract class CtrlBrowse extends OneCtrl implements DocumentListener {

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
	protected void abstractCreate(Object value) {
		int left = margin;
		int top = margin;

		// ラベルの作成(topの+3は、後のテキストボックスとの高さ調整)
		label = (JLabel) create(panel, new JLabel(help), left, top + 3);
		left += label.getWidth() + margin; // オフセット移動

		// テキストボックスの配置
		textField = (JTextField) create(panel, new JTextField(digits), left, top);
		textField.getDocument().addDocumentListener(this);
		OneOption op = kernel.getListOption().get("Basic");
		boolean editBrowse = (boolean) op.getValue("editBrowse");
		if (!editBrowse) {
			textField.setEditable(false); // 読み取り専用
			textField.setFocusable(false);
		}

		left += textField.getWidth() + margin; // オフセット移動

		// ボタンの配置(topの-2は、前のテキストボックスとの高さ調整)
		String buttonText = kernel.getJp() ? "参照" : "Browse";
		button = (JButton) create(panel, new JButton(buttonText), left, top - 3);

		final CtrlType ctrlType = this.getCtrlType();

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (kernel.getRunMode() == RunMode.Remote) {
					// TODO　リモート制御（CtrlBrowseでのボタンを押したときの処理）
					// String resultStr =
					// _kernel.RemoteClient.ShowBrowseDlg(_browseType);
					// if (resultStr != null) {
					// _textBox.Text = resultStr;
					// }
					return;
				}
				String s = textField.getText();
				JFileChooser dlg = new JFileChooser(s);
				if (ctrlType == CtrlType.FOLDER) {
					dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
				if (dlg.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = dlg.getSelectedFile();
					textField.setText(file.getPath());
				}
			}
		});

		left += button.getWidth() + margin; // オフセット移動

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, defaultHeight + margin);

	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		remove(panel, textField);
		remove(panel, button);
		label = null;
		textField = null;
		button = null;
	}

	@Override
	protected Object abstractRead() {
		return textField.getText();
	}

	@Override
	protected void abstractWrite(Object value) {
		textField.setText((String) value);
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (textField != null) {
			textField.setEditable(enabled);
		}
		if (button != null) {
			button.setEnabled(enabled);
		}
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		setOnChange();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		setOnChange();
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		if (textField.getText().equals("")) {
			return false;
		}
		return true;
	}

	@Override
	protected String abstractToText() {
		return textField.getText();
	}

	@Override
	protected void abstractFromText(String s) {
		textField.setText(s);
	}

	@Override
	protected void abstractClear() {
		textField.setText("");
	}

}
