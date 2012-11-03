package bjd.plugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import bjd.Kernel;

public final class PluginTest {

	@Test
	public void test() {
		Kernel kernel = new Kernel();
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		
		String dir = String.format("%s\\bin\\plugins", currentDir);
		//TODO Debug Print
		System.out.println(String.format("dir=%s", dir));
		Plugin plugin = new Plugin(kernel, dir);
		//TODO Debug Print
		System.out.println(String.format("plugin.length(=%d", plugin.length()));
		assertThat(plugin.length(), is(2));
	}

}
