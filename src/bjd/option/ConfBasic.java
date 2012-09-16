package bjd.option;

import java.lang.reflect.Field;

public final class ConfBasic extends OneConf {

	private boolean useExitDlg; //終了確認
	private boolean useLastSize; //前回起動時のウインドウサイズを記憶
	private boolean isWindowOpen; // 起動時にウインドウを開く
	private boolean useAdminPassword; // ウインドウ表示時に管理者パスワードを使用する
	private String password; // 管理者パスワード
	private String serverName; // サーバ名
	private boolean editBrowse; // フォルダ・ファイル選択を編集にする
	private int lang; // 言語  Japanese=0 English=1
	
	public ConfBasic(OneOption option) {
		if (option != null) {
			useExitDlg = (boolean) option.getValue("useExitDlg");
			useLastSize = (boolean) option.getValue("useLastSize");
			isWindowOpen = (boolean) option.getValue("isWindowOpen");
			useAdminPassword = (boolean) option.getValue("useAdminPassword");
			password = (String) option.getValue("password");
			serverName = (String) option.getValue("serverName");
			editBrowse = (boolean) option.getValue("editBrowse");
			lang = (int) option.getValue("lang");
		}
	}

	@Override
	protected Field abstractGetField(String tag) {
		try {
			return (ConfBasic.class).getDeclaredField(tag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean useExitDlg() {
		return useExitDlg;
	}

	public boolean useLastSize() {
		return useLastSize;
	}

	public boolean windowOpen() {
		return isWindowOpen;
	}

	public boolean useAdminPassword() {
		return useAdminPassword;
	}

	public String password() {
		return password;
	}

	public String serverName() {
		return serverName;
	}

	public boolean editBrowse() {
		return editBrowse;
	}

	public int lang() {
		return lang;
	}

}
