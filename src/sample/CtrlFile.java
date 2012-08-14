package sample;

public class CtrlFile extends OneCtrl {
	public CtrlFile(String help) {
		super(help);
		
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FILE;
	}

}
