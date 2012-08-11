package sample;

import javax.swing.JFrame;

public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	private JFrame mainFrame;
	public AppFunc(AppMenu appMenu , JFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.appMenu = appMenu;

		appMenu.addListener(this); //���g���C�x���g���M�惊�X�g�ɒǉ�
	}
	public void dispose() {
		appMenu.removeListener(this); //�C�x���g���M�惊�X�g����폜
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
			OneVal oneVal = new OneVal("useServer" , true , Crlf.NEXTLINE, new CtrlCheckBox("�T�[�o���g�p����"));
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
