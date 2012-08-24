package bjd;

import javax.swing.JFrame;

import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlFile;
import bjd.ctrl.CtrlInt;
import bjd.option.Crlf;
import bjd.option.OneVal;
import bjd.option.Option;

public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	private JFrame mainFrame;
	private Kernel kernel;
	public AppFunc(AppMenu appMenu , JFrame mainFrame, Kernel kernel) {
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
		System.out.println("selectMenu : " + name);
		switch (name) {
		case "Exit":
			System.exit(0);
			break;
		case "Option":
			Option option = new Option();
			OneVal o1 = new OneVal("useServer1", true, Crlf.CONTONIE, new CtrlInt("サーバを使用する", 3));
			option.add(o1);
			OneVal o2 = new OneVal("xxx2", 123, Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する"));
			option.add(o2);

			OneVal o3 = new OneVal("fileName", "c:\text.txt", Crlf.CONTONIE, new CtrlFile("ファイル名",20,kernel));
			option.add(o3);

			OptionDlg dlg = new OptionDlg(mainFrame, option);
			boolean b = dlg.showDialog();
			System.out.println("isOk = " + b);
			break;
		default:
			break;

		}
	}

}
