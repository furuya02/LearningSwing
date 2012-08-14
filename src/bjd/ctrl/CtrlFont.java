package bjd.ctrl;

public class CtrlFont extends OneCtrl {
	
	public CtrlFont(String help) {
		super(help);
		
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FONT;
	}

}
