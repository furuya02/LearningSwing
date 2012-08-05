package sample;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class App {

	private JFrame frame;
	private AppMenu appMenu;
	private AppFunc appFunc;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private App() {
		initialize();
		
		appMenu = new AppMenu(frame);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OptionDlg dlg = new OptionDlg();
				dlg.setModal(true);
				dlg.setVisible(true);
				System.out.println("button");
			}
		});
		frame.getContentPane().add(btnNewButton, BorderLayout.NORTH);
		appFunc = new AppFunc(appMenu);

		//appFunc.dispose();
		//appMeu.dispase();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u30BF\u30A4\u30C8\u30EB");
		frame.setFont(new Font("ÉÅÉCÉäÉI", Font.PLAIN, 12));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
