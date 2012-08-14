package bjd.ctrl;


public class CtrlFolder extends OneCtrl {
	
	public CtrlFolder(String help) {
		super(help);
		
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FOLDER;
	}

}
