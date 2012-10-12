package bjd.plugin;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import bjd.Kernel;

public class PluginTest {

	@Test
	public void test() {
		Kernel kernel = new Kernel();
		String dir = String.format("%s\\plugins", kernel.getProgDir());

		Plugin plugin = new Plugin(dir);
		assertThat(plugin.length(), is(0));
	}

}
