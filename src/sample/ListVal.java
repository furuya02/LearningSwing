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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}

	@Override
	public OneVal next() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	@Override
	public void remove() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}
}
