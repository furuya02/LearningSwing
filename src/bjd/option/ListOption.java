package bjd.option;

import bjd.Kernel;
import bjd.util.ListBase;

public final class ListOption extends ListBase {

	private OptionBasic option;
	private Kernel kernel;

	public ListOption(Kernel kernel) {
		this.kernel = kernel;

		String path = "";
		String nameTag = "OptionBasic";
		option = new OptionBasic(kernel, path, nameTag);

	}

	public OneOption get(String name) {
		return option;
	}

}
