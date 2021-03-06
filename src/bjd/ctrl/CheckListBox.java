package bjd.ctrl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionListener;

public class CheckListBox extends JScrollPane implements MouseListener {
	private JList<JCheckBox> list;
	private DefaultListModel<JCheckBox> defaultListModel;

	public CheckListBox() {

		defaultListModel = new DefaultListModel<JCheckBox>();
		list = new JList<JCheckBox>(defaultListModel);

		super.getViewport().setView(list);

		MyCellRenderer renderer = new MyCellRenderer();
		list.setCellRenderer(renderer);
		list.addMouseListener(this);
	}

	//１行追加
	public int add(String str) {
		JCheckBox checkBox = new JCheckBox(str);
		defaultListModel.addElement(checkBox);
		return getItemCount() - 1;
	}

	//１行削除
	public void remove(int index) {
		if (isRange(index)) {
			defaultListModel.remove(index);
		}
	}
	//全行削除
	public void removeAll() {
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

	//イベントリスナの設定
	public void addListSelectionListener(ListSelectionListener listener) {
		list.addListSelectionListener(listener);
	}

	// 範囲内かどうか
	private boolean isRange(int index) {
		if (index < 0 || defaultListModel.getSize() <= index) {
			return false;
		}
		return true;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Point p = e.getPoint();
		if (p.x > 20) {
			return; //チェックボタンの上以外のイベントは無視する
		}
		int index = list.locationToIndex(p);
		JCheckBox checkBox = (JCheckBox) defaultListModel.getElementAt(index);
		checkBox.setSelected(checkBox.isSelected() ? false : true);

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
}

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
