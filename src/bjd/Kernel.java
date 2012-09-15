package bjd;

import java.io.File;

import bjd.ctrl.ListView;
import bjd.log.ConfLog;
import bjd.log.ILogger;
import bjd.log.LogFile;
import bjd.log.LogView;
import bjd.log.Logger;
import bjd.net.LocalAddress;
import bjd.option.ListOption;
import bjd.option.OneOption;
import bjd.server.ListServer;
import bjd.server.OneServer;
import bjd.util.IDispose;
import bjd.util.Util;

public final class Kernel implements IDispose {

    private RunMode runMode = RunMode.Normal;
	private boolean jp = true;
	private LocalAddress localAddress;
	private ListOption listOption;
    private ListServer listServer;
	private LogView logView;
	private LogFile logFile = null;
	private View view;
    
    public LogFile getLogFile() {
		return logFile;
	}

	public LocalAddress getLocalAddress() {
		return localAddress;
	}
	
	public ListOption getListOption() {
		return listOption;
	}

	public ListServer getListServer() {
        return listServer;
    }

	public boolean getJp() {
		return jp;
	}

	public RunMode getRunMode() {
		return runMode;
	}

	public LogView getLogView() {
        return logView;
    }

    //public Kernel(MainForm mainForm, ListView listViewLog, MenuStrip menuStrip,NotifyIcon notifyIcon) {
	public Kernel(ListView listViewLog) {
		
//        MailBox = null;//実際に必要になった時に生成される(SMTPサーバ若しくはPOP3サーバの起動時)
//        TraceDlg = null;//トレース表示
//        Ver = null;//バージョン管理
//        Menu = null;//メニュー管理クラス
//        Wait = null;
//        RemoteServer = null;//クライアントへ接続中のみオブジェクトが存在する
//        RemoteClient = null;//リモートクライアント
//
//        //動作モードの初期化
//        RunMode = RunMode.Normal;//通常起動
//        if (mainForm == null) {
//            RunMode = RunMode.Service;//サービス起動
//        } else {
//            if (Environment.GetCommandLineArgs().Length > 1) {
//                RunMode = RunMode.Remote;//リモートクライアント
//            } else {
//                //サービス登録の状態を取得する
//                var setupService = new SetupService(this);
//                if (setupService.IsRegist)
//                    RunMode = RunMode.NormalRegist;//サービス登録完了状態
//            }
//        }
//
//        WindowSize = new WindowSize(this);//ウインドウの外観を保存・復元(Viewより前に初期化する)
//        Ver = new Ver();//バージョン管理
//        Menu = new Menu(this, menuStrip);
        listOption = new ListOption(this); //オプション管理
//        ListTool = new ListTool();//ツール管理
        listServer = new ListServer(); //サーバ管理
//
//        //ログ関連インスタンスの生成
//        LogView = new LogView(this, listViewLog);//ログビュー
        //view = new View(this, mainForm, listViewLog, notifyIcon);
		view = new View(this, listViewLog);
		//
//        //リモートクライアントでは、以下のオブジェクトは接続されてから初期化される
//        if (RunMode != RunMode.Remote) {
//            //ローカルアドレスの一覧(ListOption初期化時にインスタンスが必要)
            localAddress = new LocalAddress();
            initList(); //各管理クラスの初期化
//            Menu.Initialize();//メニュー構築（内部テーブルの初期化）
//            Menu.OnClick += Menu_OnClick;//メニュー選択時の処理
//        
//        }
//        Wait = new Wait();
//        TraceDlg = new TraceDlg(this);//トレース表示
//        DnsCache = new DnsCache();
//
//        _logger = CreateLogger("kernel", true, null);
//
//        switch (RunMode){
//            case RunMode.Remote:
//                RemoteClient = new RemoteClient(this);
//                RemoteClient.Start();
//                break;
//            case RunMode.Normal:
//                Menu.EnqueueMenu("StartStop_Start",true);//synchro
//                break;
//        }
//
//        //Ver5.2.4 過去のバージョンのファイルを削除する
//        var list = new List<String> { "example.cer", "example.key", "libeay32.dll", "ssleay32.dll" };
//        foreach (var l in list) {
//            var f = string.Format("{0}\\{1}", ProgDir(), l);
//            if (File.Exists(f)) {
//                File.Delete(f);
//            }
//        }
		
	}
	
	    @Override
	    public void dispose() {
//	        if (RunMode != RunMode.Service && RunMode != RunMode.Remote) {
//	            //**********************************************
//	            // 一旦ファイルを削除して現在有効なものだけを書き戻す
//	            //**********************************************
//	            var iniDb = new IniDb(ProgDir(),"Option");
//	            iniDb.DeleteIni();
	              listOption.save();
//	            //Ver5.5.1 設定ファイルの保存に成功した時は、bakファイルを削除する
//	            iniDb.DeleteBak();
	//
	            //**********************************************
	            // 破棄
	            //**********************************************
	            listServer.dispose(); //各サーバは停止される
	              listOption.dispose();
//	            ListTool.Dispose();
//	            MailBox = null;
//	        }
//	        if (RemoteClient != null)
//	            RemoteClient.Dispose();
	//
	              view.dispose();
//	        if (TraceDlg != null)
//	            TraceDlg.Dispose();
	//
//	        WindowSize.Dispose();//DisposeしないとReg.Dispose(保存)されない
	    }

	//オプションの値取得
	public Object getOptionVal(String nameTag, String name) {
		OneOption oneOption = listOption.get(nameTag);
		if (oneOption != null) {
			return oneOption.getValue(name);
		}
		Util.designProblem(String.format("nameTag=%s name=%s", nameTag, name));
		return null;
	}


	public Logger createLogger(String nameTag, boolean useDetailsLog, ILogger logger) {
		return new Logger(this, nameTag, useDetailsLog, logger);
	}

    //各管理リストの初期化
    void initList() {

        //************************************************************
        // 破棄
        //************************************************************
        listOption.dispose();
//        ListTool.Dispose();
        listServer.dispose();
//        MailBox = null;

        //************************************************************
        // 初期化
        //************************************************************
        listOption.initialize(); // dllからのリスト初期化
        
//        //オプションの読み直し
		if (logFile != null) {
			logFile.dispose();
		}
		
		Logger logger = createLogger("Log", true, null);
		OneServer remoteServer = listServer.get("RemoteServer");
		ConfLog conf = new ConfLog(listOption.get("Log"), this);
		boolean useLog = true;
		if (runMode != RunMode.Normal && runMode != RunMode.Service) {
			useLog = false;
		}
		logFile = new LogFile(logger, conf, getLogView(), useLog, remoteServer);
//        LogView.InitFont();
//
//        foreach (var o in ListOption) {
//            //SmtpServer若しくは、Pop3Serverが使用される場合のみメールボックスを初期化する                
//            if (o.NameTag == "SmtpServer" || o.NameTag == "Pop3Server") {
//                if (o.UseServer) {
//                    MailBox = new MailBox(this, ListOption.Get("MailBox"));
//                    break;
//                }
//            }
//        }
//        ListTool.Initialize(this);
//        ListServer.Initialize(this, ListOption);

    }

	private String progDir() {
		//TODO kernel.progDir() とりあえずカレントディレクトリを返しておく
		//return Path.GetDirectoryName(Define.ExecutablePath());
        return new File(".").getAbsoluteFile().getParent();
	}

	public String env(String str) {
		//TODO Kernel.env() ここの正規表現は大丈夫か
		return str.replaceAll("%ExecutablePath%", progDir());
	}


}
