package bjd.ctrl;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import say.swing.JFontChooser;

import bjd.Kernel;
import bjd.RunMode;
import bjd.option.OneOption;

public class CtrlFont extends OneCtrl implements ActionListener {

	private Kernel kernel;
	private JLabel label;
	private JButton button;
	private Font font = null;

	public CtrlFont(String help, Kernel kernel) {
		super(help);
		this.kernel = kernel;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FONT;
	}

	@Override
	protected void abstractCreate(Object value) {
		int left = margin;
		int top = margin;

		// ラベルの作成(topの+3は、後のテキストボックスとの高さ調整)
		label = (JLabel) create(panel, new JLabel(help), left, top + 3);
		left += label.getWidth() + margin; // オフセット移動

		// ボタンの配置(topの-2は、前のテキストボックスとの高さ調整)
		String buttonText = kernel.getJp() ? "フォント" : "Font";
		button = (JButton) create(panel, new JButton(buttonText), left, top - 3);

		button.addActionListener(this);
		//		button.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				JFontChooser dlg = new JFontChooser();
		//				if (font != null) {
		//					dlg.setSelectedFont(font);
		//				}
		//				if (JFontChooser.OK_OPTION == dlg.showDialog(panel)) {
		//					font = dlg.getSelectedFont();
		//					System.out.println("Selected Font : " + font);
		//					
		//					setOnChange();//コントロールの変換
		//				}
		//			}
		//		});

		left += button.getWidth() + margin; // オフセット移動

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, defaultHeight + margin);
	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		remove(panel, button);
		label = null;
		button = null;
	}

	@Override
	protected Object abstractRead() {
		return font;
	}

	@Override
	protected void abstractWrite(Object value) {
		font = (Font) value;
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		button.setEnabled(enabled);
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void actionPerformed(ActionEvent e) {
		JFontChooser dlg = new JFontChooser();
		if (font != null) {
			dlg.setSelectedFont(font);
		}
		if (JFontChooser.OK_OPTION == dlg.showDialog(panel)) {
			font = dlg.getSelectedFont();
			System.out.println("Selected Font : " + font);

			setOnChange(); //コントロールの変換
		}
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		return (font == null) ? false : true;
	}

	@Override
	protected String abstractToText() {
		throw new UnsupportedOperationException("CtrlFont.java abstractToText()は未実装");
	}

	@Override
	protected void abstractFromText(String s) {
		throw new UnsupportedOperationException("CtrlFont.java abstractFromText()は未実装");
	}

	@Override
	protected void abstractClear() {
		throw new UnsupportedOperationException("CtrlFont.java abstractClear()は未実装");
	}

}
