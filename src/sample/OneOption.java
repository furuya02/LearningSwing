package sample;

import javax.swing.JPanel;

public class OneOption {
	
	private ListVal listVal = new ListVal();
	
	public void CreateDlg(JPanel mainPanel){
		for(OneVal o : listVal){
		
		}
	}
	
	public void add(OneVal oneVal){
		listVal.add(oneVal);
	}
}
