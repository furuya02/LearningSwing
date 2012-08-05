package sample;

//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
//vim:set fileencoding=utf-8:
//http://terai.xrea.jp/Swing/AlwaysOnTop.html
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class test extends JPanel {
	public test(final JFrame frame) {
		super(new BorderLayout());
		JCheckBox checkbox = new JCheckBox(new AbstractAction("Always On Top") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox c = (JCheckBox) e.getSource();
				frame.setAlwaysOnTop(c.isSelected());
			}
		});
		frame.setAlwaysOnTop(true);
		checkbox.setSelected(true);

		JPanel p = new JPanel();
		p.add(checkbox);
		p.setBorder(BorderFactory
				.createTitledBorder("JFrame#setAlwaysOnTop(boolean)"));
		add(p, BorderLayout.NORTH);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setPreferredSize(new Dimension(320, 180));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
          }
      });
	}

	public static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame("AlwaysOnTop");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new test(frame));
		frame.pack();
		frame.setLocationRelativeTo(null);
      frame.setVisible(true);
  }
}

