package bjd.log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import bjd.Kernel;
import bjd.RunMode;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.option.OneOption;
import bjd.util.IDispose;

public class LogFile implements IDispose{
	private Kernel kernel;
	private Logger logger;

	private LogLimit logLimit;
	private boolean useLog;//「ログファイルを生成
	private String saveDirectory = "";//ログ保存ディレクトリ
    
    //以下はuseLogがtrueの時のみ初期化されている
	private boolean useLogClear;//自動ログ削除
	private int saveDays;//ログ保存日数
	private int nomalFileName;//通常ファイルの種類
	private int secureFileName;//セキュアファイルの種類
    
	private OneLogFile nomalLog;//通常ログ
	private OneLogFile secureLog;//セキュアログ

	private Calendar dt = null; //インスタンス生成時に初期化し、日付が変化したかどうかの確認に使用する
	private Calendar lastDelete = null; //new DateTime(0);

	//private System.Timers.Timer _timer;

    public LogFile(Kernel kernel) {
        this.kernel = kernel;
        this.logger = kernel.createLogger("Log", true, null);

    	dt = Calendar.getInstance();
    	lastDelete = Calendar.getInstance();
    	lastDelete.setTime(new Date(0));
        
        OneOption option = null;

        //サービス起動ではログ保存されない
        if (kernel.getRunMode() != RunMode.Normal && kernel.getRunMode() != RunMode.Service) {
            useLog = false;
        } else {
            //オプション初期化
            option = kernel.getListOption().get("Log");
            logLimit = new LogLimit(option);
            useLog = (boolean)option.getValue("useLog");
        }


        if (useLog) {
            saveDirectory = (String)option.getValue("saveDirectory");
            saveDirectory = kernel.env(saveDirectory);
            if (!Directory.Exists(saveDirectory)) {
                logger.Set(LogKind.Error, null, 9000031, String.format("saveDirectory={0}", saveDirectory));
                useLog = false;
            }
        }
        if (useLog) {
            useLogClear = (boolean)option.getValue("useLogClear");
            saveDays = (int)option.getValue("saveDays");
            nomalFileName = (int)option.getValue("nomalFileName");
            secureFileName = (int)option.getValue("secureFileName");

            logOpen();

            timer = new System.Timers.Timer{Interval = 1000*60*5};
            //5分に１回のインターバルタイマ
            timer.Elapsed += TimerElapsed;
            timer.Enabled = true;
        }

    }
    public void dispose() {
        if (useLog) {
            logClose();
            logDelete();//過去ログの自動削除 
        }
    }

    //インターバルタイマ
    void timerElapsed(Object sender, System.Timers.ElapsedEventArgs e) {
        if (kernel.getRunMode() != RunMode.Normal && kernel.getRunMode() != RunMode.Service)
            return;
        
        Calendar now = Calendar.getInstance();

        //日付が変わっている場合は、ファイルを初期化する
        if (lastDelete.Ticks != 0 && lastDelete.get(Calendar.DATE)) == now.get(Calendar.DATE))
            return;

