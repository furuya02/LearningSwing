package bjd.log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.Font;

import bjd.Kernel;
import bjd.ctrl.ListView;
import bjd.util.IDispose;
import bjd.util.Msg;
import bjd.util.MsgKind;

public final class LogView implements IDispose {

	private Kernel kernel;
	private ListView listView;
	private Timer timer;
	private ArrayList<OneLog> ar = new ArrayList<OneLog>();

	public LogView(Kernel kernel, ListView listView) {
		if (listView == null) {
			return;
		}

		this.kernel = kernel;
		this.listView = listView;

		//タイマー（表示）イベント処理
		timer = new Timer();
		timer.schedule(new MyTimer(), 0, 100);
		// timer = new Timer{Enabled = true, Interval = 100};
		// timer.Tick += TimerTick;
	}

	//タイマー(表示)イベント
	class MyTimer extends TimerTask {
		public void run() {
			if (listView == null) {
				return;
			}
			if (ar.size() == 0) {
				return;
			}
			timer.cancel(); //一時停止
			synchronized (this) {
				//				listView.BeginUpdate();

				//一回のイベントで処理する最大数は100行まで
				ArrayList<OneLog> list = new ArrayList<>();
				for (int i = 0; i < 300 && 0 < ar.size(); i++) {
					list.add(ar.get(0));
					ar.remove(0);
				}
				disp(list);

				//				listView.EndUpdate();

			}
			//１行の高さを計算してスクロールする
			//			listView.EnsureVisible(listView.Items[listView.Items.Count - 1].Index);
			timer.schedule(new MyTimer(), 0, 100); //再開

		}

	}

	public void dispose() {
		timer.cancel();
		timer = null;
		if (listView != null) {
			listView.dispose();
		}

	}

	public void initFont() {
		if (kernel != null) {
			Font font = (Font) kernel.getListOption().get("Log").getValue("font");
			if (font != null) {
				//listView.Font = font;
			}
		}
	}

	//ログビューへの表示(リモートからも使用される)
	public void append(OneLog oneLog) {
		if (listView == null) {
			return;
		}
		synchronized (this) {
			ar.add(oneLog);
		}
	}

	//選択されたログをクリップボードにコピーする
	public void setClipboard() {
		if (listView == null) {
			return;
		}
		//
		//		StringBuilder sb = new StringBuilder();
		//		var colMax = listView.Columns.Count;
		//		for (int c = 0; c < colMax; c++) {
		//			sb.append(listView.Columns[c].Text);
		//			sb.append("\t");
		//		}
		//		sb.append("\r\n");
		//		for (int i = 0; i < listView.SelectedItems.Count; i++) {
		//			for (int c = 0; c < colMax; c++) {
		//				sb.append(listView.SelectedItems[i].SubItems[c].Text);
		//				sb.append("\t");
		//
		//			}
		//			sb.append("\r\n");
		//		}
		//		Clipboard.SetText(sb.ToString());
	}

	//表示ログをクリア
	public void clear() {
		if (listView == null) {
			return;
		}
		listView.itemClear();
	}

	//このメソッドはタイマースレッドからのみ使用される
	void disp(ArrayList<OneLog> list) {
		try {
			for (OneLog oneLog : list) {
				if (listView == null) {
					break;
				}
				//リストビューへの出力       
				String [] s = new String[8];
				s[0] = oneLog.getDateStr();
				s[1] = oneLog.getLogKind();
				s[2] = oneLog.getThreadId();
				s[3] = oneLog.getNameTag();
				s[4] = oneLog.getRemoteAddr();
				s[5] = oneLog.getMessageNo();
				s[6] = oneLog.getMessage();
				s[7] = oneLog.getDetailInfomation();
				listView.itemAdd(s);
				
			}
		} catch (Exception ex) {
			StringBuilder sb = new StringBuilder();
			sb.append(ex.getMessage() + "\r\n");
			for (OneLog oneLog : list) {
				sb.append(String.format("%s %s\r\n", oneLog.getMessageNo(), oneLog.getMessage()));
			}
			Msg.show(MsgKind.Error, sb.toString());
		}
	}

}
