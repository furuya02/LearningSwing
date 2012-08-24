package bjd.ctrl;

public class CtrlFont extends OneCtrl {
	
	public CtrlFont(String help) {
		super(help);
		
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FONT;
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
