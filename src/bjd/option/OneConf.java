package bjd.option;

import java.lang.reflect.Field;

public abstract class OneConf {

	protected abstract Field abstractGetField(String tag);

	//Test時に値を設定するためのファンクション(通常は使用しない)
	public final void setTestValue(String tag, Object value) {
		try {
			//リフレクションを使用してすべてのプライベート変数にアクセスする
			Field f = abstractGetField(tag);
			
			if (f != null) {
				f.setAccessible(true);
				f.set(this, value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
