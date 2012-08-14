package bjd.ctrl;

public class CtrlMemo extends OneCtrl {
	
	public CtrlMemo(String help) {
		super(help);
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.MEMO;
	}

}
