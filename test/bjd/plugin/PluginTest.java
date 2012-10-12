package bjd.plugin;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import bjd.Kernel;

public final class PluginTest {

	@Test
	public void test() {
		Kernel kernel = new Kernel();
		String dir = String.format("%s\\plugins", kernel.getProgDir());

		Plugin plugin = new Plugin(dir);
		assertThat(plugin.length(), is(0));
	}

}
