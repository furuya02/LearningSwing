package sample;

import javax.swing.JPanel;

public class OneOption {

	private ListVal listVal = new ListVal();

	public void createDlg(JPanel mainPanel) {
		for (OneVal o : listVal) {
			System.out.println("name=" + o.getName() + "crlf=" + o.getCrlf());
			OneVal i = o;
		}
	}

	public void add(OneVal oneVal) {
		listVal.add(oneVal);
	}
}
