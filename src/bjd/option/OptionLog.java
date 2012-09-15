package bjd.option;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.OptionDlg;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlFont;
import bjd.ctrl.CtrlRadio;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;

public class OptionLog extends OneOption {

	public OptionLog(Kernel kernel, String path, String nameTag) {
		super(kernel, path, nameTag);

		ArrayList<OnePage> pageList = new ArrayList<>();
		pageList.add(page1("Basic", kernel.getJp() ? "基本設定" : "Basic"));
		pageList.add(page2("Limit", kernel.getJp() ? "表示制限" : "Limit Display"));
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));
		
		read(); //　レジストリからの読み込み

	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(new OneVal("normalFileName", 2, Crlf.NEXTLINE, new CtrlComboBox(kernel.getJp() ? "通常ログ ファイル名" : "Nomal Log", new String[]{
			kernel.getJp() ? "日ごと ( bjd.yyyy.mm.dd.log )" : "daily （bjd.yyyy.mm.dd.log）",
			kernel.getJp() ? "月ごと ( bjd.yyyy.mm.log )" : "monthly （bjd.yyyy.mm.log）",
			kernel.getJp() ? "一定 ( BlackJumboDog.Log )" : "Uniformity (BlackJumboDog.Log)"	}, 200)));
		onePage.add(new OneVal("secureFileName", 2, Crlf.NEXTLINE, new CtrlComboBox(kernel.getJp() ? "セキュリティログ ファイル名" : "Secure Log", new String[]{
			kernel.getJp() ? "日ごと ( secure.yyyy.mm.dd.log )" : "dayiy （secure.yyyy.mm.dd.log）",
			kernel.getJp() ? "月ごと ( secure.yyyy.mm.log )" : "monthly （secure.yyyy.mm.log）",
			kernel.getJp() ? "一定 ( Secure.Log )" : "Uniformity (secure.Log)"	}, 200)));
		onePage.add(new OneVal("saveDirectory", "", Crlf.NEXTLINE, new CtrlFolder(kernel.getJp() ? "ログの保存場所" : "Save place", 40, kernel)));
		onePage.add(new OneVal("useLogFile", true, Crlf.NEXTLINE, new CtrlCheckBox(kernel.getJp() ? "ログファイルを生成する" : "Generate a Log File")));
		onePage.add(new OneVal("useLogClear", false, Crlf.NEXTLINE, new CtrlCheckBox(kernel.getJp() ? "ログの削除を自動的に行う" : "Eliminate it regularly")));
		onePage.add(new OneVal("saveDays", 31, Crlf.NEXTLINE, new CtrlInt(kernel.getJp() ? "ログ保存日数(0を指定した場合、削除しない)" : "Save days(When You appointed 0, Don't eliminate)", 3)));
		onePage.add(new OneVal("linesMax", 3000, Crlf.NEXTLINE, new CtrlInt(kernel.getJp() ? "表示する最大行数" : "The number of maximum line to display", 5)));
		onePage.add(new OneVal("linesDelete", 1000, Crlf.NEXTLINE, new CtrlInt(kernel.getJp() ? "最大行数に達した際に削除する行数" : "The number of line to eliminate when I reached a maximum", 5)));
		onePage.add(new OneVal("font", null, Crlf.NEXTLINE, new CtrlFont("", kernel)));
		return onePage;
	}

	private OnePage page2(String name, String title) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(new OneVal("isDisplay", 1, Crlf.NEXTLINE, new CtrlRadio(kernel.getJp() ? "指定文字列のみを" : "A case including character string",	new String[]{
			kernel.getJp() ? "表示する" : "Display",
			kernel.getJp() ? "表示しない" : "Don't display" }, OptionDlg.width() - 40, 2)));
		ListVal list = new ListVal();
		list.add(new OneVal("Character", "", Crlf.NEXTLINE, new CtrlTextBox(kernel.getJp() ? "文字列指定" : "Character", 50)));
		onePage.add(new OneVal("LimitString", null, Crlf.NEXTLINE, new CtrlDat(kernel.getJp() ? "制限する文字列の指定" : "Limit Character", list, 250, kernel)));
		onePage.add(new OneVal("UseLimitString", false, Crlf.NEXTLINE, new CtrlCheckBox(kernel.getJp() ? "上記のルールをログファイルにも適用する" : "Apply this rule in Log")));
		return onePage;
	}

	@Override
	protected final void abstractOnChange(OneCtrl oneCtrl) {
        boolean b = (boolean) getCtrl("useLogClear").read();
        getCtrl("saveDays").setEnable(b);
	}

}
