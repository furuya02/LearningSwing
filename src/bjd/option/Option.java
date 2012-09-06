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
import bjd.ctrl.OneCtrl;
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
		pageList.add(page2("page2", "ページ２"));
		pageList.add(page3("page3", "ページ３"));
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		//ArrayList<OneVal> list = listVal.getList(null);
		//for (OneVal oneVal : list) {
		//	//TODO DEBUG
		//	System.out.println(String.format("list-> %s", oneVal.getName()));
		//}

	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);

		onePage.add(new OneVal("useDat", true, Crlf.NEXTLINE, new CtrlCheckBox("DatCtrlの有効無効")));

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

		onePage.add(new OneVal("useTextBox1", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック")));
		onePage.add(new OneVal("n2", true, Crlf.NEXTLINE, new CtrlCheckBox("チェック2")));
		onePage.add(new OneVal("textBox1", "123", Crlf.NEXTLINE, new CtrlTextBox("文字列入力", 20)));

		return onePage;
	}

	private OnePage page3(String name, String title) {
		OnePage onePage = new OnePage(name, title);

		onePage.add(new OneVal("n4", "123", Crlf.NEXTLINE, new CtrlHidden("パスワード", 20)));

		return onePage;
	}
	
	@Override
	public void onChange(OneCtrl oneCtrl) {
		//TODO Debug Print
		System.out.println(String.format("Option.java OnCange() %s[%s]",oneCtrl.getName(),oneCtrl.getCtrlType()));
		

        boolean b = (boolean)getCtrl("useServer").read();
        getCtrl("tab").setEnable(b);
		
        b = (boolean)getCtrl("useDat").read();
        getCtrl("dat").setEnable(b);
		
        b = (boolean)getCtrl("useTextBox1").read();
        getCtrl("textBox1").setEnable(b);
		
	}

	/*
//コントロールの変化
        override public void OnChange() {


            var b = (bool)GetCtrl("useServer").GetValue();
            GetCtrl("Basic").SetEnable(b);


            GetCtrl("protocol").SetEnable(false);
            GetCtrl("port").SetEnable(false);

            b = (bool)GetCtrl("useCgi").GetValue();
            GetCtrl("cgiCmd").SetEnable(b);
            GetCtrl("cgiTimeout").SetEnable(b);
            GetCtrl("cgiPath").SetEnable(b);

            b = (bool)GetCtrl("useSsi").GetValue();
            GetCtrl("ssiExt").SetEnable(b);
            GetCtrl("useExec").SetEnable(b);

            b = (bool)GetCtrl("useWebDav").GetValue();
            GetCtrl("webDavPath").SetEnable(b);

            //同一ポートで待ち受ける仮想サーバの同時接続数は、最初の定義をそのまま使用する
            var port = (int)GetValue("port");
            foreach (var o in Kernel.ListOption){
                if (o.NameTag.IndexOf("Web-") != 0)
                    continue;
                if (port != (int) o.GetValue("port"))
                    continue;
                if (o == this)
                    continue;
                //このオプション以外の最初の定義を発見した場合
                var multiple = (int)o.GetValue("multiple");
                SetVal("multiple", multiple);
                GetCtrl("multiple").SetEnable(false);
                break;
            }

            b = (bool)GetCtrl("useAutoAcl").GetValue();
            GetCtrl("autoAclLabel").SetEnable(b);
            GetCtrl("autoAclGroup").SetEnable(b);

        }	 * */
}
