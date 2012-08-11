package sample;

import java.util.ArrayList;
import java.util.Iterator;

public class ListVal implements Iterable<OneVal> , Iterator<OneVal>{
	private ArrayList<OneVal> ar = new ArrayList<OneVal>();
	private int index;
	public ListVal() {
		index = 0;
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
		return index < ar.size();
	}

	@Override
	public OneVal next() {
		OneVal oneVal = ar.get(index);
        index++;
		return oneVal;
	}

	@Override
	public void remove() {
		ar.remove(index);
        index--;
	}
}
