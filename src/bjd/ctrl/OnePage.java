package bjd.ctrl;

import bjd.option.ListVal;
import bjd.option.OneVal;

public class OnePage {
	private String name;
	private String title;
	private ListVal listVal = new ListVal();

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public OnePage(String name, String title) {
		this.name = name;
		this.title = title;
	}

	//OnePage(CtrlTabPage.pageList) CtrlGroup CtrlDatにのみ存在する
    public ListVal getListVal() {
        return listVal;
    }

    public void add(OneVal oneVal) {
		listVal.add(oneVal);
	}
    

}