        lock (this) {
            LogClose();//クローズ
            LogDelete();//過去ログの自動削除
            LogOpen();//オープン
            lastDelete = now;
        }
    }


    //ログファイルへの追加
    public void append(OneLog oneLog) {

        //表示制限の確認
        boolean enable = true;
        if(logLimit!=null)//通常起動の場合以外は初期化されていない
            enable = logLimit.Enable(oneLog.ToString());
        
        if (enable) {
            //ログビューへの追加
            kernel.LogView.Append(oneLog);

            //リモートサーバへの追加
            if (kernel.getRunMode() != RunMode.Remote) {
                var sv = kernel.ListServer.Get("RemoteServer");
                if (sv != null){
                    sv.Append(oneLog);
                }
            }
        }

        if (secureLog != null && oneLog.LogKind == (LogKind.Secure).toString()) {//セキュリティログ
            lock (this) {
                secureLog.Set(oneLog.ToString());
            }
        }
        if (nomalLog != null) {//通常ログ
            //ルール適用除外　もしくは　表示対象になっている場合
            if (!logLimit.UseLimitString || enable) {
                lock (this) {
                    nomalLog.Set(oneLog.ToString());
                }
            }
        }
    }
    void logOpen(){
        //ログファイルオープン
        dt = DateTime.Now;

        String fileName = "";
        switch (nomalFileName) {
            case 0://bjd.yyyy.mm.dd.log
                fileName = String.Format("{0}\\bjd.{1:D4}.{2:D2}.{3:D2}.log", saveDirectory, dt.Year, dt.Month, dt.Day);
                break;
            case 1://bjd.yyyy.mm.log
                fileName = String.Format("{0}\\bjd.{1:D4}.{2:D2}.log", saveDirectory, dt.Year, dt.Month);
                break;
            case 2://BlackJumboDog.Log
                fileName = String.Format("{0}\\BlackJumboDog.Log", saveDirectory);
                break;
        }
        nomalLog = new OneLogFile(fileName);
        switch (secureFileName) {
            case 0://secure.yyyy.mm.dd.log
                fileName = String.format("{0}\\secure.{1:D4}.{2:D2}.{3:D2}.log", saveDirectory, dt.Year, dt.Month, dt.Day);
                break;
            case 1://secure.yyyy.mm.log
                fileName = String.format("{0}\\secure.{1:D4}.{2:D2}.log", saveDirectory, dt.Year, dt.Month);
                break;
            case 2://secure.Log
                fileName = String.format("{0}\\secure.Log", saveDirectory);
                break;
        }
        secureLog = new OneLogFile(fileName);
    }
    
    private void logClose() {
        //オープン中のログファイルがある場合はクローズする
        if (nomalLog != null) {
            nomalLog.Dispose();
            nomalLog = null;
        }
        if (secureLog != null) {
            secureLog.Dispose();
            secureLog = null;
        }
    }
    //過去ログの自動削除 
    private void logDelete() {

        //自動ログ削除が無効な場合は、処理なし
        if (!useLogClear)
            return;
        //0を指定した場合、削除しない
        if (saveDays == 0)
            return;

        if (!Directory.Exists(saveDirectory)) {
            return;
        }

        if (nomalLog != null) {
            nomalLog.Dispose();
            nomalLog = null;
        }
        if (secureLog != null) {
            secureLog.Dispose();
            secureLog = null;
        }

        // ログディレクトリの検索
        var di = new DirectoryInfo(saveDirectory);

        //一定
        var allLog = di.GetFiles("BlackJumboDog.Log").Union(di.GetFiles("secure.Log"));
        foreach (var f in allLog) {
            Tail(f.FullName, _saveDays);//saveDays日分以外を削除
        }
        //日ごと
        var dayLog = di.GetFiles("bjd.????.??.??.Log").Union(di.GetFiles("secure.????.??.??.Log"));
        foreach (var f in dayLog) {
            var tmp = f.Name.split(".");
            if (tmp.Length == 5) {
                try {
                    int year = Convert.ToInt32(tmp[1]);
                    int month = Convert.ToInt32(tmp[2]);
                    int day = Convert.ToInt32(tmp[3]);

                    DeleteLog(year, month, day, _saveDays, f.FullName);
                } catch{

                }
            }
        }
        //月ごと
        var monLog = di.GetFiles("bjd.????.??.Log").Union(di.GetFiles("secure.????.??.Log"));
        foreach (var f in monLog) {
            var tmp = f.Name.split(".");
            if (tmp.Length == 4) {
                try {
                    var year = Convert.ToInt32(tmp[1]);
                    var month = Convert.ToInt32(tmp[2]);
                    const int day = 30;

                    DeleteLog(year, month, day, _saveDays, f.FullName);
                } catch {

                }
            }
        }
    }

    void deleteLog(int year, int month, int day, int saveDays, string fullName) {
        //日付変換の例外を無視する
        try {
            var targetDt = new DateTime(year, month, day);
            if (_dt.Ticks > targetDt.AddDays(saveDays).Ticks) {
                _logger.Set(LogKind.Detail, null, 9000032, fullName);
                File.Delete(fullName);
            }
        } catch(Exception ex) {
            _logger.Set(LogKind.Error, null, 9000045, string.Format("year={0} mont={1} day={2} {3}", year, month, day, fullName));
            File.Delete(fullName);
        }
    }
    private void tail(string fileName, int saveDays) {

        DateTime now = DateTime.Now;
        var lines = new List<string>();
        using (var fs = new FileStream(fileName, FileMode.Open, FileAccess.Read, FileShare.ReadWrite)) {
            using (var sr = new StreamReader(fs, Encoding.GetEncoding(932))) {
                var isNeed = false;
                while (true) {
                    string str = sr.ReadLine();
                    if (str == null)
                        break;
                    if (isNeed) {
                        lines.Add(str);
                    } else {
                        var tmp = str.Split('\t');
                        if (tmp.Length > 1) {
                            var targetDt = Convert.ToDateTime(tmp[0]);
                            if (now.Ticks < targetDt.AddDays(saveDays).Ticks) {
                                isNeed = true;
                                lines.Add(str);
                            }
                        }
                    }
                }
                sr.Close();
            }
            fs.Close();
        }
        using (var fs = new FileStream(fileName, FileMode.Create, FileAccess.Write, FileShare.ReadWrite)) {
            using (var sw = new StreamWriter(fs, Encoding.GetEncoding(932))) {
                foreach (string str in lines) {
                    sw.WriteLine(str);
                }
                sw.Flush();
                sw.Close();
            }
            fs.Close();
        }
    }

    //ログファイル
    private class OneLogFile implements IDispose {
    	//TODO OneLogFile クラス内グローバル変数の名前変更
        FileStream _fs;
        StreamWriter _sw;

        public OneLogFile(String fileName) {
            _fs = new FileStream(fileName, FileMode.OpenOrCreate, FileAccess.Write, FileShare.ReadWrite);
            _sw = new StreamWriter(_fs, Encoding.GetEncoding(932));
        }
        public void Dispose() {
            _sw.Flush();
            _sw.Close();
            _sw.Dispose();
            _sw = null;
            _fs.Close();
            _fs.Dispose();
            _fs = null;
        }
        public void Set(String str) {
            _fs.Seek(0, SeekOrigin.End);
            _sw.WriteLine(str);
            _sw.Flush();
        }
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

    }
    
    //ログの表示制限
    private class LogLimit {
    	//TODO LogLimit クラス内グローバル変数の名前変更
        private boolean _enabled;
        private ArrayList<String> _ar = new ArrayList<String>();

        //表示制限ルールをログファイルに適用するかどうか
        private boolean useLimitString;

        public boolean getUseLimitString() {
			return useLimitString;
		}

		public LogLimit(OneOption oneOption) {
            _enabled = ((int)oneOption.getValue("EnableLimitString") == 0);
            Dat dat = (Dat)oneOption.getValue("LimitString");
            if (dat != null) {
                for (OneDat o : dat) {
                    if (o.isEnable()) {//有効なデータだけを対象にする
                        _ar.add(o.getStrList().get(0));
                    }
                }
            }
            useLimitString = (boolean)oneOption.getValue("UseLimitString");
        }

        public boolean enable(String logStr) {
            for (String str : _ar) {
                if (logStr.indexOf(str) != -1) {//ヒット
                    return _enabled;
                }
            }
            return !_enabled;
        }
    }

}


