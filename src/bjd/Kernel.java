package bjd;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuBar;

import bjd.ctrl.ListView;
import bjd.log.ILogger;
import bjd.log.LogFile;
import bjd.log.LogKind;
import bjd.log.LogLimit;
import bjd.log.LogView;
import bjd.log.Logger;
import bjd.menu.Menu;
import bjd.net.DnsCache;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.ListOption;
import bjd.option.OneOption;
import bjd.option.OptionIni;
import bjd.server.ListServer;
import bjd.server.OneServer;
import bjd.util.IDispose;
import bjd.util.Msg;
import bjd.util.MsgKind;
import bjd.util.Util;

public final class Kernel implements IDispose {

	private RunMode runMode;
	//private LocalAddress localAddress;
	private Lang lang = Lang.JP;
	private ListOption listOption;
	private ListServer listServer;

	private LogFile logFile = null;
	private LogView logView = null;
	
	private View view;
	private Logger logger = null;
	private WindowSize windowSize;
	private Menu menu;
	private MainForm mainForm;
	private OneServer remoteServer = null;
	private TraceDlg traceDlg;
	private DnsCache dnsCache;
	//private IniDb optionIni = null;

	public View getView() {
		return view;
	}

	public TraceDlg getTraceDlg() {
		return traceDlg;
	}

	public OneServer getRemoteServer() {
		return remoteServer;
	}

//	public LocalAddress getLocalAddress() {
//		return localAddress;
//	}

	public ListOption getListOption() {
		return listOption;
	}

	public ListServer getListServer() {
		return listServer;
	}
	
	public boolean isJp() {
		return (lang == Lang.JP) ? true : false;
	}

	public RunMode getRunMode() {
		return runMode;
	}

	//テスト用コンストラクタ
	public Kernel() {
		init(null, null, null);
	}

	public Kernel(MainForm mainForm, ListView listViewLog, JMenuBar menuBar) {
		init(mainForm, listViewLog, menuBar);
	}

	public void init(MainForm mainForm, ListView listViewLog, JMenuBar menuBar) {
		this.mainForm = mainForm;
		//        MailBox = null;//実際に必要になった時に生成される(SMTPサーバ若しくはPOP3サーバの起動時)
		traceDlg = null; //トレース表示
		//        Ver = null;//バージョン管理
		this.menu = null; //メニュー管理クラス
		remoteServer = null; //クライアントへ接続中のみオブジェクトが存在する
		//        RemoteClient = null;//リモートクライアント
		//
		
		OptionIni.create(this); //インスタンスの初期化
		
		//optionIni = new IniDb(getProgDir(), "Option");
		//動作モードの初期化
		runMode = RunMode.Normal; //通常起動
		//		if (mainForm == null) {
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

		//        Ver = new Ver();//バージョン管理
		menu = new Menu(this, menuBar);
		listOption = new ListOption(this); //オプション管理
		//        ListTool = new ListTool();//ツール管理
		listServer = new ListServer(); //サーバ管理

		String path = String.format("%s\\BJD.ini", getProgDir());
		try {
			//ウインドウの外観を保存・復元(Viewより前に初期化する)
			windowSize = new WindowSize(new Conf(listOption.get("Basic")), path);
		} catch (IOException e) {
			// 指定されたWindow情報保存ファイル(BJD.ini)にIOエラーが発生している
			logger.set(LogKind.ERROR, null, 9000022, path);
		} 
		//
		//        //ログ関連インスタンスの生成
		//view = new View(this, mainForm, listViewLog, notifyIcon);
		view = new View(this, mainForm, listViewLog);
		//
		//        //リモートクライアントでは、以下のオブジェクトは接続されてから初期化される
		//        if (RunMode != RunMode.Remote) {
		//            //ローカルアドレスの一覧(ListOption初期化時にインスタンスが必要)
		//localAddress = new LocalAddress();

		//LogViewの初期化
		logView = new LogView(listViewLog);

		initList(); //各管理クラスの初期化
		menu.initialize(); //メニュー構築（内部テーブルの初期化）
		//            Menu.OnClick += Menu_OnClick;//メニュー選択時の処理
		//        }
		traceDlg = new TraceDlg(this, (mainForm != null) ? mainForm.getFrame() : null); //トレース表示
		dnsCache = new DnsCache();
		//
		Conf conf = new Conf(listOption.get("Log"));
		if (conf != null) {

			//LogFileの初期化
			String saveDirectory = (String) conf.get("saveDirectory");
			int normalLogKind = (int) conf.get("normalLogKind");
			int secureLogKind = (int) conf.get("secureLogKind");
			int saveDays = (int) conf.get("saveDays");
			boolean useLogClear = (boolean) conf.get("useLogClear");
			if (!useLogClear) {
				saveDays = 0; //ログの自動削除が無効な場合、saveDaysに0をセットする
			}
			try {
				logFile = new LogFile(saveDirectory, normalLogKind, secureLogKind, saveDays);
			} catch (IOException e) {
				Msg.show(MsgKind.ERROR, e.getMessage());
				logFile = null;
			}
			
		}
		logger = createLogger("kernel", true, null);
		
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

	}
	
