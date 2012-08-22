package bjd.ctrl;

import javax.swing.JLabel;
import javax.swing.JTextField;

//CtrlFile及びCtrlFolderの親クラス
public abstract class CtrlBrowse extends OneCtrl {

	private JTextField textField = null;
	private int width;

	public CtrlBrowse(String help, int width) {
		super(help);
		this.width = width;
	}

	@Override
	public int abstractCreate(int tabIndex) {
		int left = MARGIN;
		int top = MARGIN;

		// ラベルの作成(topの+3は、後のテキストボックスとの高さ調整)
		JLabel label = (JLabel) create(panel, new JLabel(), -1/* tabIndex */, left, top + 3, 0, 0);
		label.setText(help);
		setAutoSize(label); // サイズ自動調整
		// label.setBorder(new LineBorder(Color.RED, 2, true)); //Debug 赤枠

		// オフセット移動
		left += label.getWidth() + MARGIN;

		// テキストボックスの配置
		textField = (JTextField) create(panel, new JTextField(), -1/* tabIndex */, left, top, 0, 0);
		textField.setSize(width, 23);
		textField.setEditable(false); // 読み取り専用
		// var op = _kernel.ListOption.Get("Basic");
		// var editBrowse = (bool)op.GetValue("editBrowse");
		// _textBox.ReadOnly = !(editBrowse);

		// オフセット移動
		left += textField.getWidth() + MARGIN;
      	
//      	//ボタンの配置
//        _button = (Button)Create(Panel, new Button(), tabIndex++, left, top, 60, DefaultH);
//        _button.Text = _kernel.Jp ? "参照" : "Browse";
//        _button.Click += ButtonClick;
//
//        //パネルのサイズ設定
//        Panel.Width = left + _button.Width + Margin;
//        Panel.Height = DefaultH;
        
        //パネルのサイズ設定
		panel.setSize(left + MARGIN, DEFAULT_HEIGHT);
		
        return tabIndex;
	}
}
