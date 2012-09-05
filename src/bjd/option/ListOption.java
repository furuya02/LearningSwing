package bjd.option;

import bjd.Kernel;

public class ListOption {

	Option option;
	Kernel kernel;

	public ListOption(Kernel kernel) {
		this.kernel = kernel;
		
		option = new Option(kernel);
		
	}
	
	public OneOption get(String name) {
		return option;
	}

}
