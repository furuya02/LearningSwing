package bjd.plugins.web;

import bjd.Kernel;
import bjd.ctrl.OneCtrl;
import bjd.option.OneOption;

public final class Option extends OneOption {

	@Override
	public String getJpMenu() {
		return "Webサーバ";
	}

	@Override
	public String getEnMenu() {
		return "Web Server";
	}

	@Override
	public char getMnemonic() {
		return 'W';
	}

	public Option(Kernel kernel, String path) {
		super(kernel.isJp(), path, "Web");
		
	}

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
		//boolean b = (boolean) getCtrl("useServer").read();
		//getCtrl("Basic").setEnable(b);
	}
}
