package bjd.ctrl;

import java.util.ArrayList;

public class CtrlComboBox extends OneCtrl {
	
	private ArrayList<String>list = new ArrayList<>();

	public ArrayList<String> getList() {
		return list;
	}
	
	public CtrlComboBox(String help, ArrayList<String> list) {
		super(help);
		this.list = list;
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.COMBOBOX;
	}

}

