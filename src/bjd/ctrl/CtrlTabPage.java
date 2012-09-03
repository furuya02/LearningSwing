package bjd.ctrl;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class CtrlTabPage extends OneCtrl {
	private ArrayList<OnePage> pageList;
	private JTabbedPane tabbedPane = null;
	private ArrayList<JPanel> pagePanelList = null;

	public CtrlTabPage(String help, ArrayList<OnePage> pageList) {
		super(help);
		this.pageList = pageList;
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
		tabbedPane.setSize(getBaseWidth() - 22, getBaseHeight());

		panel.add(tabbedPane);

		//グループに含まれるコントロールを描画する(listValはCtrlPageなので座標やサイズはもう関係ない)

		pagePanelList = new ArrayList<>();
		for (OnePage onePage : pageList) {
			JPanel p = new JPanel();
			
			onePage.getListVal().createCtrl(p, 0, 0); //ページの中を作成
			
			tabbedPane.addTab(onePage.getTitle(), p);
			pagePanelList.add(p);
		}

		// オフセット移動
		left += tabbedPane.getWidth();
		top += tabbedPane.getHeight();

		//値の設定
		//abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, top + margin);

	}

	@Override
	protected void abstractDelete() {

		for (OnePage onePage : pageList) {
			onePage.getListVal().deleteCtrl(); //これが無いと、グループの中のコントロールが２回目以降表示されなくなる
		}

		while (pagePanelList.size() != 0) {
			tabbedPane.remove(pagePanelList.get(0)); //タブから削除
			pagePanelList.remove(0); // リストから削除
		}
		pagePanelList = null;
		
		remove(panel, tabbedPane);
		tabbedPane = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		return 0;
	}

	@Override
	protected void abstractWrite(Object value) {
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	@Override
	protected void abstractSetEnable(boolean enabled) {
	    tabbedPane.setEnabled(enabled);
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		return true; //未入力状態はない
	}

	@Override
	protected String abstractToText() {
	    throw new UnsupportedOperationException("*CtrlTabPage.java abstractToText()は未実装");
	}

	@Override
	protected void abstractFromText(String s) {
        throw new UnsupportedOperationException("*CtrlTabPage.java abstractFromText()は未実装");
	}

	@Override
	protected void abstractClear() {
        throw new UnsupportedOperationException("*CtrlTabPage.java abstractClear()は未実装");
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