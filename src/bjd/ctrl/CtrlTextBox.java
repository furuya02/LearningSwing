package bjd.ctrl;


public class CtrlTextBox extends OneCtrl {

	public CtrlTextBox(String help) {
		super(help);
		
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.TEXTBOX;
	}
	@Override
	public int abstractCreate(int tabIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

}
