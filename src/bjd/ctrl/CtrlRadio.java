package bjd.ctrl;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import bjd.util.Msg;
import bjd.util.MsgKind;

public class CtrlRadio extends OneCtrl implements ActionListener {

	private int width;
	private int colMax;
	private String[] list;

	private JPanel groupPanel = null;
	private JRadioButton[] radioButtonList = null;

	public CtrlRadio(String help, String[] list, int width, int colMax) {
		super(help);

		this.list = list;
		this.width = width;
		this.colMax = colMax;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.RADIO;
	}

	@Override
	protected void abstractCreate(Object value) {

		// 行数及び１項目の横幅の計算
		// 行数
		int rowMax = list.length / colMax;
		if (list.length % colMax != 0) {
			rowMax++;
		}
		// 1項目ごとの横幅
		int spanWidth = width / colMax;
		System.out.println(String.format("colMax=%d rowMax=%d spanWidth=%d", colMax, rowMax, spanWidth));

		int left = margin;
		int top = margin;
		int height = defaultHeight * rowMax + 20;

		// ラジオボタンを囲むボーダライン（groupPanel）の生成
		groupPanel = (JPanel) create(panel, new JPanel(new GridLayout(0, 1)), left, top);
		groupPanel.setBorder(BorderFactory.createTitledBorder(help));
		groupPanel.setSize(width, height);

		// ラジオボタンの生成
		radioButtonList = new JRadioButton[list.length];
		// groupPanelの中のオフセット
		int l = 10;
		int t = 18;
		ButtonGroup buttonGroup = new ButtonGroup(); // ボタンのグループ化
		for (int i = 0; i < list.length; i++) {
			if (i % colMax == 0) {
				System.out.println(String.format("i/colMax==0 i=%d", i));
				l = 10;
				if (i != 0) {
					t += defaultHeight;
				}
			}
			radioButtonList[i] = (JRadioButton) create(groupPanel, new JRadioButton(list[i]), l, t);
			radioButtonList[i].addActionListener(this);

			l += spanWidth;
			buttonGroup.add(radioButtonList[i]);
		}
		left += groupPanel.getWidth(); // オフセット移動

		//値の設定
		abstractWrite(value);
		// パネルのサイズ設定
		panel.setSize(left + margin, height + margin * 2);

	}

	@Override
	protected void abstractDelete() {
		for (int i = 0; i < radioButtonList.length; i++) {
			remove(panel, radioButtonList[i]);
			radioButtonList[i] = null;

		}
		radioButtonList = null;
		remove(panel, groupPanel);
		groupPanel = null;
	}

	@Override
	protected Object abstractRead() {
		for (int i = 0; i < list.length; i++) {
			if (radioButtonList[i].isSelected()) {
				return i;
			}
		}
		Msg.show(MsgKind.Error, "選択されているラジオボタンがありません");
		return 0;
	}

	@Override
	protected void abstractWrite(Object value) {
		radioButtonList[(int) value].setSelected(true);
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		for (int i = 0; i < radioButtonList.length; i++) {
			if (radioButtonList[i] != null) {
				radioButtonList[i].setEnabled(enabled);
			}
		}
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void actionPerformed(ActionEvent e) {
		setOnChange();
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		return true;
	}

	@Override
	protected String abstractToText() {
		for (int i = 0; i < radioButtonList.length; i++) {
			if (radioButtonList[i].isSelected()) {
				return String.valueOf(i);
			}

		}
		throw new UnsupportedOperationException("CtrlRadio.java abstractToText()");
	}

	@Override
	protected void abstractFromText(String s) {
		int n = Integer.valueOf(s);
		if (0 < n && n <= radioButtonList.length) {
			radioButtonList[n].setSelected(true);
		} else {
			throw new UnsupportedOperationException("CtrlRadio.java abstractFromText()");
		}
	}

	@Override
	protected void abstractClear() {
		radioButtonList[0].setSelected(true);
	}
}
