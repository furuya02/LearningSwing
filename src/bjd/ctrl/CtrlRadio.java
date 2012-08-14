package bjd.ctrl;

public class CtrlRadio extends OneCtrl {

	public CtrlRadio(String help) {
		super(help);
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.RADIO;
	}

}
