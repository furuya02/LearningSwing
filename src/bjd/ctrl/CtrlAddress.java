package bjd.ctrl;


public class CtrlAddress extends OneCtrl {
	public CtrlAddress(String help) {
		super(help);
		
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.ADDRESSV4;
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

}
