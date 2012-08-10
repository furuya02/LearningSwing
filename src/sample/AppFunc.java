package sample;

import javax.swing.JFrame;

public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	private JFrame frame;
	public AppFunc(AppMenu anAppMenu,JFrame aFrame) {
		frame = aFrame;
		appMenu = anAppMenu;
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
			OptionDlg dlg = new OptionDlg(frame);
			boolean b = dlg.showDialog();
			System.out.println("isOk = " + b);	
			break;
		default:
			break;
	
		}
	}

}
