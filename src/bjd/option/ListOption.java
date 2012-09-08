package bjd.option;

import bjd.Kernel;

public final class ListOption {

	private OptionBasic option;
	private Kernel kernel;

	public ListOption(Kernel kernel) {
		this.kernel = kernel;
		
		option = new OptionBasic(kernel);
		
	}
	
	public OneOption get(String name) {
		return option;
	}

}