	public Logger createLogger(String nameTag, boolean useDetailsLog, ILogger logger) {
		Conf conf = new Conf(listOption.get("Log"));
		if (conf == null) {
			//createLoggerを使用する際に、OptionLogが検索できないのは、設計上の問題がある
			Util.runtimeException("createLogger() conf==null");
		}
		Dat dat = (Dat) conf.get("limitString");
		boolean isDisplay = ((int) conf.get("isDisplay")) == 0 ? true : false;
		LogLimit logLimit = new LogLimit(dat, isDisplay);

		boolean useLimitString = (boolean) conf.get("useLimitString");
		return new Logger(logLimit, logFile, logView, isJp(), nameTag, useDetailsLog, useLimitString, logger);
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
		if (traceDlg != null) {
			//	            traceDlg.Dispose();
		}
		if (menu != null) {
			menu.dispose();
		}

		windowSize.dispose(); //DisposeしないとReg.Dispose(保存)されない
	}

	//オプションの値取得
	//	public Object getOptionVal(String nameTag, String name) {
	//		OneOption oneOption = listOption.get(nameTag);
	//		if (oneOption != null) {
	//			return oneOption.getValue(name);
	//		}
	//		Util.runtimeError(String.format("nameTag=%s name=%s", nameTag, name));
	//		return null;
	//	}

	public Conf createConf(String nameTag) {
		OneOption oneOption = listOption.get(nameTag);
		if (oneOption != null) {
			return new Conf(oneOption);
		}
		return null;
	}

	//public Logger createLogger(String nameTag, boolean useDetailsLog, ILogger logger) {
	//	return new Logger(this, nameTag, useDetailsLog, logger);
	//}

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
		remoteServer = listServer.get("RemoteServer");
		Conf conf = new Conf(listOption.get("Log"));
		boolean useLog = true;
		if (runMode != RunMode.Normal && runMode != RunMode.Service) {
			useLog = false;
		}
		
		try {
			logFile = new LogFile((String) conf.get("saveDirectory"), (int) conf.get("normalLogKind"), (int) conf.get("secureLogKind"), (int) conf.get("saveDays"));
		} catch (IOException e) {
			logger.set(LogKind.ERROR, null, 9000031, e.getMessage());
		}
		logView.setFont((Font) conf.get("font")); //logView.initFont();
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

	public String getProgDir() {
		return getProgName();
		//File file = new File(getProgName());
		//return file.getParent();
	}

	//public IniDb getOptionIni() {
	//	return optionIni;
	//}

