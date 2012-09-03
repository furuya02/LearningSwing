package bjd.ctrl;

import bjd.option.ListVal;

public class OnePage {
	private String name;
	private String title;
	private ListVal listVal;

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public ListVal getListVal() {
		return listVal;
	}

	public OnePage(String name, String title, ListVal listVal) {
		this.name = name;
		this.title = title;
		this.listVal = listVal;
	}

}
