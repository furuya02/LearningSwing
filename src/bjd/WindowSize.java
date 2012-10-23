package bjd;

import javax.swing.JFrame;

import bjd.ctrl.ListView;
import bjd.option.Conf;
import bjd.util.IDispose;
import bjd.util.Util;

public final class WindowSize implements IDispose {

	//Windowの外観を保存・復元する
	private Conf conf;
	private Reg reg; //記録する仮想レジストリ

	public WindowSize(Conf conf, String path) {
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

		boolean useLastSize = (boolean) conf.get("useLastSize");
		if (!useLastSize) {
			return;
		}

		String n = frame.getTitle();
		int w = 0;
		int h = 0;
		try {
			w = reg.getInt(String.format("%s_width", n));
			h = reg.getInt(String.format("%s_hight", n));
		} catch (RegException ex) {
		}
		if (h <= 0) {
			h = 400;
		}
		if (w <= 0) {
			w = 800;
		}
		frame.setSize(w, h);

		try {
			int y = reg.getInt(String.format("%s_top", n));
			int x = reg.getInt(String.format("%s_left", n));
			if (y <= 0) {
				y = 0;
			}
			if (x <= 0) {
				x = 0;
			}
			frame.setLocation(x, y);
		} catch (RegException ex) {

		}
	}

	//カラム幅の復元
	public void read(ListView listView) {
		if (listView == null) {
			return;
		}

		if (conf == null) {
			return; //リモート操作の時、ここでオプション取得に失敗する
		}

		boolean useLastSize = (boolean) conf.get("useLastSize");
		if (!useLastSize) {
			return;
		}
		for (int i = 0; i < listView.getColumnCount(); i++) {
			String key = String.format("%s_col-%03d", listView.getName(), i);
			try {
				int width = reg.getInt(key);
				if (width <= 0) {
					width = 100; //最低100を確保する
				}
				listView.setColWidth(i, width);
			} catch (RegException ex) {
				listView.setColWidth(i, 100); //デフォルト値
			}
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
		try {
			reg.setInt(String.format("%s_width", n), w);
			reg.setInt(String.format("%s_hight", n), h);
			reg.setInt(String.format("%s_top", n), y);
			reg.setInt(String.format("%s_left", n), x);
		} catch (RegException e) {
			Util.runtimeError("WindowSize.save()");
		}
		
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
			try {
				reg.setInt(key, listView.getColWidth(i));
			} catch (RegException e) {
				Util.runtimeError("WindowSaze.save()");
			}
		}
	}
}
