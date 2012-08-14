package bjd.ctrl;


public class CtrlCheckBox extends OneCtrl {
	public CtrlCheckBox(String help) {
		super(help);
		
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.CHECKBOX;
	}

}
