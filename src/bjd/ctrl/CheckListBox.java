package bjd.ctrl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * CheckListBox
 * クラス内でJListを保持する
 * JListのマウスボタン及び選択等のイベントをクラス内で処理して、外部にはActionEventを発生させる
 */

@SuppressWarnings("serial")
public final class CheckListBox extends JScrollPane implements MouseListener, ListSelectionListener {
	private JList<JCheckBox> list;
	private DefaultListModel<JCheckBox> defaultListModel;

	private ArrayList<ActionListener> listenerList = new ArrayList<>();

	public CheckListBox() {

		defaultListModel = new DefaultListModel<JCheckBox>();
		list = new JList<JCheckBox>(defaultListModel);

		super.getViewport().setView(list);

		MyCellRenderer renderer = new MyCellRenderer();
		list.setCellRenderer(renderer); // オリジナル描画
		list.addMouseListener(this);
		list.addListSelectionListener(this);
	}
	//************************************************************
	//外部から使用するイベント処理
	//************************************************************
	//ActionListenerの追加
	public void addActionListener(ActionListener listener) {
		listenerList.add(listener);
	}
	//イベント発生
	private void setEvent(String cmd) {
		for (ActionListener listener : listenerList) {
			listener.actionPerformed(new ActionEvent(this, 0, cmd));
		}
	}
	//************************************************************
	//コントロールの操作メソッド
	//************************************************************
	//１行追加
	public int add(String str) {
		JCheckBox checkBox = new JCheckBox(str);
		defaultListModel.addElement(checkBox);
		setEvent("add"); //イベント発生
		return getItemCount() - 1;
	}

	//１行削除
	public void remove(int index) {
		setEvent("remove"); //イベント発生
		if (isRange(index)) {
			defaultListModel.remove(index);
		}
	}

	//全行削除
	public void removeAll() {
		setEvent("removeAll"); //イベント発生
		defaultListModel.removeAllElements();
	}

	//行数取得
	public int getItemCount() {
		return defaultListModel.getSize();
	}

	//　テキスト取得
	public String getItemText(int index) {
		if (isRange(index)) {
			return defaultListModel.get(index).getText();
		}
		return defaultListModel.get(index).getText();
	}

	//　テキストの設定
	public void setItemText(int index, String str) {
		if (isRange(index)) {
			setEvent("setItemText"); //イベント発生
			defaultListModel.get(index).setText(str);
			list.repaint(); //再描画
		}
	}

	//　一致する行の取得
	public int indexOf(String s) {
		for (int i = 0; i < getItemCount(); i++) {
			if (getItemText(i).equals(s)) {
				return i;
			}
		}
		return -1;
	}

	//選択行の取得
	public int getSelectedIndex() {
		return list.getSelectedIndex();
	}

	//選択行の設定
	public void setSelectedIndex(int index) {
		if (isRange(index)) {
			list.setSelectedIndex(index);
		}
	}

	// チェック状態の取得
	public boolean getItemChecked(int index) {
		if (isRange(index)) {
			return defaultListModel.get(index).isSelected();
		}
		return false;
	}

	// チェックの設定
	public void setItemChecked(int index, boolean isSelected) {
		if (isRange(index)) {
			defaultListModel.get(index).setSelected(isSelected);
		}
	}

	// 範囲内かどうかチェック
	private boolean isRange(int index) {
		if (index < 0 || defaultListModel.getSize() <= index) {
			return false;
		}
		return true;
	}

	//************************************************************
	//MouseListenerのオーバーライド
	//************************************************************
	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		if (p.x > 20) {
			return; //チェックボタンの上以外のイベントは無視する
		}
		int index = list.locationToIndex(p);
		JCheckBox checkBox = (JCheckBox) defaultListModel.getElementAt(index);
		checkBox.setSelected(checkBox.isSelected() ? false : true);

		setEvent("changeSelected");

		//表示が遅れる場合があるので、ここで強制的に再描画する
		list.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	//************************************************************
	//ListSelectionListenerのオーバーライド
	//************************************************************
	//リストボックスの選択
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) { //複数回の突入制御
			return;
		}
		setEvent("cahngeSelectIndex");
	}

}

@SuppressWarnings("serial")
class MyCellRenderer extends JCheckBox implements ListCellRenderer {

	public MyCellRenderer() {

	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		//setText(value.toString());

		JCheckBox checkBox = (JCheckBox) value;
		String s = checkBox.getText();
		setText(s.replaceAll("\t", "____"));
		setSelected(checkBox.isSelected()); //項目の値を読み出して改めて表示する

		if (isSelected) {
			setForeground(Color.white);
			setBackground(new Color(50, 130, 255)); //選択行の色
		} else {
			setForeground(Color.black);
			setBackground(Color.white);
		}
		return this;
	}

}
