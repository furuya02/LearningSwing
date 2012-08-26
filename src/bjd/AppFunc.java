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

			OptionDlg dlg = new OptionDlg(mainFrame, kernel.getListOption().get("XXX"));
			if(dlg.showDialog()){
				//System.out.println("isOk");
			}				
			
			break;
		default:
			break;

		}
	}

}
