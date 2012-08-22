package bjd.ctrl;


public class CtrlFile extends CtrlBrowse {
	
	public CtrlFile(String help , int width) {
		super(help, width);
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FILE;
	}

}
