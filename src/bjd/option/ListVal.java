package bjd.option;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;

import bjd.ctrl.ICtrlEventListener;
import bjd.util.ListBase;
import bjd.util.Msg;
import bjd.util.MsgKind;

public class ListVal extends ListBase<OneVal> {

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
		ar.add(oneVal);

		//
		// StringBuilder sb = new StringBuilder();
		// for (OneVal o : list1) {
		// sb.append(String.format(" %s", o.getName()));
		// }
		//
		// // 追加オブジェクトが既に重複している場合
		//
		// // 既存オブジェクトの一覧
		// ArrayList<OneVal> list2 = new ArrayList<>();
		// for (OneVal o : ar) {
		// list2 = o.getList(list2);
		// }
		// sb = new StringBuilder();
		// for (OneVal o : list2) {
		// sb.append(String.format(" %s", o.getName()));
		// }
		//
		// for (OneVal o1 : list1) {
		// for (OneVal o2 : list2) {
		// if (o1.getName().equals(o2.getName())) {
		// Msg.show(MsgKind.Error, String.format(
		// "ListVal.add(%s) 名前が重複しているため追加できませんでした",
		// o1.getName()));
		// return;
		// }
		// }
		// }

	}

	// 階層下のOneValを一覧する
	public ArrayList<OneVal> getList(ArrayList<OneVal> list) {
		if (list == null) {
			list = new ArrayList<>();
		}
		for (OneVal o : ar) {
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
		for (OneVal o : ar) {

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
		for (OneVal o : ar) {
			o.deleteCtrl();
		}
	}

	// コントロールからの値のコピー(isComfirm==true 確認のみ)
	public boolean readCtrl(boolean isComfirm) {
		for (OneVal o : ar) {
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
		for (OneVal o : ar) {
			if (!o.isComplete()) {
				return false;
			}
		}
		return true;
	}

	public void setListener(ICtrlEventListener listener) {
		for (OneVal o : ar) {
			o.setListener(listener);
		}
	}
}
