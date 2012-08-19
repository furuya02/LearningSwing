package bjd.ctrl;

import java.util.ArrayList;

public class CtrlDat extends OneCtrl {
	
	private ArrayList<CtrlType> ctrlTypeList = new ArrayList<CtrlType>();;

	public ArrayList<CtrlType> getCtrlTypeList() {
		return ctrlTypeList;
	}

	public CtrlDat(String help, ArrayList<CtrlType> ctrlTypeList) {
		super(help);
		this.ctrlTypeList = ctrlTypeList;
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.DAT;
	}

}
