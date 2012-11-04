package bjd.plugins.web;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.RunMode;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlFile;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.net.ProtocolKind;
import bjd.option.Crlf;
import bjd.option.OneOption;
import bjd.option.OneVal;
import bjd.option.OptionIni;

public final class Option extends OneOption {

	@Override
	public String getJpMenu() {
		return "Webサーバ";
	}

	@Override
	public String getEnMenu() {
		return "Web Server";
	}

	@Override
	public char getMnemonic() {
		return 'W';
	}

	public Option(Kernel kernel, String path) {
		super(kernel.isJp(), path, "Web");
		
		RunMode runMode = kernel.getRunMode();
		boolean editBrowse = kernel.getEditBrowse();

		ArrayList<OnePage> pageList = new ArrayList<>();
		
		add(new OneVal("useServer", true, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "Webサーバを使用する" : "Use Web Server")));
		
		pageList.add(page1("Basic", isJp() ? "基本設定" : "Basic", runMode, editBrowse));
		pageList.add(page2("Cgi", isJp() ? "CGI" : "CGI", runMode, editBrowse));
		pageList.add(pageAcl());
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		read(OptionIni.getInstance()); //　レジストリからの読み込み
	}

	private OnePage page1(String name, String title, RunMode runMode, boolean editBrowse) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(createServerOption(ProtocolKind.Tcp, 80, 30, 50)); //サーバ基本設定
		onePage.add(new OneVal("documentRoot", "", Crlf.NEXTLINE, new CtrlFolder(isJp(), isJp() ? "ドキュメントのルートディレクトリ" : "DocumentRoot", 300, runMode, editBrowse)));
		onePage.add(new OneVal("welcomeFileName", "index.html", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "Welcomeファイルの指定(カンマで区切って複数指定可能です)" : "Welcome File", 200)));
		onePage.add(new OneVal("useHidden", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "隠し属性ファイルへリクエストを許可する" : "Cover it and prohibit a request to a file of attribute")));
		onePage.add(new OneVal("useDot", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "URLに..が含まれるリクエストを許可する" : "Prohibit the request that .. is include in")));
		onePage.add(new OneVal("useExpansion", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "BJDを経由したリクエストの特別拡張を有効にする" : "Use special expansion")));
		onePage.add(new OneVal("useDirectoryEnum", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "ディレクトリ一覧を表示する" : "Display Index")));
		onePage.add(new OneVal("serverHeader", "BlackJumboDog Version $v", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "Server:ヘッダの指定" : "Server Header", 500)));
		onePage.add(new OneVal("useEtag", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "ETagを追加する" : "Use ETag")));
        onePage.add(new OneVal("serverAdmin", "", Crlf.CONTINUE, new CtrlTextBox(isJp() ? "管理者メールアドレス" : "server admin", 150)));

        return onePage;
	}

	private OnePage page2(String name, String title, RunMode runMode, boolean editBrowse) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(new OneVal("useCgi", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "CGIを使用する" : "Use CGI")));
	/*	{
			var l = new ListVal();
			l.Add(new OneVal("Extension", "", Crlf.CONTINUE, new CtrlTextBox(isJp() ? "拡張子" : "Extension", 50)));
			l.Add(new OneVal("Program", "", Crlf.NEXTLINE, new CtrlFile(isJp(),isJp() ? "プログラム" : "Program", 330, runMode,editBrowse)));
			onePage.add(new OneVal("cgiCmd", null, Crlf.NEXTLINE, new CtrlDat("", l, 600, 142, kernel.Jp)));
		}
		onePage.add(new OneVal("cgiTimeout", 10, Crlf.NEXTLINE, new CtrlInt(isJp() ? "CGIタイムアウト(秒)" : "CGI Timeout(sec)", 5)));
		{
			var l = new ListVal();
			l.Add(new OneVal("CgiPath", "", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "CGIパス" : "CGI Path", 300)));
			l.Add(new OneVal("Directory", "", Crlf.NEXTLINE, new CtrlFolder(isJp(),isJp() ? "参照ディレクトリ" : "Directory", 300, runMode, editBrowse)));
			onePage.add(new OneVal("cgiPath", null, Crlf.NEXTLINE, new CtrlDat(isJp() ? "CGIパスを指定した場合、指定したパスのみCGIが許可されます" : "When I appointed a CGI path It is admitted CGI only the path that I appointed", l, 600, 155, kernel.Jp)));
		}
		onePage.add(new OneVal("CGI", list, Crlf.NEXTLINE, new CtrlTabPage("CGI")));
*/
        return onePage;
	}

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
		//boolean b = (boolean) getCtrl("useServer").read();
		//getCtrl("Basic").setEnable(b);
	}
}
/*
 * 
            Add(new OneVal("useServer", false,Crlf.NEXTLINE,new CtrlCheckBox(isJp() ? "Webサーバを使用する" : "Use Web Server")));


            {//PAGE CGI ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                list.Add(new OneVal("useCgi", false,Crlf.NEXTLINE,new CtrlCheckBox(isJp() ? "CGIを使用する" : "Use CGI")));
                {//DAT
                    var l = new ListVal();
                    l.Add(new OneVal("Extension","",Crlf.CONTINUE,new CtrlTextBox(isJp() ? "拡張子" : "Extension", 50)));
                    l.Add(new OneVal("Program","",Crlf.NEXTLINE,new CtrlBrowse(isJp() ? "プログラム" : "Program",BrowseType.File,330, kernel)));
                    list.Add(new OneVal("cgiCmd",null,Crlf.NEXTLINE,new CtrlDat("",l,600,142,kernel.Jp)));
                }//DAT
                list.Add(new OneVal("cgiTimeout", 10,Crlf.NEXTLINE,new CtrlInt(isJp() ? "CGIタイムアウト(秒)" : "CGI Timeout(sec)", 5)));
                {//DAT
                    var l = new ListVal();
                    l.Add(new OneVal("CgiPath","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "CGIパス" : "CGI Path", 300)));
                    l.Add(new OneVal("Directory","",Crlf.NEXTLINE,new CtrlBrowse(isJp() ? "参照ディレクトリ" : "Directory",BrowseType.Folder,300, kernel)));
                    list.Add(new OneVal("cgiPath", null,Crlf.NEXTLINE,new CtrlDat(isJp() ? "CGIパスを指定した場合、指定したパスのみCGIが許可されます" : "When I appointed a CGI path It is admitted CGI only the path that I appointed",l,600,155,kernel.Jp)));
                }//DAT
                Add(new OneVal("CGI", list, Crlf.NEXTLINE, new CtrlTabPage("CGI")));
            }//PAGE CGI ////////////////////////////////////////////////////////////////////////////

            {//PAGE SSI ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                list.Add(new OneVal("useSsi", false,Crlf.NEXTLINE,new CtrlCheckBox(isJp() ? "SSIを使用する" : "Use SSI")));
                list.Add(new OneVal("ssiExt", "html,htm",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "SSIとして認識する拡張子(カンマで区切って複数指定できます)" : "Extension to recognize as SSI ( Separator , )", 200)));
                list.Add(new OneVal("useExec", false,Crlf.NEXTLINE,new CtrlCheckBox(isJp() ? "exec cmd (cgi) を有効にする" : "Use exec,cmd(cgi)")));
                Add(new OneVal("SSI", list, Crlf.NEXTLINE, new CtrlTabPage("SSI")));
            }//PAGE SSI ////////////////////////////////////////////////////////////////////////////

            {//PAGE WebDAV ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                list.Add(new OneVal("useWebDav", false,Crlf.NEXTLINE,new CtrlCheckBox(isJp() ? "WebDAVを使用する" : "Use WebDAV")));
                {//DAT
                    var l = new ListVal();
                    l.Add(new OneVal("WebDAV Path","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "WebDAVパス" : "WebDAV Path", 300)));
                    l.Add(new OneVal("Writing permission",false,Crlf.NEXTLINE,new CtrlCheckBox(isJp() ? "書き込みを許可する" : "Writing permission")));
                    l.Add(new OneVal("Directory","",Crlf.NEXTLINE,new CtrlBrowse(isJp() ? "参照ディレクトリ" : "Directory",BrowseType.Folder,300, kernel)));
                    list.Add(new OneVal("webDavPath", null,Crlf.NEXTLINE,new CtrlDat(isJp() ? "指定したパスでのみWevDAVが有効になります" : "WebDAV becomes effective only in the path that I appointed",l,600,280,kernel.Jp)));
                }//DAT
                Add(new OneVal("WebDAV", list, Crlf.NEXTLINE, new CtrlTabPage("WebDAV")));
            }//PAGE WebDAV ////////////////////////////////////////////////////////////////////////////

            {//PAGE 別名指定 ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                {//DAT
                    var l = new ListVal();
                    l.Add(new OneVal("Alias","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "別名" : "Alias", 300)));
                    l.Add(new OneVal("Directory","",Crlf.NEXTLINE,new CtrlBrowse(isJp() ? "参照ディレクトリ" : "Directory",BrowseType.Folder,300,kernel)));
                    list.Add(new OneVal("aliase", null,Crlf.NEXTLINE,new CtrlDat(isJp() ? "指定した名前（別名）で指定したディレクトリを直接アクセスします" : "Access the directory which I appointed by the name(alias) that I appointed directly",l,600,250,kernel.Jp)));
                }//DAT
                Add(new OneVal("Alias", list, Crlf.NEXTLINE, new CtrlTabPage(isJp() ? "別名指定" : "Alias")));
            }//PAGE 別名指定 ////////////////////////////////////////////////////////////////////////////

            {//PAGE MIME ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                {//DAT
                    var l = new ListVal();
                    l.Add(new OneVal("Extension","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "拡張子" : "Extension", 100)));
                    l.Add(new OneVal("mime","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "MIMEタイプ" : "MIME Type", 250)));
                    list.Add(new OneVal("mime", null,Crlf.NEXTLINE,new CtrlDat(isJp() ? "データ形式を指定するための、「MIMEタイプ」のリストを設定します" : "Set a MIME Type list in order to appoint data form",l,600,350,kernel.Jp)));
                }//DAT
                Add(new OneVal("MimeType", list, Crlf.NEXTLINE, new CtrlTabPage(isJp() ? "MIMEタイプ" : "MIME Type")));
            }//PAGE MIME ////////////////////////////////////////////////////////////////////////////

            {//PAGE 認証 ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                {//DAT
                    var l = new ListVal();
                    l.Add(new OneVal("Directory","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "URL (Directory)" : "Directory", 250)));
                    l.Add(new OneVal("AuthName","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "名前 (AuthName)" : "AuthName", 150)));
                    l.Add(new OneVal("Require","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "ユーザ/グループ (Require)" : "Require", 400)));
                    list.Add(new OneVal("authList", null,Crlf.NEXTLINE,new CtrlDat(isJp() ? "ユーザ/グループは「;」で区切って複数設定できます" : "divide it in [;], and plural [Require] can appoint it",l,600,350,kernel.Jp)));
                }//DAT
                Add(new OneVal("Certification", list, Crlf.NEXTLINE, new CtrlTabPage(isJp() ? "認証リスト" : "Certification")));
            }//PAGE 認証 ////////////////////////////////////////////////////////////////////////////


            {//PAGE 認証(ユーザ) ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                {//DAT
                    var l = new ListVal();
                    l.Add(new OneVal("user","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "ユーザ (user)" : "user", 150)));
                    l.Add(new OneVal("pass","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "パスワード (password)" : "password", 150,true)));
                    list.Add(new OneVal("userList", null,Crlf.NEXTLINE,new CtrlDat(isJp() ? "ユーザ定義" : "User List",l,600,350,kernel.Jp)));
                }//DAT
                Add(new OneVal("CertUserList", list, Crlf.NEXTLINE, new CtrlTabPage(isJp() ? "認証（ユーザリスト）" : "Certification(User List)")));
            }//PAGE 認証 ////////////////////////////////////////////////////////////////////////////

            {//PAGE 認証(グループ) ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                {//DAT
                    var l = new ListVal();
                    l.Add(new OneVal("group","",Crlf.NEXTLINE,new CtrlTextBox(isJp() ? "グループ (group)" : "group", 150)));
                    l.Add(new OneVal("user","",Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "ユーザ(user)" : "user", 450)));
                    list.Add(new OneVal("groupList", null,Crlf.NEXTLINE,new CtrlDat(isJp() ? "ユーザは「;」で区切って複数設定できます" : "divide it in [;], and plural [user] can appoint it",l,600,350,kernel.Jp)));
                }//DAT
                Add(new OneVal("CertGroupList", list, Crlf.NEXTLINE, new CtrlTabPage(isJp() ? "認証（グループリスト）" : "Certification(Group List)")));
            }//PAGE 認証 ////////////////////////////////////////////////////////////////////////////

            {//PAGE indexの定型 ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();
                list.Add(new OneVal("encode", 0,Crlf.NEXTLINE,new CtrlComboBox(isJp() ? "エンコード" : "Encode",new List<string>(){"UTF-8", "SHIFT-JIS", "EUC"})));
                list.Add(new OneVal("indexDocument", "",Crlf.NEXTLINE,new CtrlMemo(isJp() ? "インデックスドキュメント" : "Index Document", 600, 145)));
                list.Add(new OneVal("errorDocument", "", Crlf.NEXTLINE, new CtrlMemo(isJp() ? "エラードキュメント" : "Error Document", 600, 145)));
                Add(new OneVal("ModelSentence", list, Crlf.NEXTLINE, new CtrlTabPage(isJp() ? "雛型" : "Model Sentence")));
            }//PAGE indexの定型////////////////////////////////////////////////////////////////////////////

            {//PAGE 自動拒否 ////////////////////////////////////////////////////////////////////////////
                var list = new ListVal();

                list.Add(new OneVal("useAutoAcl", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "自動拒否を使用する" : "use automatic deny")));
                list.Add(new OneVal("autoAclLabel", isJp() ? "「ACL」設定で「指定するアドレスからのアクセスのみを」-「禁止する」にチェックされている必要があります" : "It is necessary for it to be checked if I [Deny] by [ACL] setting", Crlf.NEXTLINE, new CtrlLabel(isJp() ? "「ACL」設定で「指定するアドレスからのアクセスのみを」-「禁止する」にチェックされている必要があります" : "It is necessary for it to be checked if I [Deny] by [ACL] setting")));
                var l = new ListVal();
                l.Add(new OneVal("AutoAclApacheKiller", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "Apache Killer の検出" : "Search of Apache Killer")));
                list.Add(new OneVal("autoAclGroup", l, Crlf.NEXTLINE, new CtrlGroup(isJp() ? "拒否リストに追加するイベント" : "Target Event")));

                Add(new OneVal("AutoACL", list, Crlf.NEXTLINE, new CtrlTabPage(isJp() ? "自動拒否" : "AutoDeny")));
            }//PAGE 自動拒否 ////////////////////////////////////////////////////////////////////////////


            Init();//初期化処理
        }
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

        }
    } * 
 * */
