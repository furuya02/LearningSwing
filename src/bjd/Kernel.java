package bjd;

import bjd.option.ListOption;

public final class Kernel {
	private RunMode runMode = RunMode.Normal;
	private boolean jp = true;

	private ListOption listOption;

	public ListOption getListOption() {
		return listOption;
	}

	public boolean getJp() {
		return jp;
	}

	public RunMode getRunMode() {
		return runMode;
	}

	public Kernel() {
		listOption = new ListOption(this);
	}

}
