package bjd.option;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlHidden;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.OnePage;

public final class Option extends OneOption {
	private Kernel kernel;

	public Option(Kernel kernel) {

		this.kernel = kernel;

		//TODO 「サーバを使用する」にチェックしたとき、タブの表示・非表示を切り返す
		//TODO ■DEBUG TabPageの中にDatが追加できない　TabPageをページごと整理して記述できるようにしてからで行する

		//		ArrayList<Ip> listV4 = new ArrayList<>();
		//		listV4.add(new Ip("INADDR_ANY"));
		//		listV4.add(new Ip("192.168.0.1"));
		//		listV4.add(new Ip("10.0.0.1"));
		//		ArrayList<Ip> listV6 = new ArrayList<>();
		//		listV6.add(new Ip("IN6ADDR_ANY_INIT"));
		//		listV6.add(new Ip("::1"));
		//		listV6.add(new Ip("80eb::1"));

		add(new OneVal("useServer", true, Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する")));

		ArrayList<OnePage> pageList = new ArrayList<>();
		pageList.add(page1("page1", "ページ１"));
		//pageList.add(page2("page2", "ページ２"));
		//pageList.add(page3("page3", "ページ３"));
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		//ArrayList<OneVal> list = listVal.getList(null);
		//for (OneVal oneVal : list) {
		//	//TODO DEBUG
		//	System.out.println(String.format("list-> %s", oneVal.getName()));
		//}

	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);

		ListVal list = new ListVal();
		list.add(new OneVal("combo", 0, Crlf.NEXTLINE, new CtrlComboBox("コンボボックス", new String[] { "DOWN", "PU", "FULL" }, 200)));
		list.add(new OneVal("fileName2", "c:\\work", Crlf.NEXTLINE, new CtrlFolder("フォルダ", 30, kernel)));
		list.add(new OneVal("text", "user1", Crlf.NEXTLINE, new CtrlTextBox("テキスト入力", 30)));
		list.add(new OneVal("hidden", "123", Crlf.NEXTLINE, new CtrlHidden("パスワード", 30)));
		list.add(new OneVal("hidden2", "123", Crlf.NEXTLINE, new CtrlHidden("パスワード", 30)));
		onePage.add(new OneVal("dat", null, Crlf.NEXTLINE, new CtrlDat("データ", list, 300, kernel)));

		return onePage;
	}

	private OnePage page2(String name, String title) {
		OnePage onePage = new OnePage(name, title);

		onePage.add(new OneVal("n1", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック")));
		onePage.add(new OneVal("n2", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック2")));
		onePage.add(new OneVal("n3", "123", Crlf.NEXTLINE, new CtrlTextBox("文字列入力", 20)));

		return onePage;
	}

	private OnePage page3(String name, String title) {
		OnePage onePage = new OnePage(name, title);

		onePage.add(new OneVal("n4", "123", Crlf.NEXTLINE, new CtrlHidden("パスワード", 20)));

		return onePage;
	}

}
