package sample;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Dlg extends JDialog {
	
	// ダイアログの戻り値（OKボタンで閉じたかどうか）
	private boolean isOk = false;

	public Dlg(JFrame frame, int width, int height) {
		super(frame);
		setSize(width, height);
		setLocationRelativeTo(frame); // 親ウインドウの中央）

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
