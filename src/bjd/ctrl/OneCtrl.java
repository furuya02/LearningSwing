package bjd.ctrl;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bjd.util.Msg;
import bjd.util.MsgKind;

public abstract class OneCtrl {
	private JPanel owner;
	private int controlCounter = 0; // 生成したコントロールを全部はきしたかどうかを確認するためのカウンタ
	private ArrayList<ICtrlEventListener> listenerList = new ArrayList<>();

	protected String help;
	protected JPanel panel = null;
	protected final int margin = 3;
	protected final int defaultHeight = 22;

	public OneCtrl(String help) {
		this.help = help;
	}

	public CtrlSize getCtrlSize() {
		return new CtrlSize(panel.getWidth(), panel.getHeight());
	}

	public Object getHelp() {
		return help;
	}

	public abstract CtrlType getCtrlType();

	// コントロールの生成
	protected abstract void abstractCreate(Object value);

	public void create(JPanel owner, int x, int y, Object value) {
		this.owner = owner;
		// this.oneVal = oneVal;
		if (panel == null) {
			panel = (JPanel) create(owner, new JPanel(), x, y);

			// Debug 色付ける
			Random r = new Random();
			Color bc = new Color(r.nextInt(205), r.nextInt(205), r.nextInt(205));
			panel.setBackground(bc);

			// 全部の子コントロールをベースとなるpanelのサイズは、abstractCreate()で変更される
			abstractCreate(value); // panelの上に独自コントロールを配置する
		}
	}

	// コントロールの破棄
	protected abstract void abstractDelete();

	public void delete() {
		abstractDelete();
		remove(owner, panel);
		panel = null;
		// CtrlTabPageの時は、PanelはTabControlを指しているので破棄できない
		// if (GetType() != CtrlType.TabPage) {
		// Panel.Dispose();
		// }
		// Panel = null;
		if (controlCounter != 0) {
			Msg.show(MsgKind.Error, String.format("生成したコントロールと破棄したコントロールの数が一致しません。 remove()に漏れが無いかを確認する必要があります。 %s", getCtrlType()));
		}
	}

	// フィールドテキストに合わせてサイズを自動調整する
	protected void setAutoSize(JComponent component) {
		Dimension dimension = component.getPreferredSize(); // 適切サイズを取得
		dimension.width += 8; // 微調整
		component.setSize(dimension);
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected abstract void abstractSetEnable(boolean enabled);

	public void setEnable(boolean enabled) {
		if (panel != null) {
			abstractSetEnable(enabled);
		}
	}

	//***********************************************************************
	// コントロールへの値の設定・取得
	//***********************************************************************
	// データが無効なときnullが返る
	protected abstract Object abstractRead();

	public Object read() {
		return abstractRead();
	}

	protected abstract void abstractWrite(Object value);

	public void write(Object value) {
		abstractWrite(value);
	}

	//***********************************************************************
	// コントロールの生成・破棄（共通関数）
	//***********************************************************************
	protected JComponent create(JPanel owner, JComponent self, int x, int y) {
		controlCounter++;
		JComponent control = self;
		control.setLocation(x, y);
		if (self instanceof JButton) { //JButtonは、AutoSizeだと小さくなってしまう
			control.setSize(75, 22);
		} else {
			setAutoSize(control); // サイズ自動調整(この時点でテキストが適切に設定されているばあ、これでサイズの調整は終わる)
		}

		if (!(self instanceof JScrollPane)) { //JScrollPaneは、textAreaを配置する関係で、setLayout(null)だと入力できなくなる
			control.setLayout(null); // 子コントロールを絶対位置で表示する
		}
		owner.add(control);
		control.setFont(owner.getFont()); // フォントの継承
		return control;
	}

	protected void remove(JPanel owner, JComponent self) {
		if (self != null) {
			controlCounter--;
			owner.remove(self);
		}
		removeListener(); //リスナーも削除する
	}

	//***********************************************************************
	// イベントリスナー関連
	//***********************************************************************
	public void setListener(ICtrlEventListener listener) {
		listenerList.add(listener);
	}

	public void removeListener() {
		while (listenerList.size() != 0) {
			listenerList.remove(0);
		}
	}

	protected void setOnChange() {
		for (ICtrlEventListener listener : listenerList) {
			listener.onChange(this);
		}
	}

	//***********************************************************************
	// CtrlDat関連　(Add/Del/Edit)の状態の変更、チェックリストボックスとのテキストの読み書き
	//***********************************************************************
	//CtrlDatで入力が入っているかどうかでボタン
	protected abstract boolean abstractIsComplete();

	public boolean isComplete() {
		if (panel != null) {
			return abstractIsComplete();
		}
		return false;
	}

	//CtrlDatでリストボックスに追加するため使用される
	protected abstract String abstractToText();

	public String toText() {
		if (panel != null) {
			return abstractToText();
		}
		return "";
	}

	//CtrlDatでリストボックスから値を戻す時、使用される
	protected abstract void abstractFromText(String s);

	public void fromText(String s) {
		if (panel != null) {
			abstractFromText(s);
		}
	}

	//CtrlDatでDelDelボタンを押したときに使用される
	protected abstract void abstractClear();

	public void clear() {
		if (panel != null) {
			abstractClear();
		}
	}
}
