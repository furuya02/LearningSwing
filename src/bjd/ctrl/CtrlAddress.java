package bjd.ctrl;


public class CtrlAddress extends OneCtrl {
	public CtrlAddress(String help) {
		super(help);
		
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.ADDRESSV4;
	}

}
