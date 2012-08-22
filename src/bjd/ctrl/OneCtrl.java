package bjd.ctrl;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bjd.option.OneVal;

public abstract class OneCtrl {
	protected String help;
	//private OneVal oneVal;
	protected JPanel panel = null;
	protected final int MARGIN = 3;
	protected final int DEFAULT_HEIGHT = 28;
	
	public OneCtrl(String help) {
		this.help = help;
	}
	
	//public OneVal getOneVal() {
	//	return oneVal;
	//}
	
	public void setOneVal(OneVal value) {
		//oneVal = value;
	}
	
	public CtrlSize getCtrlSize() {
		return new CtrlSize(panel.getWidth(), panel.getHeight());
	}

    public abstract CtrlType getCtrlType();
    
    
    public abstract int abstractCreate(int tabIndex);
    public int create(JPanel owner, int x, int y, int tabIndex) {
        //this.oneVal = oneVal;
        if (panel == null) {
            //if (GetType() == CtrlType.TabPage) {
            //    //TabPage�̏ꍇ�́ATabControl��e�p�l���Ƃ���
            //    Panel = owner;
            //} else {
                //�S���̃R���g���[�����ݍ��ރp�l��(Width,HJeight�͌�Ŏq�N���X��_create()�ŏC�������)
                panel = (JPanel) create(owner, new JPanel(), -1/*tabIndex*/, x, y, 0, 0);
        		
                //Debug �F�t���� 
                Random r = new Random();
                Color bc = new Color(r.nextInt(205), r.nextInt(205), r.nextInt(205));
                panel.setBackground(bc);
            //}
            //panel�̏�ɓƎ��R���g���[����z�u����
            return abstractCreate(tabIndex);
        }
        return tabIndex;
    }

    //DOTO int w,h�͕s�v�ł͂Ȃ���
	//�R���g���[�������̋��ʍ�Ɗ֐�
    protected JComponent create(JPanel owner, JComponent self, int tabIndex, int x, int y, int w, int h) {
    	JComponent control = self;
    	control.setLocation(x, y);
    	control.setSize(w, h);
    	control.setLayout(null); //�@�q�R���g���[�����Έʒu�ŕ\������
//    	if (tabIndex == -1) {
//    		control.TabStop = false;
//    	} else {
//    		control.TabIndex = tabIndex;
//    	}
    	owner.add(control);
    	control.setFont(owner.getFont()); //�t�H���g�̌p��
    	return control;
    }

    //�t�B�[���h�e�L�X�g�ɍ��킹�ăT�C�Y��������������
	protected void setAutoSize(JComponent component) {
		Dimension dimension = component.getPreferredSize(); //�K�؃T�C�Y���擾
		dimension.width += 8; //������
		component.setSize(dimension);
    }
    
}
