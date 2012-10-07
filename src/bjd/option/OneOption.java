package bjd.option;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import bjd.Kernel;
import bjd.ctrl.CtrlBindAddr;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlGroup;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlRadio;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.ICtrlEventListener;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.net.BindAddr;
import bjd.net.Ip;
import bjd.net.ProtocolKind;
import bjd.util.IDispose;
import bjd.util.IniDb;
import bjd.util.Util;

public abstract class OneOption implements ICtrlEventListener, IDispose {

	private ListVal listVal = new ListVal();
	protected Kernel kernel;
	private String path; //実態が格納されているモジュール(DLL)のフルパス
	private String nameTag;
	private IniDb iniDb;

	public final ListVal getListVal() {
		return listVal;
	}

	public final String getNameTag() {
		return nameTag;
	}

	public final boolean getUseServer() {
		OneVal oneVal = listVal.search("useServer");
		if (oneVal != null) {
			return (boolean) oneVal.getValue();
		}
		return false;
	}
	
    public abstract String getJpMenu();
    public abstract String getEnMenu();	
    public abstract char getMnemonic();	

	public OneOption(Kernel kernel, String path, String nameTag) {
		this.kernel = kernel;
		this.path = path;
		this.nameTag = nameTag;

		String progDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		iniDb = new IniDb(progDir, "Option");
	}

	//レジストリからの読み込み
	protected final void read() {
		iniDb.read(nameTag, listVal);
	}

	protected final OnePage pageAcl() {
		OnePage onePage = new OnePage("ACL", "ACL");
		onePage.add(new OneVal("enableAcl", 0, Crlf.NEXTLINE, new CtrlRadio(kernel.getJp() ? "指定したアドレスからのアクセスのみを" : "Access of ths user who appoint it", new String[] { kernel.getJp() ? "許可する" : "Allow", kernel.getJp() ? "禁止する" : "Deny" }, 550, 2)));

		ListVal list = new ListVal();
		list.add(new OneVal("aclName", "", Crlf.NEXTLINE, new CtrlTextBox(kernel.getJp() ? "名前（表示名）" : "Name(Display)", 20)));
		list.add(new OneVal("aclAddress", "", Crlf.NEXTLINE, new CtrlTextBox(kernel.getJp() ? "アドレス" : "Address", 20)));
		onePage.add(new OneVal("acl", null, Crlf.NEXTLINE, new CtrlDat(kernel.getJp() ? "利用者（アドレス）の指定" : "Access Control List", list, 320, kernel)));
		return onePage;
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

	//OneValとしてサーバ基本設定を作成する
	protected final OneVal createServerOption(ProtocolKind protocolKind, int port, int timeout, int multiple) {
		ListVal list = new ListVal();
		list.add(new OneVal("protocolKind", 0, Crlf.CONTONIE, new CtrlComboBox(kernel.getJp() ? "プロトコル" : "Protocol", new String[] { "TCP", "UDP" }, 80)));
		list.add(new OneVal("port", port, Crlf.NEXTLINE, new CtrlInt(kernel.getJp() ? "クライアントから見たポート" : "Port (from client side)", 5)));
		ArrayList<Ip> v4 = null;
		ArrayList<Ip> v6 = null;
		if (kernel.getLocalAddress() != null) {
			v4 = kernel.getLocalAddress().getV4();
			v6 = kernel.getLocalAddress().getV6();
		}
		list.add(new OneVal("bindAddress2", new BindAddr(), Crlf.NEXTLINE, new CtrlBindAddr(kernel.getJp() ? "待ち受けるネットワーク" : "Bind Address", v4, v6)));
		list.add(new OneVal("useResolve", false, Crlf.NEXTLINE, new CtrlCheckBox((kernel.getJp() ? "クライアントのホスト名を逆引きする" : "Reverse pull of host name from IP address"))));
		list.add(new OneVal("useDetailsLog", true, Crlf.CONTONIE, new CtrlCheckBox(kernel.getJp() ? "詳細ログを出力する" : "Use Details Log")));
		list.add(new OneVal("multiple", multiple, Crlf.CONTONIE, new CtrlInt(kernel.getJp() ? "同時接続数" : "A repetition thread", 5)));
		list.add(new OneVal("timeOut", timeout, Crlf.NEXTLINE, new CtrlInt(kernel.getJp() ? "タイムアウト(秒)" : "Timeout", 6)));
		return new OneVal("GroupServer", null, Crlf.NEXTLINE, new CtrlGroup(kernel.getJp() ? "サーバ基本設定" : "Server Basic Option", list));
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
		Util.designProblem(String.format("名前が見つかりません name=%s", name));
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
		Util.designProblem(String.format("名前が見つかりません name=%s", name));
		return null;
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
			if (o != null) {
				o.setEnable(false); // プロトコル 変更不可
			}
			abstractOnChange(oneCtrl);
		} catch (NullPointerException e) {
			// コントロールの破棄後に、このイベントが発生した場合（この例外は無視する）
		}
	}

	@Override
	public void dispose() {
	}

	//レジストリへ保存
	public final void save() {
		iniDb.save(nameTag, listVal);
	}

	//	public void read2() {
	//         //opDb.Read(NameTag, allList);//レジストリから取得
	//     }	

}
