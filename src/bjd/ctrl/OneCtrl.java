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
	private JPanel owner;
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
    
    //コントロールの生成
    public abstract int abstractCreate(int tabIndex);
    public int create(JPanel owner, int x, int y, int tabIndex) {
    	this.owner = owner;
        //this.oneVal = oneVal;
        if (panel == null) {
            //if (GetType() == CtrlType.TabPage) {
            //    //TabPageの場合は、TabControlを親パネルとする
            //    Panel = owner;
            //} else {
                //全部のコントロールを包み込むパネル(Width,HJeightは後で子クラスの_create()で修正される)
                panel = (JPanel) create(owner, new JPanel(), -1/*tabIndex*/, x, y, 0, 0);
        		
                //Debug 色付ける 
                Random r = new Random();
                Color bc = new Color(r.nextInt(205), r.nextInt(205), r.nextInt(205));
                panel.setBackground(bc);
            //}
            //panelの上に独自コントロールを配置する
            return abstractCreate(tabIndex);
        }
        return tabIndex;
    }
    //コントロールの破棄
    public abstract int abstractDelete();
    public void delete() {
        if (panel != null) {
        	abstractDelete();
        	owner.remove(panel);
        	panel = null;
            //CtrlTabPageの時は、PanelはTabControlを指しているので破棄できない
            //if (GetType() != CtrlType.TabPage) {
            //    Panel.Dispose();
            //}
            //Panel = null;
        }    	
    }

    //コントロールからの値の読み込み
    public abstract Object abstractRead();
	public Object read() {
        return abstractRead();
	}

	
    //DOTO int w,hは不要ではないか
	//コントロール生成の共通作業関数
    protected JComponent create(JPanel owner, JComponent self, int tabIndex, int x, int y, int w, int h) {
    	JComponent control = self;
    	control.setLocation(x, y);
    	control.setSize(w, h);
    	setAutoSize(control); // サイズ自動調整(この時点でテキストが適切に設定されているばあ、これでサイズの調整は終わる)
    	
    	control.setLayout(null); //　子コントロールを絶対位置で表示する
//    	if (tabIndex == -1) {
//    		control.TabStop = false;
//    	} else {
//    		control.TabIndex = tabIndex;
//    	}
    	owner.add(control);
    	control.setFont(owner.getFont()); //フォントの継承
    	return control;
    }

    //フィールドテキストに合わせてサイズを自動調整する
	protected void setAutoSize(JComponent component) {
		Dimension dimension = component.getPreferredSize(); //適切サイズを取得
		dimension.width += 8; //微調整
		component.setSize(dimension);
    }
    
}
