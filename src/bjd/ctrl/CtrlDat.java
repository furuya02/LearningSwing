package bjd.ctrl;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bjd.Kernel;
import bjd.option.Dat;
import bjd.option.ListVal;
import bjd.option.OneDat;
import bjd.option.OneVal;
import bjd.util.Msg;
import bjd.util.MsgKind;

public class CtrlDat extends OneCtrl implements ActionListener, ListSelectionListener, ICtrlEventListener {

	private JPanel border = null;
	private JButton[] buttonList = null;
	private CheckListBox checkListBox = null;
	private ListVal listVal;

	private int width;
	private int height;
	private Kernel kernel;

	private static final int ADD = 0;
	private static final int EDIT = 1;
	private static final int DEL = 2;
	private static final int IMPORT = 3;
	private static final int EXPORT = 4;
	private static final int CLEAR = 5;
	private String[] tagList = new String[] { "Add", "Edit", "Del", "Import", "Export", "Clear" };
	private String[] strList = new String[] { "追加", "変更", "削除", "インポート", "エクスポート", "クリア" };

	public CtrlType[] getCtrlTypeList() {
		CtrlType[] ctrlTypeList = new CtrlType[listVal.size()];
		int i = 0;
		for (OneVal o : listVal) {
			ctrlTypeList[i++] = o.getOneCtrl().getCtrlType();
		}
		return ctrlTypeList;
	}

	public CtrlDat(String help, ListVal listVal, int width, int height, Kernel kernel) {
		super(help);
		this.listVal = listVal;
		this.width = width;
		this.height = height;
		this.kernel = kernel;
	}

	public ListVal getListVal() {
		return listVal;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.DAT;
	}

	@Override
	protected void abstractCreate(Object value) {
		int left = margin;
		int top = margin;

		// ボーダライン（groupPanel）の生成
		border = (JPanel) create(panel, new JPanel(new GridLayout()), left, top);
		border.setBorder(BorderFactory.createTitledBorder(help));
		border.setSize(width, height); // サイズは、コンストラクタで指定されている

		//Datに含まれるコントロールを配置

		//ボーダーの中でのオフセット移動
		left += 8;
		top += 12;
		listVal.createCtrl(border, left, top);
		listVal.setListener(this); //コントロールの変化を取得

		//オフセット移動
		CtrlSize ctrlSize = listVal.getCtrlSize();
		top += ctrlSize.getHeight();

		//ボタン配置
		buttonList = new JButton[tagList.length];

		for (int i = 0; i < tagList.length; i++) {
			String btnText = kernel.getJp() ? strList[i] : tagList[i];
			buttonList[i] = (JButton) create(border, new JButton(btnText), left + (i * 86), top);
			buttonList[i].setActionCommand(tagList[i]);
			buttonList[i].addActionListener(this);
			buttonList[i].setSize(85, buttonList[i].getHeight());
		}

		//オフセット移動
		top += buttonList[0].getHeight() + margin;

		//チェックリストボックス配置
		checkListBox = (CheckListBox) create(border, new CheckListBox(), left, top);
		checkListBox.setSize(width - 20, height - top - 15);
		checkListBox.addListSelectionListener(this);

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(border.getWidth() + margin * 2, border.getHeight() + margin * 2);

		buttonsInitialise(); //ボタン状態の初期化
	}

	@Override
	protected void abstractDelete() {
		listVal.deleteCtrl(); //これが無いと、グループの中のコントロールが２回目以降表示されなくなる

		if (buttonList != null) {
			for (int i = 0; i < buttonList.length; i++) {
				remove(border, buttonList[i]);
				buttonList[i] = null;
			}
		}
		remove(panel, border);
		remove(panel, checkListBox);
		border = null;
	}

	@Override
	protected Object abstractRead() {
		Dat dat = new Dat(getCtrlTypeList());
		//チェックリストボックスの内容からDatオブジェクトを生成する
		for (int i = 0; i < checkListBox.getItemCount(); i++) {
			boolean enable = checkListBox.getItemChecked(i);
			dat.add(enable, checkListBox.getItemText(i));
		}
		return dat;
	}

