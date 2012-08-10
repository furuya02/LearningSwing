package sample;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class MainForm {

	private JFrame mainForm;
	private AppMenu appMenu;
	private AppFunc appFunc;
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
		
		appMenu = new AppMenu(mainForm);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		mainForm.getContentPane().add(btnNewButton, BorderLayout.NORTH);
		appFunc = new AppFunc(appMenu,mainForm);

		//appFunc.dispose();
		//appMeu.dispase();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainForm = new JFrame();
		mainForm.setTitle("\u30BF\u30A4\u30C8\u30EB");
		mainForm.setFont(new Font("ÉÅÉCÉäÉI", Font.PLAIN, 12));
		mainForm.setBounds(100, 100, 450, 300);
		mainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
