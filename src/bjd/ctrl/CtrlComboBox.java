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

	@Override
	public int abstractCreate(int tabIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int abstractDelete() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object abstractRead() {
		//return combobox.getSelectedItem();
		return -1;
	}

}

