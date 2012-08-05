package sample;

public class AppFunc  implements SelectMenuListener {
	private AppMenu appMenu;
	public AppFunc(AppMenu appMenu) {
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
			//System.exit(0);
			break;
		default:
			break;
	
		}
	}

}
