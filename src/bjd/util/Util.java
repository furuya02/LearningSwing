package bjd.util;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;

public final class Util {

	private Util() {
		// コンストラクタの隠蔽
	}

	public static JButton createButton(JComponent owner, String text, String actionCommand, ActionListener actionListener, int width) {
		JButton btn = new JButton(text);
		btn.setActionCommand(actionCommand);
		btn.addActionListener(actionListener);
		btn.setPreferredSize(new Dimension(75, 25));
		owner.add(btn);
		return btn;
	}

	public static ArrayList<String> textFileRead(File file) {
		ArrayList<String> lines = new ArrayList<>();
		try {
			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				while (true) {
					String str = br.readLine();
					if (str == null) {
						break;
					}
					System.out.println(str);
					lines.add(str);
				}
				br.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return lines;
	}

	public static boolean textFileSave(File file, ArrayList<String> lines) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String l : lines) {
				bw.newLine();
				bw.write(l);
			}
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
}
