package bjd;

import javax.swing.JFrame;

import menu.AppMenu;
import menu.SelectMenuListener;
//TODO 最終的に破棄

public final class AppFunc implements SelectMenuListener {
	private MainForm mainForm;
	private AppMenu appMenu;
	private JFrame mainFrame;
	private Kernel kernel;
	
	public AppFunc(MainForm mainForm, AppMenu appMenu, JFrame mainFrame, Kernel kernel) {
		this.mainForm = mainForm;
		this.mainFrame = mainFrame;
		this.appMenu = appMenu;
		this.kernel = kernel;

		appMenu.addListener(this); //自身をイベント送信先リストに追加
	}

	public void dispose() {
		appMenu.removeListener(this); //イベント送信先リストから削除
	}

	@Override
	public void selectMenu(String name) {
		switch (name) {
			case "Exit":
				mainForm.exit();
				break;
			case "Basic Option":
				OptionDlg dlg = new OptionDlg(mainFrame, kernel.getListOption().get("Basic"));
				if (dlg.showDialog()) {

				}

				break;
			case "Log":
				dlg = new OptionDlg(mainFrame, kernel.getListOption().get("Log"));
				if (dlg.showDialog()) {

				}

				break;
			case "Test":
				kernel.Test();

				break;
			default:
				break;

		}
	}

}
