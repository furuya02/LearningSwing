package bjd.ctrl;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;


public class CtrlInt extends OneCtrl {

	private JLabel label = null;
	private JTextField textField = null;
	private int digits;
	
	public CtrlInt(String help, int digits) {
		super(help);
		this.digits = digits;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.INT;
	}

	@Override
	public int abstractDelete() {
		panel.remove(label);
		label = null;
		panel.remove(textField);
		textField = null;
		
		return 0;
	}

	@Override
	public int abstractCreate(int tabIndex) {
		int left = MARGIN;
		int top = MARGIN;

		//ラベルの作成
		label = (JLabel) create(panel, new JLabel(help), -1/* tabIndex */, left, top + 3 /* +3 は後のテキストボックスとの整合のため*/, 0, 0);
		//label.setBorder(new LineBorder(Color.RED, 2, true)); //Debug 赤枠
		left += label.getWidth() + MARGIN; //オフセット移動
		
		//テキストボックスの配置
		textField = (JTextField) create(panel, new JTextField(digits), -1/* tabIndex */, left, top, 0, 0);
		((AbstractDocument) textField.getDocument()).setDocumentFilter(new IntegerDocumentFilter(digits));
		left += textField.getWidth(); //オフセット移動

        //パネルのサイズ設定
		panel.setSize(left + MARGIN, DEFAULT_HEIGHT);
		return tabIndex;
	}
	
	//コントロールの値の読み込み
	@Override
	public Object abstractRead() {
		String str = textField.getText();
		if (str.equals("")) {
			str = "0";
		}
		try {
			return Integer.valueOf(str);
		} catch (Exception e) {
			System.out.println(String.format("■ %s",e));
			return 0;
		}
	}

	//数値入力に制限する
	class IntegerDocumentFilter extends DocumentFilter {
		private int digits;

		public IntegerDocumentFilter(int digits) {
			this.digits = digits;
		}

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			if (string == null) {
				return;
			} else {
				replace(fb, offset, 0, string, attr);
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			replace(fb, offset, length, "", null);
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			Document doc = fb.getDocument();
			int currentLength = doc.getLength();
			String currentContent = doc.getText(0, currentLength);
			String before = currentContent.substring(0, offset);
			String after = currentContent.substring(length + offset, currentLength);
			String newValue = before + (text == null ? "" : text) + after;

			if (newValue.length() > digits) { // 桁オーバー
				return;
			}

			checkInput(newValue, offset);
			fb.replace(offset, length, text, attrs);
		}

		private int checkInput(String str, int offset) throws BadLocationException {
			int ret = 0;
			if (str.length() > 0) {
				try {
					ret = Integer.parseInt(str);
				} catch (NumberFormatException e) {
					throw new BadLocationException(str, offset);
				}
			}
			return ret;
		}
	}

}
