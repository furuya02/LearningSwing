package bjd.option;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlHidden;
import bjd.ctrl.CtrlTextBox;
import bjd.net.Ip;

public class ListOption {

	Option option;
	Kernel kernel;

	public ListOption(Kernel kernel) {

		ArrayList<Ip> listV4 = new ArrayList<>();
		listV4.add(new Ip("INADDR_ANY"));
		listV4.add(new Ip("192.168.0.1"));
		listV4.add(new Ip("10.0.0.1"));
		ArrayList<Ip> listV6 = new ArrayList<>();
		listV6.add(new Ip("IN6ADDR_ANY_INIT"));
		listV6.add(new Ip("::1"));
		listV6.add(new Ip("80eb::1"));

		option = new Option();

		//		//TODO 「サーバを使用する」にチェックしたとき、タブの表示・非表示を切り返す
		//		//PageTabテスト
		//		option.add(new OneVal("useServer", true, Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する")));
		//		//PageList
		//		ArrayList<OnePage> pageList = new ArrayList<>();
		//		//pgae1
		//		ListVal page1 = new ListVal();
		//		page1.add(new OneVal("acl", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック")));
		//        page1.add(new OneVal("acl2", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック2")));
		//		pageList.add(new OnePage("name1", "page1", page1));
		//		//Page2
		//		ListVal page2 = new ListVal();
		//		page2.add(new OneVal("acl3", "123", Crlf.NEXTLINE, new CtrlTextBox("文字列入力",20)));
		//		pageList.add(new OnePage("name2", "page2", page2));
		//		option.add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		//CtrlDat テスト
		ListVal list = new ListVal();
		list.add(new OneVal("combo", 0, Crlf.NEXTLINE, new CtrlComboBox("コンボボックス", new String[] { "DOWN", "PU", "FULL" }, 200)));
		list.add(new OneVal("fileName2", "c:\\work", Crlf.NEXTLINE, new CtrlFolder("フォルダ", 30, kernel)));
		list.add(new OneVal("text", "user1", Crlf.NEXTLINE, new CtrlTextBox("テキスト入力", 30)));
		list.add(new OneVal("hidden", "123", Crlf.NEXTLINE, new CtrlHidden("パスワード", 30)));
		option.add(new OneVal("data", null, Crlf.NEXTLINE, new CtrlDat("データ", list, 300, kernel)));

	}

	public OneOption get(String name) {
		return option;
	}

}
