package bjd.ctrl;

public class CtrlBindAddr extends OneCtrl {
	public CtrlBindAddr(String help) {
		super(help);
		
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.BINDADDR;
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
		// TODO Auto-generated method stub
		return 0;
	}

}
