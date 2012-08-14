package bjd.ctrl;


public class CtrlInt extends OneCtrl {

	public CtrlInt(String help) {
		super(help);
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.INT;
	}

}
