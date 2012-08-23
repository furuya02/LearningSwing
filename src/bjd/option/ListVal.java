package bjd.option;
import javax.swing.JPanel;

import bjd.ctrl.CtrlSize;
import bjd.ctrl.OneCtrl;
import bjd.util.ListBase;

public class ListVal extends ListBase<OneVal> {

	private CtrlSize ctrlSize = null;
	
	public void add(OneVal oneVal) {
		ar.add(oneVal);
	}
	
	public int createCtrl(JPanel mainPanel, int baseX, int baseY , int tabIndex) {
		
		//�I�t�Z�b�g�v�Z�p
		int x = baseX;
		int y = baseY;
		int h = 0; //�P�s�̒��ň�Ԕw�̍����I�u�W�F�N�g�̍�����ێ�����E
		int w = 0; //x�I�t�Z�b�g�̍ő�l��ێ�����
		for (OneVal o : ar) {
			tabIndex = o.createCtrl(mainPanel, x, y, tabIndex);

			//���ׂẴR���g���[�����쐬�������T�C�Y�����߂�
			CtrlSize ctrlSize = o.getCtrlSize();
			if (h < ctrlSize.getHeight()) {
				h = ctrlSize.getHeight();
			}
			if (o.getCrlf() == Crlf.NEXTLINE) {
				y += h;
				x = baseX;
				h = 0;
			} else {
				x += ctrlSize.getWidth();
				if (w < x) {
					w = x;
				}
			}
		}
		//�J�n�ʒu����ړ������I�t�Z�b�g�ŁA����ListVal�I�u�W�F�N�g��width,height���Z�o����
		ctrlSize = new CtrlSize(w - baseX, y - baseY + h);
		
		return tabIndex;
	}
	public void deleteCtrl(){
		for (OneVal o : ar) {
			o.deleteCtrl();
		}		
	}
	public CtrlSize getCtrlSize() {
		if (ctrlSize == null) {
			throw new ExceptionInInitializerError();
		}
		return ctrlSize;
	}
}