	@Override
	protected void abstractWrite(Object value) {
		if (value == null) {
			return;
		}
		Dat dat = (Dat) value;
		for (OneDat d : dat) {
			StringBuilder sb = new StringBuilder();
			ArrayList<String> strList = d.getStrList();
			for (String s : strList) {
				if (sb.length() != 0) {
					sb.append("\t");
				}
				sb.append(s);
			}
			int i = checkListBox.add(sb.toString());
			checkListBox.setItemChecked(i, d.isEnable());
		}
		//データがある場合は、１行目を選択する
		if (checkListBox.getItemCount() > 0) {
			checkListBox.setSelectedIndex(0);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedIndex = checkListBox.getSelectedIndex(); // 選択行
		String cmd = e.getActionCommand();
		if (cmd.equals(tagList[ADD])) {
			//コントロールの内容をテキストに変更したもの
			String s = controlToText();
			if (s.equals("")) {
				return;
			}
			//同一のデータがあるかどうかを確認する
			if (checkListBox.indexOf(s) != -1) {
				Msg.show(MsgKind.Error, kernel.getJp() ? "既に同一内容のデータが存在します。" : "There is already the same data");
				return;
			}
			//チェックリストボックスへの追加
			int index = checkListBox.add(s);
			checkListBox.setItemChecked(index, true); //最初にチェック（有効）状態にする
			checkListBox.setSelectedIndex(index); //選択状態にする
		} else if (cmd.equals(tagList[EDIT])) {
			//コントロールの内容をテキストに変更したもの
			String str = controlToText();
			if (str.equals("")) {
				return;
			}
			if (checkListBox.getItemText(selectedIndex).equals(str)) {
				Msg.show(MsgKind.Error, kernel.getJp() ? "変更内容はありません" : "There is not a change");
				return;
			}
			//同一のデータがあるかどうかを確認する
			if (checkListBox.indexOf(str) != -1) {
				Msg.show(MsgKind.Error, kernel.getJp() ? "既に同一内容のデータが存在します" : "There is already the same data");
				return;
			}
			checkListBox.setItemText(selectedIndex, str);

		} else if (cmd.equals(tagList[DEL])) {
			for (OneVal v : listVal) { //コントロールの内容をクリア
				v.getOneCtrl().clear();
			}
			if (selectedIndex >= 0) {
				checkListBox.remove(selectedIndex);
			}
		} else if (cmd.equals(tagList[IMPORT])) {
			//TODO CtrlDat IMPORT未実装
			//var d = new OpenFileDialog();
			//if (DialogResult.OK == d.ShowDialog()) {
			//    Import(d.FileName);
			//}
		} else if (cmd.equals(tagList[EXPORT])) {
			//TODO CtrlDat EXPORTT未実装
			//var dlg = new SaveFileDialog();
			//if (DialogResult.OK == dlg.ShowDialog()) {
			//    Export(dlg.FileName);
			//}
		} else if (cmd.equals(tagList[CLEAR])) {
			int n = Msg.show(MsgKind.Question, kernel.getJp() ? "すべてのデータを削除してよろしいですか" : "May I eliminate all data?");
			if (n == 0) {
				checkListBox.removeAll();
			}
			for (OneVal v : listVal) { //コントロールの内容をクリア
				v.getOneCtrl().clear();
			}
		}

		System.out.println(e.getActionCommand());
	}

	//リストボックスの選択
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) { //複数回の突入制御
			return;
		}
		int index = checkListBox.getSelectedIndex();

		buttonsInitialise(); //ボタン状態の初期化

		//チェックリストの内容をコントロールに転送する
		if (index >= 0) {
			textToControl(checkListBox.getItemText(index));
		}
	}

	//チェックボックス用のテキストを入力コントロールに戻す
	private void textToControl(String str) {
		String[] tmp = str.split("\t");
		if (listVal.size() != tmp.length) {
			Msg.show(MsgKind.Error, (kernel.getJp()) ? "項目数が一致しません" : "The number of column does not agree");
			return;
		}
		int i = 0;
		for (OneVal v : listVal) {
			v.getOneCtrl().fromText(tmp[i++]);
		}
	}

	//入力コントロールの内容をチェックボックス用のテキストに変換する
	private String controlToText() {

		StringBuilder sb = new StringBuilder();
		for (OneVal v : listVal) {
			if (sb.length() != 0) {
				sb.append("\t");
			}
			sb.append(v.getOneCtrl().toText());
		}
		return sb.toString();

	}

	//コントロールの入力内容に変化があった場合
	@Override
	public void onChange(OneCtrl oneCtrl) {
		buttonsInitialise(); //ボタン状態の初期化
		System.out.println(String.format("onChenge(%s)", oneCtrl.getCtrlType()));
	}

	//ボタン状態の初期化
	private void buttonsInitialise() {
		//コントロールの入力が完了しているか
		boolean isComplete = isComplete();
		//チェックリストボックスのデータ件数
		int count = checkListBox.getItemCount();
		//チェックリストボックスの選択行
		int index = checkListBox.getSelectedIndex();

		buttonList[ADD].setEnabled(isComplete);
		buttonList[EXPORT].setEnabled(count > 0);
		buttonList[CLEAR].setEnabled(count > 0);
		buttonList[DEL].setEnabled(index >= 0);
		buttonList[EDIT].setEnabled(index >= 0 && isComplete);
	}

	//***********************************************************************
	//コントロールの入力が完了しているか
	//***********************************************************************
	@Override
	public boolean isComplete() {
		return listVal.isComplete();
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (border != null) {
			border.setEnabled(enabled);
		}
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	// 必要なし
	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		throw new UnsupportedOperationException("CtrlDat.java abstractIsComlete()は使用禁止");
	}

	@Override
	protected String abstractToText() {
		throw new UnsupportedOperationException("CtrlDat.java abstractToText()は使用禁止");
	}

	@Override
	protected void abstractFromText(String s) {
		throw new UnsupportedOperationException("CtrlDat.java abstractFromText()は使用禁止");
	}

	@Override
	protected void abstractClear() {
		throw new UnsupportedOperationException("CtrlDat.java abstractClear()は使用禁止");
	}
}
