package sample;

public class OneCtrl {
	private String help;
	private OneVal oneVal;
	
	public OneCtrl(String help) {
		this.help = help;
	}
	
	public OneVal getOneVal() {
		return oneVal;
	}
	
	public void setOneVal(OneVal value) {
		oneVal = value;
	}
}
