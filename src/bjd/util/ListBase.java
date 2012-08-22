package bjd.util;

import java.util.ArrayList;
import java.util.Iterator;


//****************************************************************
// �I���W�i����List�N���X�𐶐�����ꍇ�̊��N���X
// T�̎w�肷��N���X�� IDisposable�̐��񂪂���
//****************************************************************
public abstract class ListBase<T extends IDispose> implements Iterable<T>, Iterator<T> {	
	protected ArrayList<T> ar = new ArrayList<T>();
	private int index;

	public ListBase() {
		index = 0;
	}

	public void dispose() {
		for (T o : ar) {
			o.dispose(); //�I������
		}
		ar.clear(); //�j��
	}
	public int size() {
		return ar.size();
	}
	public void remove(int index) {
		ar.remove(index);
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return index < ar.size();
	}

	@Override
	public T next() {
		T o = ar.get(index);
		index++;
		return o;
	}

	@Override
	public void remove() {
		; //������
	}
}

