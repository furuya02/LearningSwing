package bjd;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class Menu {
	
	private JMenuBar menuBar = null;
	private Font font;
	private ActionListener actionListener;
	public Menu(JFrame frame, ActionListener actionListener) {
		font = frame.getFont();
		this.actionListener = actionListener;
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
	}
	
	public void dispose() {
		while (menuBar.getMenuCount() > 0) {
			JMenu m =  menuBar.getMenu(0);
			m.removeAll();
			menuBar.remove(m);
		}
		menuBar.invalidate();
	}
	
	//JMenuItemの追加
	public void add(JMenu owner, String str, String name, char mnemonic, String strAccelerator) {
		JMenuItem menuItem = new JMenuItem(str);
		menuItem.setMnemonic(mnemonic);
		if (strAccelerator != null) {
			//KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK)
			//String strAccelerator = keyStroke.toString();
			menuItem.setAccelerator(KeyStroke.getKeyStroke(strAccelerator));
        }
		menuItem.setFont(font);
		menuItem.addActionListener(actionListener);
		menuItem.setName(name);

		owner.add(menuItem);
	}

	//JMenuの追加
	public JMenu add(String str, char mnemonic) {
		JMenu menu = new JMenu(str);
		menu.setMnemonic(mnemonic);
		menu.setFont(font);
		menuBar.add(menu);
		return menu;
	}

}


