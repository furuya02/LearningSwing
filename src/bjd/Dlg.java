package bjd;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import bjd.util.Util;

@SuppressWarnings("serial")
public abstract class Dlg extends JDialog implements WindowListener, ActionListener {

	// ダイアログの戻り値（OKボタンで閉じたかどうか）
	private boolean isOk = false;
	protected JPanel mainPanel = new JPanel();

	public Dlg(JFrame frame, int width, int height) {
		super(frame);
		setSize(width, height);
		setLocationRelativeTo(frame); // 親ウインドウの中央）

		// WindowListenerとして自分自身を登録
		addWindowListener(this);

		// ESCキーで閉じる
		InputMap imap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
		getRootPane().getActionMap().put("escape", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		// mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		// ボタンパネル
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// OK　ボタン
		JButton btn = Util.createButton(buttonPanel, "OK", "OK", this, 75);
		getRootPane().setDefaultButton(btn);
		// Cancel　ボタン
		Util.createButton(buttonPanel, "Cancel", "CANCEL", this, 75);
	}

	public boolean showDialog() {
		setModal(true);
		setVisible(true);
		return isOk;
	}

	protected abstract boolean onOk();

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(String.format("actionPerformed() getActionCommand=%s",e.getActionCommand()));
		if (e.getActionCommand().equals("OK")) {
			isOk = true;
			if (!onOk()) {
				return;
			}
		}
		dispose();

	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// System.out.println("windowActivated");

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// System.out.println("windowClosed");

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// System.out.println("windowClosing");
		// ×ボタンが押されたとき、ここしか通らないので、ここで破棄する
		dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// System.out.println("windowDeactivated");

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// System.out.println("windowDeiconified");

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// System.out.println("windowIconified");

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// System.out.println("windowOpened");
	}
}
