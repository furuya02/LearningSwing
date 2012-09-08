package bjd.ctrl;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import bjd.OptionDlg;
import bjd.util.Msg;
import bjd.util.MsgKind;

public abstract class OneCtrl {
	private JPanel owner;
	private int controlCounter = 0; // 生成したコントロールを全部はきしたかどうかを確認するためのカウンタ
	private ArrayList<ICtrlEventListener> listenerList = new ArrayList<>();

	private String name = "";
	protected String help;
	protected JPanel panel = null;
	protected final int margin = 3;
	protected final int defaultHeight = 22;

	public OneCtrl(String help) {
		this.help = help;
	}

	//OneValのコンストラクタでnameの初期化に使用される
	//OneValのコンストラクタ内以外で利用してはならない
	public final void setName(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public final Dimension getCtrlSize() {
		if (panel == null) {
			return new Dimension(0, 0);
		}
		return new Dimension(panel.getWidth(), panel.getHeight());
	}

	public final Object getHelp() {
		return help;
	}

	public abstract CtrlType getCtrlType();

	// コントロールの生成
	protected abstract void abstractCreate(Object value);

	public final void create(JPanel owner, int x, int y, Object value) {
		this.owner = owner;

		if (panel == null) {

			panel = (JPanel) create(owner, new JPanel(), x, y);

			// Debug 色付ける
			// Random r = new Random();
			// Color bc = new Color(r.nextInt(205), r.nextInt(205),
			// r.nextInt(205));
			// panel.setBackground(bc);

			// 全部の子コントロールをベースとなるpanelのサイズは、abstractCreate()で変更される
			abstractCreate(value); // panelの上に独自コントロールを配置する
		}
	}

	// コントロールの破棄
	protected abstract void abstractDelete();

	public final void delete() {
		abstractDelete();

		if (owner != null) {
			remove(owner, panel);
		}
		panel = null;
		if (controlCounter != 0) {
			Msg.show(MsgKind.Error, String.format("生成したコントロールと破棄したコントロールの数が一致しません。 remove()に漏れが無いかを確認する必要があります。 %s", getCtrlType()));
		}
	}

	// フィールドテキストに合わせてサイズを自動調整する
	protected final void setAutoSize(JComponent component) {
		Dimension dimension = component.getPreferredSize(); // 適切サイズを取得
		dimension.width += 8; // 微調整
		component.setSize(dimension);
	}

	public static int getDlgWidth() {
		return OptionDlg.width();
	}

	public static int getDlgHeight() {
		return OptionDlg.height();
	}

	// ***********************************************************************
	// コントロールの値の読み書き
	// ***********************************************************************
	// データが無効なときnullが返る
	// TODO abstractRead() nullを返す際に、コントロールを赤色表示にする
	protected abstract Object abstractRead();

	public final Object read() {
		return abstractRead();
	}

	protected abstract void abstractWrite(Object value);

	public final void write(Object value) {
		abstractWrite(value);
	}

	// ***********************************************************************
	// コントロールへの有効・無効
	// ***********************************************************************
	protected abstract void abstractSetEnable(boolean enabled);

	public final void setEnable(boolean enabled) {
		if (panel != null) {
			abstractSetEnable(enabled);
		}
	}

	// ***********************************************************************
	// コントロールの生成・破棄（共通関数）
	// ***********************************************************************
	protected final JComponent create(JPanel owner, JComponent self, int x, int y) {
		controlCounter++;
		JComponent control = self;
		control.setLocation(x, y);
		if (self instanceof JButton) { // JButtonは、AutoSizeだと小さくなってしまう
			control.setSize(75, 22);
		} else {
			setAutoSize(control); // サイズ自動調整(この時点でテキストが適切に設定されているばあ、これでサイズの調整は終わる)
		}

		// JScrollPaneは、textAreaを配置する関係で、setLayout(null)だと入力できなくなる
		// JTabbedPaneは、setLayout(null)すると例外が発生する
		if (!(self instanceof JScrollPane) && !(self instanceof JTabbedPane)) {
			control.setLayout(null); // 子コントロールを絶対位置で表示する
		}
		if (owner != null) { // ownerがnullの場合は、非表示（デバッグモード）
			owner.add(control);
			control.setFont(owner.getFont()); // フォントの継承
		}
		return control;
	}

	protected final void remove(JComponent owner, JComponent self) {
		if (self != null) {
			controlCounter--;
			if (owner != null) { // ownerがnullの場合は、非表示（デバッグモード）
				owner.remove(self);
			}
		}
		removeListener(); // リスナーも削除する
	}

	// ***********************************************************************
	// イベントリスナー関連
	// ***********************************************************************
	public final void setListener(ICtrlEventListener listener) {
		listenerList.add(listener);
	}

	public final void removeListener() {
		while (listenerList.size() != 0) {
			listenerList.remove(0);
		}
	}

	protected final void setOnChange() {
		for (ICtrlEventListener listener : listenerList) {
			listener.onChange(this);
		}
	}

	// ***********************************************************************
	// CtrlDat関連　(Add/Del/Edit)の状態の変更、チェックリストボックスとのテキストの読み書き
	// ***********************************************************************
	// CtrlDatで入力が入っているかどうかでボタン
	protected abstract boolean abstractIsComplete();

	//CtrlDatでOverrideされている
	public boolean isComplete() {
		if (panel != null) {
			return abstractIsComplete();
		}
		return false;
	}

	// CtrlDatでリストボックスに追加するため使用される
	protected abstract String abstractToText();

	public final String toText() {
		if (panel != null) {
			return abstractToText();
		}
		return "";
	}

	// CtrlDatでリストボックスから値を戻す時、使用される
	protected abstract void abstractFromText(String s);

	public final void fromText(String s) {
		if (panel != null) {
			abstractFromText(s);
		}
	}

	// CtrlDatでDelDelボタンを押したときに使用される
	protected abstract void abstractClear();

	public final void clear() {
		if (panel != null) {
			abstractClear();
		}
	}
}
