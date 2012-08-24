package bjd.ctrl;

public class CtrlRadio extends OneCtrl {

	public CtrlRadio(String help) {
		super(help);
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.RADIO;
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
