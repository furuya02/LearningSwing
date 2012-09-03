package bjd.option;

import java.util.ArrayList;

import javax.swing.JPanel;

import bjd.ctrl.CtrlSize;
import bjd.ctrl.ICtrlEventListener;
import bjd.util.ListBase;
import bjd.util.Msg;
import bjd.util.MsgKind;

public class ListVal extends ListBase<OneVal> {

    private CtrlSize ctrlSize = null;

    public void add(OneVal oneVal) {

        // System.out.println(String.format(">ListVal.add(%s) 追加",oneVal.getName()));

        // 追加オブジェクトの一覧
        ArrayList<OneVal> list1 = new ArrayList<>();
        list1 = oneVal.getOneValList(list1);
        StringBuilder sb = new StringBuilder();
        for (OneVal o : list1) {
            sb.append(String.format(" %s", o.getName()));
        }
        // System.out.println(String.format("追加オブジェクトの一覧 = %s",sb.toString()));

        // 追加オブジェクトが既に重複している場合

        // 既存オブジェクトの一覧
        ArrayList<OneVal> list2 = new ArrayList<>();
        for (OneVal o : ar) {
            list2 = o.getOneValList(list2);
        }
        sb = new StringBuilder();
        for (OneVal o : list2) {
            sb.append(String.format(" %s", o.getName()));
        }
        // System.out.println(String.format("既存オブジェクトの一覧 = %s",sb.toString()));

        for (OneVal o1 : list1) {
            for (OneVal o2 : list2) {
                if (o1.getName().equals(o2.getName())) {
                    Msg.show(MsgKind.Error, String.format(
                            "ListVal.add(%s) 名前が重複しているため追加できませんでした",
                            o1.getName()));
                    return;
                }
            }
        }

        // 重複が無いので追加する
        ar.add(oneVal);
    }

    // 階層下のOneValを一覧する
    public ArrayList<OneVal> getOneValList(ArrayList<OneVal> list) {
        if (list == null) {
            list = new ArrayList<>();
        }

        for (OneVal o : ar) {
            list = o.getOneValList(list);
        }
        return list;
    }

    public OneVal search(String name) {
        ArrayList<OneVal> list = new ArrayList<>();
        for (OneVal o : ar) {
            list = o.getOneValList(list);
        }
        for (OneVal o : list) {
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
            CtrlSize ctrlSize = o.getCtrlSize();
            if (h < y + ctrlSize.getHeight()) {
                h = y + ctrlSize.getHeight();
            }
            x += ctrlSize.getWidth();
            if (w < x) {
                w = x;
            }

            if (o.getCrlf() == Crlf.NEXTLINE) {
                y = h;
                x = baseX;
            }
        }
        // 開始位置から移動したオフセットで、このListValオブジェクトのwidth,heightを算出する
        ctrlSize = new CtrlSize(w - baseX, h - baseY);
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

    public CtrlSize getCtrlSize() {
        if (ctrlSize == null) {
            throw new ExceptionInInitializerError();
        }
        return ctrlSize;
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
