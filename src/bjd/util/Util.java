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
import javax.swing.JFileChooser;

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

	private static File selectedFile = null; //前回選択したファイル
	public static File fileChooser(File file) {
		JFileChooser dlg = new JFileChooser();
		//初期化
		if (file != null) { //ファイルの指定がある場合は、それを使用する
			dlg.setSelectedFile(file);
		} else if (selectedFile != null) { //前回選択したものがある場合は、それを使用する
			dlg.setSelectedFile(selectedFile);
		}
		if (dlg.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			selectedFile = dlg.getSelectedFile();
			return selectedFile;
		}
		return null;
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
					lines.add(str);
				}
				br.close();
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return lines;
	}

	public static boolean textFileSave(File file, ArrayList<String> lines) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String l : lines) {
				bw.write(l);
				bw.newLine();
			}
			bw.close();
		} catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
		return true;
	}
}
