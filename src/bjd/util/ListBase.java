package bjd.util;

import java.util.ArrayList;
import java.util.Iterator;


//****************************************************************
// オリジナルのListクラスを生成する場合の基底クラス
// Tの指定するクラスは IDisposableの制約がある
//****************************************************************
public abstract class ListBase<T extends IDispose> implements Iterable<T>, Iterator<T> {	
	protected ArrayList<T> ar = new ArrayList<T>();
	private int index;

	public ListBase() {
		index = 0;
	}

	public void dispose() {
		for (T o : ar) {
			o.dispose(); //終了処理
		}
		ar.clear(); //破棄
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
		; //未実装
	}
}