	//プログラム本体のパス取得
	private String getProgName() {
		java.net.URL url = this.getClass().getResource("Kernel.class");
		if (url.getProtocol().equals("file")) { //Jar化されていない場合
			try {
				File classFile = new File(new URI(url.toString()));
				Package pack = this.getClass().getPackage();
				if (pack == null) { //無名パッケージ
					return classFile.getParentFile().toString();
				} else { //パッケージ名がある場合、階層の分だけ上に上がる
					String packName = pack.getName();
					String[] words = packName.split("\\.");
					File dir = classFile.getParentFile();
					for (int i = 0; i < words.length; i++) {
						dir = dir.getParentFile();
					}
					return dir.toString();
				}
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
				return "";
			}
		}
		//jarファイルの場合
		File f = new File(System.getProperty("java.class.path"));
		String path = "";
		try {
			path = f.getCanonicalPath(); //正規化したパスを取得する
		} catch (IOException e) {
			e.printStackTrace();
			//正規化に失敗した場合は、正規化されないパスを取得する
			path = f.getAbsolutePath();
		}
		return new File(path).getParent();
	}

	public String env(String str) {
		//TODO Kernel.env() ここの正規表現は大丈夫か
		return str.replaceAll("%ExecutablePath%", getProgDir());
	}

	//メニュー選択時の処理
	//RemoteClientの場合は、このファンクションはフックされない
	public void menuOnClick(String cmd) {

		if (cmd.indexOf("Option_") == 0) {
			OneOption oneOption = listOption.get(cmd.substring(7));
			if (oneOption != null) {
				OptionDlg dlg = new OptionDlg(mainForm.getFrame(), oneOption);
				if (dlg.showDialog()) {
					oneOption.save(OptionIni.getInstance());
					//Menu.EnqueueMenu("StartStop_Reload",true/*synchro*/);
				}
			}
		} else if (cmd.indexOf("Tool_") == 0) {
			//        	OneTool oneTool = listTool.Get(cmd.substring(5));
			//            if (oneTool == null)
			//                return;
			//            OneServer oneServer = ListServer.Get(cmd.substring(5));
			//            //BJD.EXE以外の場合、サーバオブジェクトへのポインタが必要になる
			//            if(oneTool.NameTag != "BJD" && oneServer==null)
			//                return;
			//            ToolDlg dlg = oneTool.CreateDlg(oneServer);
			//            dlg.ShowDialog();
		} else if (cmd.indexOf("StartStop_") == 0) {
			switch (cmd) {
				case "StartStop_Start":
					//Start();
					break;
				case "StartStop_Stop":
					//Stop();
					break;
				case "StartStop_Restart":
					//Stop();
					//Thread.Sleep(100);
					//Start();
					break;
				case "StartStop_Reload":
					//Stop();
					initList();
					view.setLang();
					menu.initialize();
					//Start();
					break;
				case "StartStop_Service":
					//SetupService(); //サービスの設定
					break;
				default:
					Util.runtimeException(String.format("cmd=%s", cmd));
					break;

			}
			view.setColor(); //ウインドのカラー初期化
			//menu.setEnable(); //状態に応じた有効・無効
		} else {
			switch (cmd) {
				case "File_LogClear":
					logView.clear();
					break;
				case "File_LogCopy":
					logView.setClipboard();
					break;
				case "File_Trace":
					traceDlg.open();
					break;
				case "File_Exit":
					mainForm.exit();
					break;
				case "Help_Version":

					mainForm.test();
					break;
				case "Help_Homepage":
					mainForm.test2();
					//Process.Start(Define.WebHome());
					break;
				case "Help_Document":
					//Process.Start(Define.WebDocument());
					break;
				case "Help_Support":
					//Process.Start(Define.WebSupport());
					break;
				default:
					Util.runtimeException(String.format("cmd=%s", cmd));
					break;
			}
		}

	}

	public DnsCache getDnsCache() {
		return dnsCache;
	}

}
