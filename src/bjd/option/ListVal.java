package bjd.option;
import javax.swing.JPanel;

import bjd.ctrl.CtrlSize;
import bjd.ctrl.OneCtrl;
import bjd.util.ListBase;

public class ListVal extends ListBase<OneVal> {

	private CtrlSize ctrlSize = null;
	
	public void add(OneVal oneVal) {
		ar.add(oneVal);
	}
	//コントロール生成
	public int createCtrl(JPanel mainPanel, int baseX, int baseY , int tabIndex) {
		
		//オフセット計算用
		int x = baseX;
		int y = baseY;
		int h = 0; //１行の中で一番背の高いオブジェクトの高さを保持する・
		int w = 0; //xオフセットの最大値を保持する
		for (OneVal o : ar) {
			tabIndex = o.createCtrl(mainPanel, x, y, tabIndex);

			//すべてのコントロールを作成した総サイズを求める
			CtrlSize ctrlSize = o.getCtrlSize();
			if (h < ctrlSize.getHeight()) {
				h = ctrlSize.getHeight();
			}
			if (o.getCrlf() == Crlf.NEXTLINE) {
				y += h;
				x = baseX;
				h = 0;
			} else {
				x += ctrlSize.getWidth();
				if (w < x) {
					w = x;
				}
			}
		}
		//開始位置から移動したオフセットで、このListValオブジェクトのwidth,heightを算出する
		ctrlSize = new CtrlSize(w - baseX, y - baseY + h);
		
		return tabIndex;
	}
	//コントロール破棄
	public void deleteCtrl(){
		for (OneVal o : ar) {
			o.deleteCtrl();
		}		
	}
	//コントロールからの値のコピー
	public void readCtrl() {
		for (OneVal o : ar) {
			o.readCtrl();
		}		
	}
	public CtrlSize getCtrlSize() {
		if (ctrlSize == null) {
			throw new ExceptionInInitializerError();
		}
		return ctrlSize;
	}
}
