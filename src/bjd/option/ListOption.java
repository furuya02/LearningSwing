package bjd.option;

import bjd.Kernel;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlFile;
import bjd.ctrl.CtrlInt;

public class ListOption {
	
	Option option;
	Kernel kernel;
	
	public ListOption(Kernel kernel){
		option = new Option();
		OneVal o1 = new OneVal("useServer1", true, Crlf.CONTONIE, new CtrlInt("サーバを使用する", 3));
		option.add(o1);
		OneVal o2 = new OneVal("xxx2", 123, Crlf.NEXTLINE, new CtrlCheckBox("サーバを使用する"));
		option.add(o2);
		OneVal o3 = new OneVal("fileName", "c:\text.txt", Crlf.CONTONIE, new CtrlFile("ファイル名",20,kernel));
		option.add(o3);
	}

	public OneOption get(String name) {
		return option;
	}

}
