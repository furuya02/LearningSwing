package bjd.option;

import java.awt.Font;

import javax.swing.JPanel;

import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlSize;
import bjd.ctrl.OneCtrl;
import bjd.net.BindAddr;
import bjd.net.Ip;
import bjd.util.Crypt;
import bjd.util.IDispose;

public class OneVal implements IDispose {

	private String name;
	private Object value;
	private Crlf crlf;
	private OneCtrl oneCtrl;

	public OneVal(String name, Object value, Crlf crlf, OneCtrl oneCtrl) {
		this.name = name;
		this.value = value;
		this.crlf = crlf;
		this.oneCtrl = oneCtrl;
		oneCtrl.setOneVal(this); // OneCtrlのOneValはここで初期化される
	}

	@Override
	public void dispose() {
	}

	public OneCtrl getOneCtrl() {
		return oneCtrl;
	}

	public Crlf getCrlf() {
		return crlf;
	}

	public Object getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public int createCtrl(JPanel mainPanel, int baseX, int baseY , int tabIndex) {
		System.out.println(String.format("OneVal.createCtrl() name=%s", name));
		return oneCtrl.create(mainPanel, baseX, baseY, tabIndex);
	}
	
	public void deleteCtrl() {
		System.out.println(String.format("OneVal.deleteCtrl() name=%s", name));
		oneCtrl.delete();
	}
	
	
	public CtrlSize getCtrlSize() {
		return oneCtrl.getCtrlSize();
	}

	/**
	 * 設定ファイル(Option.ini)への出力
	 * 
	 * @param isSecret
	 *            デバッグ用の設定ファイル出力用（パスワード等を***で表現する）
	 * @throws Exception 
	 */
	public String toReg(boolean isSecret) {
		switch (oneCtrl.getCtrlType()) {
			case DAT:
				return ((Dat) value).toReg(isSecret);
			case CHECKBOX:
				return String.valueOf(value);
			case FONT:
				if (value != null) {
					Font font = (Font) value;
					return String.format("%s,%s,%s", font.getName(), font.getStyle(), font.getSize());
				}
				return "";
			case FILE:
			case FOLDER:
			case TEXTBOX:
				return (String) value;
			case HIDDEN:
				if (isSecret) {
					return "***";
				}
				return Crypt.encrypt((String) value);
			case MEMO:
				return ((String) value).replaceAll("\r\n", "\t");
			case RADIO:
			case COMBOBOX:
			case INT:
				return String.valueOf(value);
			case BINDADDR:
				return value.toString();
			case ADDRESSV4:
				return value.toString();
			case TABPAGE:
			case GROUP:
				return "";
			default:
				return ""; //"実装されていないCtrlTypeが指定されました OneVal.toReg()"
		}
	}

	/**
	 * 出力ファイル(Option.ini)からの入力用
	 * 
	 * @param str
	 *            　読み込み行
	 * @return 成否
	 * @throws Exception 
	 */
	public boolean fromReg(String str) {
		if (str == null) {
			value = null;
			return false;
		}
		switch (oneCtrl.getCtrlType()) {
			case DAT:
				CtrlDat ctrlDat = (CtrlDat) oneCtrl;
				Dat dat = new Dat(ctrlDat.getCtrlTypeList());
				if (!dat.fromReg(str)) {
					value = null;
					return false;
				}
				value = dat;
				break;
			case CHECKBOX:
				if (str.equalsIgnoreCase("false") || str.equalsIgnoreCase("true")) {
					value = Boolean.parseBoolean(str);
				} else {
					return false;
				}
				break;
			case FONT:
				value = null;
				String[] tmp = str.split(",");
				if (tmp.length == 3) {
					String name = tmp[0];
					int style = Integer.parseInt(tmp[1]);
					int size = Integer.parseInt(tmp[2]);
					value = new Font(name, style, size);
					// 検証
					Font f = (Font) value;
					if (f.getStyle() != style || f.getSize() < 0) {
						value = null;
						return false;
					}
				}
				break;
			case MEMO:
				try {
					value = str.replaceAll("\t", "\r\n");
				} catch (Exception ex) {
					value = "";
					return false;
				}
				break;
			case FILE:
			case FOLDER:
			case TEXTBOX:
				value = str;
				break;
			case HIDDEN:
				String s = Crypt.decrypt(str);
				if (s.equals("ERROR")) {
					value = "";
					return false;
				}
				value = s;
				break;
			case RADIO:
				try {
					value = Integer.parseInt(str);
				} catch (Exception e) {
					value = 0;
					return false;
				}
				if ((int) value < 0) {
					value = 0;
					return false;
				}
				break;
			case COMBOBOX:
				try {
					int max = ((CtrlComboBox) oneCtrl).getList().size();
					int n = Integer.parseInt(str);
					if (n < 0 || max <= n) {
						value = 0;
						return false;
					}
					value = n;
				} catch (Exception e) {
					value = 0;
					return false;
				}
				break;
			case INT:
				try {
					value = Integer.parseInt(str);
				} catch (Exception e) {
					value = 0;
					return false;
				}
				break;
			case BINDADDR:
				try {
					value = new BindAddr(str);
				} catch (Exception ex) {
					value = 0;
					return false;
				}
				break;
			case ADDRESSV4:
				try {
					value = new Ip(str);
				} catch (Exception ex) {
					value = null;
					return false;
				}
				break;
			case TABPAGE:
			case GROUP:
				break;
			default:
				value = 0;
				return false;
				//"実装されていないCtrlTypeが指定されました OneVal.fromReg()"
		}
		return true;
	}
}
