package sample;

<<<<<<< HEAD
import javax.swing.JFrame;

public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	private JFrame frame;
	public AppFunc(AppMenu anAppMenu,JFrame aFrame) {
		frame = aFrame;
		appMenu = anAppMenu;
=======
public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	public AppFunc(AppMenu appMenu) {
		this.appMenu = appMenu;
>>>>>>> f460185d3e1ed1a87b0f348c9fe7e1bae0f19a73
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
<<<<<<< HEAD
			OptionDlg dlg = new OptionDlg(frame);
			boolean b = dlg.showDialog();
			System.out.println("isOk = " + b);	
=======
			//System.exit(0);
>>>>>>> f460185d3e1ed1a87b0f348c9fe7e1bae0f19a73
			break;
		default:
			break;
	
		}
	}

}
