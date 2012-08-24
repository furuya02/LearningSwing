package bjd.ctrl;

public class CtrlHidden extends OneCtrl {
	public CtrlHidden(String help) {
		super(help);
		
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.HIDDEN;
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
