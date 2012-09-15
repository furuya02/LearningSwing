package bjd.ctrl;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import bjd.util.IDispose;

public final class ListView extends JScrollPane implements IDispose {

	private JTable table;
	private DefaultTableModel model = new DefaultTableModel();

	public ListView() {
		//自動的にスクロールバーを表示
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table = new JTable(model);
		table.setDefaultEditor(Object.class, null); //セルの編集禁止
		table.setRowHeight(20); //行の高さ
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //横幅の自動調整をOFF
		table.setShowGrid(false); //グリッド線を描画しない
		table.getTableHeader().setReorderingAllowed(false); //カラムの入れ替えを禁止

		getViewport().add(table); // ScrollPaneにtebleをセットする
	}

	@Override
	public void dispose() {
		remove(table);
		model = null;
		table = null;
	}
	
	public void addColumn(String str) {
		model.addColumn(str);
	}

	public void itemAdd(String[] str) {
		model.addRow(str);
	}
	
	// 全行削除
	public void itemClear() {
		model.setRowCount(0);
	}

	//*******************************************************
	//列幅の設定・取得
	//*******************************************************
	public void setColWidth(int index, int width) {
		TableColumn col = table.getColumnModel().getColumn(index);
		col.setPreferredWidth(width);
	}

	public int getColWidth(int index) {
		TableColumn col = table.getColumnModel().getColumn(index);
		return col.getWidth();
	}


}
