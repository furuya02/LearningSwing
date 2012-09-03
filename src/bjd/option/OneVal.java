package bjd.option;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlGroup;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlPage;
import bjd.ctrl.CtrlSize;
import bjd.ctrl.CtrlType;
import bjd.ctrl.ICtrlEventListener;
import bjd.ctrl.OneCtrl;
import bjd.net.BindAddr;
import bjd.net.Ip;
import bjd.util.Crypt;
import bjd.util.IDispose;
import bjd.util.Msg;
import bjd.util.MsgKind;

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

		ListVal listVal = null;
		if (oneCtrl.getCtrlType() == CtrlType.DAT) {
			listVal = ((CtrlDat) oneCtrl).getListVal();
		} else if (oneCtrl.getCtrlType() == CtrlType.GROUP) {
			listVal = ((CtrlGroup) oneCtrl).getListVal();
        } else if (oneCtrl.getCtrlType() == CtrlType.TABPAGE) {
            listVal = ((CtrlTabPage) oneCtrl).getListVal();
        } else if (oneCtrl.getCtrlType() == CtrlType.PAGE) {
            listVal = ((CtrlPage) oneCtrl).getListVal();
		}
		
		if (listVal != null) {
			ArrayList<OneVal> list = listVal.getOneValList(null);
			for (OneVal o : list) {
				if (name.equals(o.getName())) {
					Msg.show(MsgKind.Error, String.format("名前に重複があります %s", name));
				}
			}
		}
	}

	//階層下のOneValを一覧する
	public ArrayList<OneVal> getOneValList(ArrayList<OneVal> list) {
		if (list == null) {
			list = new ArrayList<>();
		}

		if (oneCtrl.getCtrlType() == CtrlType.DAT) {
			list = ((CtrlDat) oneCtrl).getListVal().getOneValList(list);
		} else if (oneCtrl.getCtrlType() == CtrlType.GROUP) {
			list = ((CtrlGroup) oneCtrl).getListVal().getOneValList(list);
        } else if (oneCtrl.getCtrlType() == CtrlType.TABPAGE) {
            list = ((CtrlTabPage) oneCtrl).getListVal().getOneValList(list);
        } else if (oneCtrl.getCtrlType() == CtrlType.PAGE) {
            list = ((CtrlPage) oneCtrl).getListVal().getOneValList(list);
		}
		list.add(this);
		return list;
	}

	public boolean isComplete() {
		ListVal listVal = null;
		if (oneCtrl.getCtrlType() == CtrlType.DAT) {
			listVal = ((CtrlDat) oneCtrl).getListVal();
		} else if (oneCtrl.getCtrlType() == CtrlType.GROUP) {
			listVal = ((CtrlGroup) oneCtrl).getListVal();
        } else if (oneCtrl.getCtrlType() == CtrlType.TABPAGE) {
            listVal = ((CtrlTabPage) oneCtrl).getListVal();
        } else if (oneCtrl.getCtrlType() == CtrlType.PAGE) {
            listVal = ((CtrlPage) oneCtrl).getListVal();
		}

		if (listVal != null) {
			return listVal.isComplete();
		}
		return oneCtrl.isComplete();
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

	//コントロール生成
	public void createCtrl(JPanel mainPanel, int baseX, int baseY) {
		oneCtrl.create(mainPanel, baseX, baseY, value);
		
		//System.out.println(String.format("OneVal.createCtrl() name=%s", name));
	}

	//コントロール破棄
	public void deleteCtrl() {
		//System.out.println(String.format("OneVal.deleteCtrl() name=%s", name));
		oneCtrl.delete();
	}

	//コントロールからの値のコピー (isComfirm==true 確認のみ)
	public boolean readCtrl(boolean isConfirm) {
		Object o = oneCtrl.read();
		if (o == null) {
			if (isConfirm) { //確認だけの場合は、valueへの値セットは行わない
				Msg.show(MsgKind.Error, String.format("データに誤りがあります 「%s」", oneCtrl.getHelp()));
			}
			return false;
		}
		value = o; //値の読込
		return true;
	}

	public CtrlSize getCtrlSize() {
		return oneCtrl.getCtrlSize();
	}

	public void setListener(ICtrlEventListener listener) {
		oneCtrl.setListener(listener);
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
					int max = ((CtrlComboBox) oneCtrl).getMax();
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
