package bjd.option;

import java.util.ArrayList;
import java.util.Arrays;

//public class OneDat : IDisposable {
public class OneDat {

	private boolean enable;
	private ArrayList<String> strList;
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public ArrayList<String> getStrList() {
		return strList;
	}
	
	public void setStrList(ArrayList<String> strList) {
		this.strList = strList;
	}
	
	public String getStr() {
		StringBuilder sb = new StringBuilder();
		for (String s : strList) {
			if (sb.length() != 0) {
				sb.append('\t');
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	public OneDat(boolean enable, String str) {
		this.enable = enable;
		String[] tmp = str.split("\t");
		//strList = new ArrayList<String>(Arrays.asList(tmp));
		strList = (ArrayList<String>) Arrays.asList(tmp);
	}
}

