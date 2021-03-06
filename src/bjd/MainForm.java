package bjd;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;

import bjd.util.Msg;
import bjd.util.MsgKind;

public final class MainForm {

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
			//UIManager.put("Button.font", new Font("SansSerif", Font.PLAIN, 20));
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		appMenu = new AppMenu(mainForm);

		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = Msg.show(MsgKind.Question, "テストメッセージ");
				System.out.print(i);
			}
		});
		mainForm.getContentPane().add(btnNewButton, BorderLayout.NORTH);


		kernel = new Kernel();

		appFunc = new AppFunc(appMenu, mainForm, kernel);

		// appFunc.dispose();
		// appMeu.dispase();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainForm = new JFrame();
		mainForm.setTitle("\u30BF\u30A4\u30C8\u30EB");
		//mainForm.setFont(new Font("メイリオ", Font.PLAIN, 12));
		mainForm.setBounds(100, 100, 450, 300);
		mainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
