package sample;

import javax.swing.JFrame;

public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	private JFrame frame;
	public AppFunc(AppMenu anAppMenu,JFrame aFrame) {
		frame = aFrame;
		appMenu = anAppMenu;
		appMenu.addListener(this); //自身をイベント送信先リストに追加
	}
	public void dispose() {
		appMenu.removeListener(this); //イベント送信先リストから削除
	}

	@Override
	public void selectMenu(String name) {
		System.out.println("selectMenu : " + name);
		switch (name) {
		case "Exit":
			System.exit(0);
			break;
		case "Option":
			OptionDlg dlg = new OptionDlg(frame);
			boolean b = dlg.showDialog();
			System.out.println("isOk = " + b);	
			break;
		default:
			break;
	
		}
	}

}
