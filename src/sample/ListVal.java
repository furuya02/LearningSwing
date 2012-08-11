package sample;

import java.util.ArrayList;
import java.util.Iterator;

public class ListVal implements Iterable<OneVal> , Iterator<OneVal>{
	private ArrayList<OneVal> ar = new ArrayList<OneVal>();
	
	public ListVal() {
		
	}
	
	public void add(OneVal oneVal) {
		ar.add(oneVal);
	}

	@Override
	public Iterator iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public OneVal next() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void remove() {
		// TODO 自動生成されたメソッド・スタブ
	}
}
