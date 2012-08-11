package sample;

import javax.swing.JFrame;

public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	private JFrame mainFrame;
	public AppFunc(AppMenu appMenu , JFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.appMenu = appMenu;

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
			Option option = new Option();
			OneVal oneVal = new OneVal("useServer" , true , Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する"));
			option.add(oneVal);
			option.add(oneVal);
			OptionDlg dlg = new OptionDlg(mainFrame, option);
			boolean b = dlg.showDialog();
			System.out.println("isOk = " + b);
			break;
		default:
			break;

		}
	}

}
