package bjd.ctrl;

public class CtrlMemo extends OneCtrl {
	
	public CtrlMemo(String help) {
		super(help);
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.MEMO;
	}
	@Override
	public int abstractCreate(int tabIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

}
