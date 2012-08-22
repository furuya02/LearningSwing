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
	public int abstractCreate(int tabIndex) {
		int left = MARGIN;
		int top = MARGIN;

		//���x���̍쐬
		JLabel label = (JLabel) create(panel, new JLabel(), -1/* tabIndex */, left, top + 3 /* +3 �͌�̃e�L�X�g�{�b�N�X�Ƃ̐����̂���*/, 0, 0);
		label.setText(help); 
		setAutoSize(label); //�T�C�Y��������
		//label.setBorder(new LineBorder(Color.RED, 2, true)); //Debug �Ԙg
        
		//�I�t�Z�b�g�ړ�
		left += label.getWidth() + MARGIN;
		
		//�e�L�X�g�{�b�N�X�̔z�u
		textField = (JTextField) create(panel, new JTextField(), -1/* tabIndex */, left, top, 0, 0);
		((AbstractDocument) textField.getDocument()).setDocumentFilter(new IntegerDocumentFilter(digits));
        textField.setColumns(digits);
		setAutoSize(textField); //�T�C�Y��������

		//�I�t�Z�b�g�ړ�
		left += textField.getWidth();

        //�p�l���̃T�C�Y�ݒ�
		panel.setSize(left + MARGIN, DEFAULT_HEIGHT);
		return tabIndex;
	}

	//���l���͂ɐ�������
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

			if (newValue.length() > digits) { // ���I�[�o�[
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
