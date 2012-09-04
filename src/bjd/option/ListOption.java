package bjd.option;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.OnePage;
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

		//TODO 「サーバを使用する」にチェックしたとき、タブの表示・非表示を切り返す
		option.add(new OneVal("useServer", true, Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する")));
		//option.add(new OneVal("server2", true, Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する")));

		ArrayList<OnePage> pageList = new ArrayList<>();
		
		ListVal page1 = new ListVal();
		page1.add(new OneVal("acl", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック")));
        page1.add(new OneVal("acl2", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック2")));
		pageList.add(new OnePage("name1", "page1", page1));
		
		ListVal page2 = new ListVal();
		page2.add(new OneVal("acl3", "123", Crlf.NEXTLINE, new CtrlTextBox("文字列入力",20)));
		pageList.add(new OnePage("name2", "page2", page2));
		
		option.add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		//CtrlDat テスト
//		ListVal list = new ListVal();
//		list.add(new OneVal("xxx2", true, Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する2")));
//		list.add(new OneVal("xxx3", true, Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する3")));
//		list.add(new OneVal("xxx4", "123", Crlf.NEXTLINE, new CtrlTextBox("サーバを使用する3", 10)));
//		option.add(new OneVal("group", null, Crlf.NEXTLINE, new CtrlDat("グループ", list, 300, kernel)));

		
		//option.add(new OneVal("bindAddr", null, Crlf.NEXTLINE, new CtrlBindAddr(kernel.getJp() ? "待ち受けるネットワーク" : "BinfAddress", listV4, listV6)));

		// option.add(new OneVal("xxx4", 123, Crlf.NEXTLINE, new
		// CtrlInt("サーバを使用する", 3)));
		// option.add(new OneVal("fileName", "c:\\text.txt", Crlf.NEXTLINE, new
		// CtrlFile("ファイル名",30,kernel)));
		// option.add(new OneVal("fileName2", "c:\\work", Crlf.NEXTLINE, new
		// CtrlFolder("フォルダ名",30,kernel)));
		// option.add(new OneVal("text", "TEST", Crlf.NEXTLINE, new
		// CtrlTextBox("テキスト入力",30)));
		// option.add(new OneVal("hidden", "HIDDEN", Crlf.NEXTLINE, new
		// CtrlHidden("パスワード",30)));
		// option.add(new OneVal("radio", 0, Crlf.NEXTLINE, new
		// CtrlRadio("ラジオボタン",new
		// String[]{"１１１","２２２","３３３","４４４","555"},500,3)));
		// option.add(new OneVal("combo", 0, Crlf.NEXTLINE, new
		// CtrlComboBox("コンボボックス",new
		// String[]{"１１１","２２２","３３３","４４４","555"},200)));
		// option.add(new OneVal("memo","123\n123", Crlf.NEXTLINE, new
		// CtrlMemo("メモ",200,60)));
		// option.add(new OneVal("addresds",new Ip("127.0.0.1"), Crlf.NEXTLINE,
		// new CtrlAddress("アドレス")));

		// option.add(new OneVal("xxx", true, Crlf.NEXTLINE, new
		// CtrlCheckBox("サーバを使用する2")));

		// listVal.add(new OneVal("xxx31", true, Crlf.NEXTLINE, new
		// CtrlCheckBox("サーバを使用する3")));
		// listVal.add(new OneVal("fileName5", "c:\\text.txt", Crlf.NEXTLINE,
		// new CtrlFile("ファイル名",30,kernel)));
		// listVal.add(new OneVal("text2", "TEST", Crlf.NEXTLINE, new
		// CtrlTextBox("テキスト入力",30)));
		// listVal.add(new OneVal("hidden2", "HIDDEN", Crlf.NEXTLINE, new
		// CtrlHidden("パスワード",30)));

		// ListVal list = new ListVal();
		// list.add(new OneVal("xxx2", true, Crlf.CONTONIE, new
		// CtrlCheckBox("サーバを使用する2")));
		// list.add(new OneVal("xxx3", true, Crlf.NEXTLINE, new
		// CtrlCheckBox("サーバを使用する3")));
		// listVal.add(new OneVal("group",list, Crlf.NEXTLINE, new CtrlGroup("グループ",180,150)));

		// ListVal list2 = new ListVal();
		// list2.add(new OneVal("xxx3", true, Crlf.CONTONIE, new
		// CtrlCheckBox("サーバを使用する2")));
		// list2.add(new OneVal("xxxc", true, Crlf.NEXTLINE, new
		// CtrlCheckBox("サーバを使用する3")));
		// listVal.add(new OneVal("group2",list2, Crlf.NEXTLINE, new
		// CtrlGroup("グループ")));

		// option.add(new OneVal("group3",listVal, Crlf.NEXTLINE, new
		// CtrlGroup("グループ")));

	}

	public OneOption get(String name) {
		return option;
	}

}
