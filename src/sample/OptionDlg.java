<<<<<<< HEAD
package sample;

import javax.swing.JFrame;

public class OptionDlg extends Dlg {


	private static final int DLG_WIDTH = 500;
	private static final int DLG_HEIGHT = 400;
	
	public OptionDlg(JFrame frame) {
		super(frame, DLG_WIDTH, DLG_HEIGHT);
		
	}
}
=======
package sample;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class OptionDlg extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			OptionDlg dialog = new OptionDlg();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public OptionDlg() {
		setBounds(100, 100, 461, 377);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
		
		setModal(true); //ƒ‚[ƒ_ƒ‹

	}

}
>>>>>>> f460185d3e1ed1a87b0f348c9fe7e1bae0f19a73
