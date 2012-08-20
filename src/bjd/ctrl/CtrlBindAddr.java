package bjd.ctrl;

public class CtrlBindAddr extends OneCtrl {
	public CtrlBindAddr(String help) {
		super(help);
		
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.BINDADDR;
	}

}
