package bjd.ctrl;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
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

	public void addColumn(String str) {
		model.addColumn(str);
	}

	public void addRow(String[] str) {
		model.addRow(str);
	}

	@Override
	public void dispose() {
		remove(table);
		model = null;
		table = null;
	}
}
