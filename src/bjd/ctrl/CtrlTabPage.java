package bjd.ctrl;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import bjd.option.ListVal;

public class CtrlTabPage extends OneCtrl {
	private ListVal listVal;
	private JTabbedPane tabbedPane = null;
	
	public CtrlTabPage(String help, ListVal listVal) {
		super(help);
		this.listVal = listVal;
	}

	public ListVal getListVal() {
		return listVal;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.TABPAGE;
	}

	@Override
	protected void abstractCreate(Object value) {

        int left = margin;
        int top = margin;

		tabbedPane = (JTabbedPane) create(panel, new JTabbedPane(), left, top);
        tabbedPane.setSize(300,300);
        
        panel.add(tabbedPane);
        JPanel page1 = new JPanel();
        tabbedPane.addTab("page1",page1);
		
        
//        //グループに含まれるコントロールを描画する
//        int x = left + 8;
//        int y = top + 12;
//        listVal.createCtrl(border, x, y);
//        CtrlSize ctrlSize = listVal.getCtrlSize();
//
//        // borderのサイズ指定
//        border.setSize(width, ctrlSize.getHeight() + 25); // 横はコンストラクタ、縦は、含まれるコントロールで決まる

        // オフセット移動
        left += tabbedPane.getWidth();
        top += tabbedPane.getHeight();

        //値の設定
        abstractWrite(value);

        // パネルのサイズ設定
        panel.setSize(left + margin, top + margin);

	}

	@Override
	protected void abstractDelete() {
		remove(panel,tabbedPane);
		tabbedPane = null;
	    // TODO CtrlTabPage Auto-generated method stub

	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		// TODO CtrlTabPage Auto-generated method stub
		return 0;
	}

	@Override
	protected void abstractWrite(Object value) {
		// TODO CtrlTabPage Auto-generated method stub

	}
	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	@Override
	protected void abstractSetEnable(boolean enabled) {
		// TODO CtrlTabPage Auto-generated method stub

	}
	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		// TODO CtrlTabPage Auto-generated method stub
		return false;
	}

	@Override
	protected String abstractToText() {
		// TODO CtrlTabPage Auto-generated method stub
		return null;
	}

	@Override
	protected void abstractFromText(String s) {
		// TODO CtrlTabPage Auto-generated method stub

	}

	@Override
	protected void abstractClear() {
		// TODO CtrlTabPage Auto-generated method stub

	}

}
//JTabbedPane tabbedpane = new JTabbedPane();
//
//JPanel tabPanel1 = new JPanel();
//tabPanel1.add(new JButton("button1"));
//
//JPanel tabPanel2 = new JPanel();
//tabPanel2.add(new JLabel("Name:"));
//tabPanel2.add(new JTextField("", 10));
//
//JPanel tabPanel3 = new JPanel();
//tabPanel3.add(new JButton("button2"));
//
//tabbedpane.addTab("tab1", tabPanel1);
//tabbedpane.addTab("tab2", tabPanel2);
//tabbedpane.addTab("tab3", tabPanel3);