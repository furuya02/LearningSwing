package bjd;

import javax.swing.JFrame;

import bjd.ctrl.ListView;
import bjd.option.ConfBasic;
import bjd.util.IDispose;

public final class WindowSize implements IDispose {

	//Windowの外観を保存・復元する
	private ConfBasic conf;
	private Reg reg; //記録する仮想レジストリ

	public WindowSize(ConfBasic conf, String path) {
		this.conf = conf; 
		reg = new Reg(path); //ウインドサイズ等を記録する仮想レジストリ
	}

	@Override
	public void dispose() {
		//明示的に呼ばないと、保存されない
		reg.dispose(); //Regの保存
	}

	//ウインドウサイズの復元
	public void read(JFrame frame) {
		if (frame == null) {
			return;
		}
		if (conf == null) {
			return; //リモート操作の時、ここでオプション取得に失敗する
		}

		boolean useLastSize = conf.useLastSize();
		if (!useLastSize) {
			return;
		}

		String n = frame.getTitle();
		int w = reg.getInt(String.format("%s_width", n));
		if (w <= 0) {
			w = 800;
		}
		int h = reg.getInt(String.format("%s_hight", n));
		if (h <= 0) {
			h = 400;
		}
		frame.setSize(w, h);

		int y = reg.getInt(String.format("%s_top", n));
		if (y != -1) {
			if (y <= 0) {
				y = 0;
			}
		}
		int x = reg.getInt(String.format("%s_left", n));
		if (x == -1) {
			return;
		}
		if (x <= 0) {
			x = 0;
		}
		frame.setLocation(x, y);

	}

	//カラム幅の復元
	public void read(ListView listView) {
		if (listView == null) {
			return;
		}

		if (conf == null) {
			return; //リモート操作の時、ここでオプション取得に失敗する
		}

		boolean useLastSize = conf.useLastSize();
		if (!useLastSize) {
			return;
		}
		for (int i = 0; i < listView.getColumnCount(); i++) {
			String key = String.format("%s_col-%03d", listView.getName(), i);
			int width = reg.getInt(key);
			if (width <= 0) {
				width = 100; //最低100を確保する
			}
			listView.setColWidth(i, width);
		}
	}

	//ウインドウサイズの保存
	public void save(JFrame frame) {
		if (frame == null) {
			return;
		}
		String n = frame.getTitle();
		int w = frame.getWidth();
		int h = frame.getHeight();
		int x = frame.getX();
		int y = frame.getY();
		reg.setInt(String.format("%s_width", n), w);
		reg.setInt(String.format("%s_hight", n), h);
		reg.setInt(String.format("%s_top", n), y);
		reg.setInt(String.format("%s_left", n), x);

		//		if (form.WindowState == FormWindowState.Normal) {
		//			reg.SetInt(String.format("%s_width", form.Text), form.Width);
		//			reg.SetInt(String.format("%s_hight", form.Text), form.Height);
		//
		//			//Ver5.5.3 終了位置の保存
		//			reg.SetInt(String.format("%s_top", form.Text), form.Top);
		//			reg.SetInt(String.format("%s_left", form.Text), form.Left);
		//		}

	}

	//カラム幅の保存
	public void save(ListView listView) {
		if (listView == null) {
			return;
		}
		for (int i = 0; i < listView.getColumnCount(); i++) {
			String key = String.format("%s_col-%03d", listView.getName(), i);
			reg.setInt(key, listView.getColWidth(i));
		}
	}
}
