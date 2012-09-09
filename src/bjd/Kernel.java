package bjd;

import bjd.net.LocalAddress;
import bjd.option.ListOption;

public final class Kernel {
	private RunMode runMode = RunMode.Normal;
	private boolean jp = true;
	private LocalAddress localAddress;
	private ListOption listOption;

	public LocalAddress getLocalAddress() {
		return localAddress;
	}
	
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
		localAddress = new LocalAddress();

		listOption = new ListOption(this);
	}

}
