package bjd.option;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlAddress;
import bjd.ctrl.CtrlBindAddr;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlFont;
import bjd.ctrl.CtrlHidden;
import bjd.ctrl.CtrlMemo;
import bjd.ctrl.CtrlPage;
import bjd.ctrl.CtrlTabPage;
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

        
		ListVal pageList = new ListVal();
		    ListVal page = new ListVal();
		    page.add(new OneVal("acl", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック")));
		pageList.add(new OneVal("page1",null,Crlf.NEXTLINE,new CtrlPage("ページ１",page)));
        
        
        option.add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

        
        
		//option.add(new OneVal("bindAddr", null, Crlf.NEXTLINE, new CtrlBindAddr(kernel.getJp() ? "待ち受けるネットワーク" : "BinfAddress", listV4, listV6)));

		// option.add(new OneVal("xxx5", true, Crlf.NEXTLINE, new
		// CtrlCheckBox("サーバを使用する")));
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
