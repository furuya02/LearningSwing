package bjd.menu;

import bjd.util.ListBase;

public final class ListMenu extends ListBase<OneMenu> {

	public OneMenu add(OneMenu o) {
		ar.add(o);
		return o;
	}

	public OneMenu insert(int index, OneMenu o) {
		ar.add(index, o);
		return o;
	}
}
