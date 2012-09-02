package bjd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

interface SelectMenuListener extends EventListener {
	void selectMenu(String name);
}

public class AppMenu implements ActionListener {
	private boolean jp = true;
	private Menu menu;
	//イベント
	private Vector<SelectMenuListener> listenerList = new Vector<SelectMenuListener>();

	public AppMenu(JFrame frame) {
		menu = new Menu(frame, this);
		init(jp);
	}

	public void dispose() {
		menu.dispose();
	}

	//イベント追加
	void addListener(SelectMenuListener l) {
		if (!listenerList.contains(l)) {
			listenerList.add(l);
		}
	}

	//イベント削除
	void removeListener(SelectMenuListener l) {
		listenerList.remove(l);
	}

	public void init(boolean setJp) {
		this.jp = setJp;

		//「ファイル」
		JMenu m = add("ファイル", "File", 'F');
		//　終了
		add(m, "設定", "Setting", 'S', null);
		//    セパレータ
		m.addSeparator();
		//　終了
		add(m, "終了", "Exit", 'X', "alt pressed F4");

		//「ツール」
		JMenu t = add("ツール", "Tool", 'T');
		// オプション
		add(t, "オプション", "Option", 'O', null);

	}

	//JMenuの追加
	JMenu add(String jpStr, String enStr, char mnemonic) {
		String title = enStr;
		if (jp) {
			title = String.format("%s(%c)", jpStr, mnemonic);
		}
		return menu.add(title, mnemonic);
	}

	// JMenuItemの追加
	void add(JMenu owner, String jpStr, String enStr, char mnemonic, String strAccelerator) {
		String title = enStr;
		if (jp) {
			title = String.format("%s(%c)", jpStr, mnemonic);
		}
		//name=enStr
		menu.add(owner, title, enStr, mnemonic, strAccelerator);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = ((JMenuItem) e.getSource()).getName();
		Vector<SelectMenuListener> list = (Vector<SelectMenuListener>) listenerList.clone();
		for (SelectMenuListener n : list) {
			n.selectMenu(name);
		}
	}
}
