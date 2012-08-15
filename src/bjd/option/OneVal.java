package bjd.option;

import java.awt.Font;

import bjd.Crypt;
import bjd.ctrl.OneCtrl;

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
		oneCtrl.setOneVal(this); // OneCtrl��OneVal�͂����ŏ����������
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
	 * �ݒ�t�@�C��(Option.ini)�ւ̏o��
	 * 
	 * @param isDebug
	 *            �f�o�b�O�p�̐ݒ�t�@�C���o�͗p�i�p�X���[�h����***�ŕ\������j
	 */
	public String toReg(boolean isDebug) {
		switch (oneCtrl.getCtrlType()) {
			case DAT:
				// var listVal = ((CtrlDat)OneCtrl).ListVal;
				// return ((Dat)Value).ToReg(isDebug, listVal);
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
				return isDebug ? "***" : Crypt.encrypt((String) value);
			case MEMO:
				return ((String) value).replaceAll("\r\n", "\t");
			case RADIO:
			case INT:
				return String.valueOf(value);
			case COMBOBOX:
				// return ((CtrlComboBox)OneCtrl).StrList[(int)Value];
			case BINDADDR:
				// return Value.ToString();
			case ADDRESSV4:
				// return Value.ToString();
			case TABPAGE:
			case GROUP:
				return "";
			default:
				// throw new
				// Exception("�R���g���[���̌^�ɉ������e�L�X�g������������Ă��܂��� OneVal::ToText()�@" +
				// OneCtrl.GetType());
		}
		return "ERROR";
	}

	/**
	 * �o�̓t�@�C��(Option.ini)����̓��͗p
	 * 
	 * @param str
	 *            �@�ǂݍ��ݍs
	 * @return ����
	 */
	public boolean fromReg(String str) {
		switch (oneCtrl.getCtrlType()) {
			case DAT:
				// var dat = new Dat();
				// dat.FromReg(str);
				// Value = dat;
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
				if (str != null) {
					String[] tmp = str.split(",");
					if (tmp.length == 3) {
						String name = tmp[0];
						int style = Integer.parseInt(tmp[1]);
						int size = Integer.parseInt(tmp[2]);
						value = new Font(name, style, size);
						// ����
						Font f = (Font) value;
						if (f.getStyle() != style || f.getSize() < 0) {
							value = null;
							return false;
						}
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
			case INT:
				try {
					value = Integer.parseInt(str);
				} catch (Exception e) {
					value = 0;
					return false;
				}
				break;
			case COMBOBOX:
				// var i = ((CtrlComboBox)OneCtrl).StrList.IndexOf(str);
				// Value = i >= 0 ? i : 0;
				break;
			case BINDADDR:
				// value = new BindAddr(str);
				break;
			case ADDRESSV4:
				// value = new Ip(str);
				break;
			case TABPAGE:
			case GROUP:
				break;
			default:
				// throw new
				// Exception("�R���g���[���̌^�ɉ������e�L�X�g����̕ϊ�����������Ă��܂��� OneVal::FromText()�@"
				// + OneCtrl.GetType());
		}
		return true;
	}
}
