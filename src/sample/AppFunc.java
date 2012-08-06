package sample;

public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	public AppFunc(AppMenu appMenu) {
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
			//System.exit(0);
			break;
		default:
			break;
	
		}
	}

}
