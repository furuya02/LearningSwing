package bjd.ctrl;

import bjd.option.ListVal;

public class CtrlTabPage extends OneCtrl {
	private ListVal listVal;

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
		// TODO CtrlTabPage Auto-generated method stub

	}

	@Override
	protected void abstractDelete() {
		// TODO CtrlTabPage Auto-generated method stub

	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		// TODO CtrlTabPage Auto-generated method stub
		return null;
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