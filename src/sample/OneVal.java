package sample;

public class OneVal {
	
	private String name;
	private Object value;
	private Crlf crlf;
	private OneCtrl oneCtrl;

	public OneVal(String name, Object value, Crlf crlf, OneCtrl oneCtrl) {
		this.name = name;
		this.value = value;
		this.crlf = crlf;
		this.oneCtrl = oneCtrl;
		oneCtrl.setOneVal(this); //OneCtrlのOneValはここで初期化される
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

	/**
	 * 設定ファイル(Option.ini)への出力
	 * 
	 * @param isDebug
	 *            デバッグ用の設定ファイル出力用（パスワード等を***で表現する）
	 */
	public String toReg(boolean isDebug) {
		switch (oneCtrl.getCtrlType()) {
		case DAT:
			// var listVal = ((CtrlDat)OneCtrl).ListVal;
			// return ((Dat)Value).ToReg(isDebug, listVal);
		case CHECKBOX:
			return String.valueOf(value);
		case FONT:
			// if (value != null) {
			// var font = (Font)Value;
			// return string.Format("{0},{1},{2}", font.FontFamily.Name,
			// font.Size, font.Style.ToString());
			// }
			return "";
		case FILE:
		case FOLDER:
		case TEXTBOX:
          return (String)value;
            case HIDDEN:
                //return isDebug ? "***" : Crypt.Encrypt((string)Value);
            case MEMO:
                //return Util.SwapStr("\r\n", "\t", (String)value);
            case RADIO:
            case INT:
                return String.valueOf(value);
            case COMBOBOX:
                //return ((CtrlComboBox)OneCtrl).StrList[(int)Value];
            case BINDADDR:
                //return Value.ToString();
            case ADDRESSV4:
                //return Value.ToString();
            case TABPAGE:
            case GROUP:
                return "";
            default:
                //throw new Exception("コントロールの型に応じたテキスト化が実装されていません OneVal::ToText()　" + OneCtrl.GetType());
        }
		return "ERROR";
    }
    /**
     * 出力ファイル(Option.ini)からの入力用
     * @param str　読み込み行
     * @return 成否
     */
    public boolean fromReg(String str) {
        switch (oneCtrl.getCtrlType()) {
            case DAT:
//                var dat = new Dat();
//                dat.FromReg(str);
//                Value = dat;
                break;
            case CHECKBOX:
                try {
                    value = Boolean.parseBoolean(str);
                } catch (Exception e) {
                    value = false;
                }
                break;
            case FONT:
                value = null;
//                if (str != null) {
//                    var tmp = str.Split(',');
//                    if (tmp.Length == 3) {
//                        var family = new FontFamily(tmp[0]);
//                        var size = (float)Convert.ToDouble(tmp[1]);
//                        var style = (FontStyle)Enum.Parse(typeof(FontStyle), tmp[2]);
//                        Value = new Font(family, size, style);
//                    }
//                }
//                break;
            case MEMO:
                //Value = Util.SwapStr("\t", "\r\n", str);
                break;
            case FILE:
            case FOLDER:
            case TEXTBOX:
                value = str;
                break;
            case HIDDEN:
                //Value = Crypt.Decrypt(str);
                break;
            case RADIO:
            case INT:
            	try {
                    value = Integer.parseInt(str);
                } catch (Exception e) {
                    value = 0;
                }
                break;
            case COMBOBOX:
                //var i = ((CtrlComboBox)OneCtrl).StrList.IndexOf(str);
                //Value = i >= 0 ? i : 0;
                break;
            case BINDADDR:
                //value = new BindAddr(str);
                break;
            case ADDRESSV4:
                //value = new Ip(str);
                break;
            case TABPAGE:
            case GROUP:
                break;
            default:
//                throw new Exception("コントロールの型に応じたテキストからの変換が実装されていません OneVal::FromText()　" + OneCtrl.GetType());
        }
        return true;
    }
}
