package bjd.option;

import java.io.File;

import javax.swing.JPanel;

import bjd.Kernel;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.ICtrlEventListener;
import bjd.ctrl.OneCtrl;
import bjd.util.IniDb;

public abstract class OneOption implements ICtrlEventListener {

	private ListVal listVal = new ListVal();

	protected Kernel kernel;
	private String path; //実態が格納されているモジュール(DLL)のフルパス
	private String nameTag;
	private boolean useAcl;
	private IniDb iniDb;
	
	public final boolean getUseServer() {
		OneVal oneVal = listVal.search("useServer");
		if (oneVal != null) {
			return (boolean) oneVal.getValue();
		}
		return false;
    }

	public OneOption(Kernel kernel, String path, String nameTag, boolean useAcl) {
		this.kernel = kernel;
		this.path = path;
		this.nameTag = nameTag;
		this.useAcl = useAcl;

		String progDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		iniDb = new IniDb(progDir, "Option");
	}

	// ダイアログ作成時の処理
	public final void createDlg(JPanel mainPanel) {
		// 表示開始の基準位置
		int x = 0;
		int y = 0;
		listVal.createCtrl(mainPanel, x, y);
		listVal.setListener(this);

		// コントロールの状態を初期化するために、ダミーのイベントを発生させる
		onChange(null);
	}

	// ダイアログ破棄時の処理
	public final void deleteDlg() {
		listVal.deleteCtrl();
	}

	// ダイアログでOKボタンが押された時の処理
	public final boolean onOk(boolean isComfirm) {
		return listVal.readCtrl(isComfirm);
	}

	public final void add(OneVal oneVal) {
		listVal.add(oneVal);
	}
	
	//OnePageの生成メソッドに変更する
    private void appendServerOption(ListVal listVal, ProtocolKind protocolKind, int port, int timeout, int multiple) {
    	
    	ListVal list = new ListVal();
        list.add(new OneVal("protocolKind", protocolKind, Crlf.CONTONIE, new CtrlComboBox((Kernel.Jp) ? "プロトコル" : "Protocol", new List<string>{ "TCP", "UDP" })));

        list.add(new OneVal("port", port, Crlf.NEXTLINE, new CtrlInt((Kernel.Jp) ? "クライアントから見たポート" : "Port (from client side)", 5)));
        list.add(new OneVal("bindAddress2", new BindAddr(), Crlf.Nextline, new CtrlBindAddr((Kernel.Jp) ? "待ち受けるネットワーク" : "Bind Address", Kernel.LocalAddress.V4, Kernel.LocalAddress.V6)));
        list.add(new OneVal("useResolve", false, Crlf.NEXTLINE, new CtrlCheckBox((kernel.getJp() ? "クライアントのホスト名を逆引きする" : "Reverse pull of host name from IP address")));
        list.add(new OneVal("useDetailsLog", true, Crlf.CONTONIE, new CtrlCheckBox(kernel.getJp() ? "詳細ログを出力する" : "Use Details Log")));
        list.add(new OneVal("multiple", multiple, Crlf.CONTONIE, new CtrlInt(kernel.getJp() ? "同時接続数" : "A repetition thread", 5)));
        list.add(new OneVal("timeOut", timeout, Crlf.NEXTLINE, new CtrlInt(kernel.getJp() ? "タイムアウト(秒)" : "Timeout", 6)));
        listVal.add(new OneVal("GroupServer", list, Crlf.NEXTLINE, new CtrlGroup(kernel.getJp() ? "サーバ基本設定" : "Server Basic Option")));
    }

	
    //値の設定
	public final void setValue(String name, Object value) {
	OneVal oneVal = listVal.search(name);
		if (oneVal != null) {
			//コントロールの値を変更
			oneVal.getOneCtrl().write(value);
			//レジストリへ保存
			save();
			return;
		}
		throw new UnsupportedOperationException("OneOption.java setVal() 名前が見つかりません　" + name);
	}

	//値の取得
	public final Object getValue(String name) {

		//DEBUG
		if (name.equals("editBrowse")) {
			return false;
		}

		OneVal oneVal = listVal.search(name);
		if (oneVal != null) {
			return oneVal.getValue();
		}
		throw new UnsupportedOperationException("OneOption.java getVal() 名前が見つかりません　" + name);
	}	

	protected final OneCtrl getCtrl(String name) {
		OneVal oneVal = listVal.search(name);
		if (oneVal != null) {
			return oneVal.getOneCtrl();
		}
		return null;
	}

	protected abstract void abstractOnChange(OneCtrl oneCtrl);

	@Override
	public final void onChange(OneCtrl oneCtrl) {
		try {
            OneCtrl o = getCtrl("protocolKind");
            if (o != null){
            	o.setEnable(false); // プロトコル 変更不可
            }
			abstractOnChange(oneCtrl);
		} catch (NullPointerException e) {
			// コントロールの破棄後に、このイベントが発生した場合（この例外は無視する）
		}
	}
	
	//レジストリへ保存
	public final void save() {
		iniDb.save(nameTag, listVal);
	}

}
