package bjd.option;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;

import bjd.ctrl.ICtrlEventListener;
import bjd.util.ListBase;
import bjd.util.Msg;
import bjd.util.MsgKind;

public final class ListVal extends ListBase<OneVal> {

	private Dimension dimension = null;

	public void add(OneVal oneVal) {

		// 追加オブジェクトの一覧
		ArrayList<OneVal> list = oneVal.getList(null);

		for (OneVal o : list) {
			if (null != search(o.getName())) {
				Msg.show(MsgKind.Error, String.format(
						"ListVal.add(%s) 名前が重複しているため追加できませんでした", o.getName()));
				return;
			}
		}
		// 重複が無いので追加する
		getAr().add(oneVal);
	}

	// 階層下のOneValを一覧する
	public ArrayList<OneVal> getList(ArrayList<OneVal> list) {
		if (list == null) {
			list = new ArrayList<>();
		}
		for (OneVal o : getAr()) {
			list = o.getList(list);
		}
		return list;
	}

	public OneVal search(String name) {
		for (OneVal o : getList(null)) {
			if (o.getName().equals(name)) {
				return o;
			}
		}
		return null;
	}

	// コントロール生成
	public void createCtrl(JPanel mainPanel, int baseX, int baseY) {

		// オフセット計算用
		int x = baseX;
		int y = baseY;
		int h = y; // １行の中で一番背の高いオブジェクトの高さを保持する・
		int w = x; // xオフセットの最大値を保持する
		for (OneVal o : getAr()) {

			o.createCtrl(mainPanel, x, y);

			// すべてのコントロールを作成した総サイズを求める
			Dimension dimension = o.getSize();
			if (h < y + dimension.height) {
				h = y + dimension.height;
			}
			x += dimension.width;
			if (w < x) {
				w = x;
			}

			if (o.getCrlf() == Crlf.NEXTLINE) {
				y = h;
				x = baseX;
			}
		}
		// 開始位置から移動したオフセットで、このListValオブジェクトのwidth,heightを算出する
		dimension = new Dimension(w - baseX, h - baseY);
	}

	// コントロール破棄
	public void deleteCtrl() {
		for (OneVal o : getAr()) {
			o.deleteCtrl();
		}
	}

	// コントロールからの値のコピー(isComfirm==true 確認のみ)
	public boolean readCtrl(boolean isComfirm) {
		for (OneVal o : getAr()) {
			if (!o.readCtrl(isComfirm)) {
				return false;
			}
		}
		return true;
	}

	public Dimension getSize() {
		if (dimension == null) {
			throw new ExceptionInInitializerError();
		}
		return dimension;
	}

	public boolean isComplete() {
		for (OneVal o : getAr()) {
			if (!o.isComplete()) {
				return false;
			}
		}
		return true;
	}

	public void setListener(ICtrlEventListener listener) {
		for (OneVal o : getAr()) {
			o.setListener(listener);
		}
	}
}
