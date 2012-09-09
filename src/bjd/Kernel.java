package bjd;

import java.io.File;

import bjd.log.Logger;
import bjd.net.LocalAddress;
import bjd.option.ListOption;
import bjd.option.OneOption;
import bjd.util.IDispose;

public final class Kernel implements IDispose {
	private RunMode runMode = RunMode.Normal;
	private boolean jp = true;
	private LocalAddress localAddress;
	private ListOption listOption;
    
	public LocalAddress getLocalAddress() {
		return localAddress;
	}
	
	public ListOption getListOption() {
		return listOption;
	}

	public boolean getJp() {
		return jp;
	}

	public RunMode getRunMode() {
		return runMode;
	}

	public Kernel() {
		localAddress = new LocalAddress();

		listOption = new ListOption(this);
		listOption.initialize(); // dllからのリスト初期化
	}

	//オプションの値取得
	public Object getOptionVal(String nameTag, String name) {
		OneOption oneOption = listOption.get(nameTag);
		if (oneOption != null) {
			return oneOption.getValue(name);
		}
		throw new UnsupportedOperationException(String.format("Kernel.java getOptionVal() 設計に問題があります %s.%s",nameTag,name));
	}

	@Override
	public void dispose() {
//        if (RunMode != RunMode.Service && RunMode != RunMode.Remote) {
//            //**********************************************
//            // 一旦ファイルを削除して現在有効なものだけを書き戻す
//            //**********************************************
//            var iniDb = new IniDb(ProgDir(),"Option");
//            iniDb.DeleteIni();
			  listOption.save();
//            //Ver5.5.1 設定ファイルの保存に成功した時は、bakファイルを削除する
//            iniDb.DeleteBak();
//
            //**********************************************
            // 破棄
            //**********************************************
//            ListServer.Dispose();//各サーバは停止される
			  listOption.dispose();
//			  ListTool.Dispose();
//            MailBox = null;
//        }
//        if (RemoteClient != null)
//            RemoteClient.Dispose();
//
//        View.Dispose();
//        if (TraceDlg != null)
//            TraceDlg.Dispose();
//
//        WindowSize.Dispose();//DisposeしないとReg.Dispose(保存)されない
	}

	public Logger createLogger(String string, boolean b, Object object) {
		//TODO kernel.createLogger() 未実装
		return null;
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
