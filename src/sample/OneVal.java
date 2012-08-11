package sample;

enum Crlf {
    NEXTLINE,
    CONTONIE
}

public class OneVal {
	
	private String name;
	private Object value;
	private Crlf crlf;
	private OneCtrl oneCtrl;

	public OneVal(String name, Object value, Crlf crlf, OneCtrl oneCtrl) {
		this.name = name;
		this.value = value;
		this.crlf = crlf;
		this.oneCtrl = oneCtrl;
		oneCtrl.setOneVal(this); //OneCtrl‚ÌOneVal‚Í‚±‚±‚Å‰Šú‰»‚³‚ê‚é
	}

	public OneCtrl getOneCtrl() {
		return oneCtrl;
	}

	public Crlf getCrlf() {
		return crlf;
	}

	public Object getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
	
}
