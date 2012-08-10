package sample;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.security.acl.Owner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OptionDlg extends JDialog {

	//ダイアログの戻り値（OKボタンで閉じたかどうか）
	private boolean isOk = false;
	private static final int DLG_WIDTH = 500;
	private static final int DLG_HEIGHT = 400;
	
	public OptionDlg(JFrame frame) {
		super(frame);
		
		setLocationRelativeTo(frame); //親ウインドウの中央）
		setSize(DLG_WIDTH, DLG_HEIGHT);	
		
		JPanel contentPanel = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
	
		//ボタンPanel
		JPanel buttonPanel = new JPanel();
		//OK　ボタン
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				isOk = true;
				setVisible(false);
			}
		});
		buttonPanel.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		//Cancel　ボタン
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		buttonPanel.add(cancelButton);
	}

	public boolean showDialog() {
		setModal(true);
		setVisible(true);
		return isOk;
	}
}
