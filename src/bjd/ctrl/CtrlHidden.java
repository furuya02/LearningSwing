package bjd.ctrl;

public class CtrlHidden extends OneCtrl {
	public CtrlHidden(String help) {
		super(help);
		
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.HIDDEN;
	}

}
