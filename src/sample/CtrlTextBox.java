package sample;

public class CtrlTextBox extends OneCtrl {

	public CtrlTextBox(String help) {
		super(help);
		
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.TEXTBOX;
	}

}
