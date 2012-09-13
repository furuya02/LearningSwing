package bjd.log;

import java.util.ArrayList;
import java.util.Timer;

import bjd.Kernel;
import bjd.util.IDispose;

public class LogView implements IDispose {

    private Kernel kernel;
//    private ListView listView;
    private Timer timer;
    private ArrayList<OneLog> ar = new ArrayList<OneLog>();

    //public LogView(Kernel kernel,ListView listView) {
    public LogView(Kernel kernel) {
//        if (listView == null)
//            return;
        
        this.kernel = kernel;
//        listView = listView;

        //タイマー（表示）イベント処理
//        timer = new Timer{Enabled = true, Interval = 100};
//        timer.Tick += TimerTick;
    }
    public void dispose() {
//        if (listView != null)
//            listView.Dispose();

    }
//    public void InitFont(){
//        if (kernel != null) {
//            var font = (Font)kernel.ListOption.Get("Log").GetValue("font");
//            if (font != null)
//                listView.Font = font;
//        }
//    }
    
    //ログビューへの表示(リモートからも使用される)
    public void append(OneLog oneLog) {
        //if (listView == null)
        //    return;
        synchronized (this) {
            ar.add(oneLog);
        }
    }

    //選択されたログをクリップボードにコピーする
//    public void SetClipboard() {
//        if (listView == null)
//            return;
//
//        var sb = new StringBuilder();
//        var colMax = listView.Columns.Count;
//        for (int c = 0; c < colMax; c++) {
//            sb.Append(listView.Columns[c].Text);
//            sb.Append("\t");
//        }
//        sb.Append("\r\n");
//        for (int i = 0; i < listView.SelectedItems.Count; i++) {
//            for (int c = 0; c < colMax; c++) {
//                sb.Append(listView.SelectedItems[i].SubItems[c].Text);
//                sb.Append("\t");
//
//            }
//            sb.Append("\r\n");
//        }
//        Clipboard.SetText(sb.ToString());
//    }
    //表示ログをクリア
//    public void Clear() {
//        if (listView == null)
//            return;
//        listView.Items.Clear();
//    }

    //タイマー(表示)イベント
//    void TimerTick(object sender, EventArgs e) {
//        if (ar.Count == 0)
//            return;
//        timer.Enabled = false;
//        lock (this) {
//            listView.BeginUpdate();
//
//            //一回のイベントで処理する最大数は100行まで
//            var list = new List<OneLog>();
//            for (int i = 0; i < 300 && 0 < ar.Count; i++) {
//                list.Add(ar[0]);
//                ar.RemoveAt(0);
//            }
//            Disp(list);
//
//            listView.EndUpdate();
//
//        }
//        //１行の高さを計算してスクロールする
//        listView.EnsureVisible(listView.Items[listView.Items.Count - 1].Index);
//        timer.Enabled = true;
//    }
//
//    //このメソッドはタイマースレッドからのみ使用される
//    void Disp(List<OneLog> list) {
//        try {
//            foreach (OneLog oneLog in list) {
//                if (listView == null)
//                    break;
//                //リストビューへの出力                    
//                ListViewItem item = listView.Items.Add(oneLog.DateStr);
//                item.SubItems.Add(oneLog.LogKind);
//                item.SubItems.Add(oneLog.ThreadId);
//                item.SubItems.Add(oneLog.NameTag);
//                item.SubItems.Add(oneLog.RemoteAddr);
//                item.SubItems.Add(oneLog.MessageNo);
//                item.SubItems.Add(oneLog.Message);
//                item.SubItems.Add(oneLog.DetailInfomation);
//            }
//        } catch (Exception ex) {
//            var sb = new StringBuilder();
//            sb.Append(ex.Message + "\r\n");
//            foreach (var oneLog in list) {
//                string.Format("{0} {1} {2}\r\n", oneLog.MessageNo, oneLog.Message);
//            }
//            Msg.Show(MsgKind.Error, sb.ToString());
//        }
//    }

}
