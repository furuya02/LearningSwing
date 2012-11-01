package bjd.plugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.Kernel;

public final class PluginTest {

	@Test
	public void test() {
		Kernel kernel = new Kernel();
		String dir = String.format("%s\\plugins", "C:\\tmp2\\out");
		//TODO Debug Print
		System.out.println(String.format("dir=%s", dir));
		Plugin plugin = new Plugin(dir);
		assertThat(plugin.length(), is(1));
	}

}
