package bjd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import bjd.ctrl.ListView;
import bjd.ctrl.StatusBar;
import bjd.util.Msg;
import bjd.util.MsgKind;
import java.awt.event.WindowEvent;

public final class MainForm implements WindowListener {

	private JFrame mainForm;
	private AppMenu appMenu;
	private AppFunc appFunc;
	private Kernel kernel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.mainForm.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private MainForm() {
		initialize();

		try {
			// UIManager.put("Button.font", new Font("SansSerif", Font.PLAIN,
			// 20));
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		appMenu = new AppMenu(mainForm);

		kernel = new Kernel();
		appFunc = new AppFunc(this, appMenu, mainForm, kernel);

		ListView listView = new ListView();
		listView.addColumn("1ST");
		listView.addColumn("2ST");
		listView.addColumn("3ST");
		for (int i = 0; i < 100; i++) {
			listView.addRow(new String[] { "1234567890", "abcdefghikl", "c" });
		}
		mainForm.add(listView);

		StatusBar bar = new StatusBar();
		mainForm.add(bar, BorderLayout.PAGE_END);

		// appFunc.dispose();
		// appMeu.dispase();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainForm = new JFrame();
		mainForm.setTitle("\u30BF\u30A4\u30C8\u30EB");
		// mainForm.setFont(new Font("メイリオ", Font.PLAIN, 12));
		mainForm.setBounds(100, 100, 450, 300);
		mainForm.addWindowListener(this);

		//×を押したときにWindowClosingを発生さた後、ウインドウを閉じる
		//mainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//×を押したときにWindowClosingを発生させる（終了はしない）
		mainForm.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	//終了処理
	public void exit() {

		//プログラムの終了確認
		//        if (_kernel.RunMode == RunMode.Normal || _kernel.RunMode == RunMode.NormalRegist) {
		//            if ((bool)_kernel.ListOption.Get("Basic").GetValue("useExitDlg")) {
		//                if (DialogResult.OK != Msg.Show(MsgKind.Question, _kernel.Jp ? "プログラムを終了してよろしいですか" : "May I finish a program?")) {
		//                    e.Cancel = true;//終了処理で中止された場合は、プログラムを終了しない
		//                    return;
		//                }
		//            }
		//        }
		//        _kernel.Dispose();

		//終了確認
		boolean useExitDlg = (boolean) kernel.getOptionVal("Basic", "useExitDlg");
		if (useExitDlg) {
			if (0 != Msg.show(MsgKind.Question, kernel.getJp() ? "プログラムを終了してよろしいですか" : "May I finish a program?")) {
				return; //キャンセル
			}
		}

		kernel.dispose();

		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowActivated()"));
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowClosed()"));
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowClosing()"));
		exit(); //終了
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowDeactivated()"));
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowDeiconified()"));
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowIconified()"));
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowOpend()"));
	}

}
