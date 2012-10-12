package bjd.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import sample.Server;

import bjd.option.OneOption;
import bjd.server.OneServer;
import bjd.util.FileSearch;

public class Plugin {

	private ArrayList<String> ar = new ArrayList<>();

	private String[] getClassNameList(File file) {
		ArrayList<String> ar = new ArrayList<>();
		try {
			//jarファイル内のファイルを列挙
			JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
			JarEntry entry;
			while ((entry = jarIn.getNextJarEntry()) != null) {
				if (!entry.isDirectory()) { //ディレクトリは対象外
					String name = entry.getName();
					//　sample/Server.class

					//　sample/Server　　.classを外す
					int index = name.indexOf(".class");
					if (index != -1) {
						name = name.substring(0, index);
						//　sample.Server  /を.に置き換える
						ar.add(name.replace('/', '.'));

					}
				}
			}
			return (String[]) ar.toArray(new String[0]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new String[0];
	}

	public Plugin(String dir) {
		FileSearch fileSearch = new FileSearch(dir);
		for (File file : fileSearch.listFiles("*Server.jar")) {
			try {
				JarFile jar = new JarFile(file);
				Manifest mf = jar.getManifest();
				Attributes att = mf.getMainAttributes();
				String cname = "sample.Server";att.getValue("Plugin-Class");
				URL url = file.getCanonicalFile().toURI().toURL();
				URLClassLoader loader = new URLClassLoader(
						new URL[] { url });
				Class cobj = loader.loadClass(cname);
				Class[] ifnames = cobj.getInterfaces();
				//for (int j = 0; j < ifnames.length; j++) {
				//	if (ifnames[j] == SamplePluginAppPlugin.class) {
				//		System.out.println("load..... " + cname);
				//		SamplePluginAppPlugin plugin =
				//				(SamplePluginAppPlugin)cobj.newInstance();
				//		plugins.add(plugin);
				//		break;
				//	}
				OneServer oneServer = (OneServer)cobj.newInstance();  
				//Object newInstance = cobj.newInstance();
				//OneServer oneServer = (OneServer)cobj.newInstance();
				int x=0;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}			
			/*ar.add(file.getPath());
			String[] classNameList = getClassNameList(file);
			for (String className : classNameList) {
				OneServer oneServer = null;
				OneOption oneOption = null;
				
                //JarFile jar = new JarFile(file);
                //Manifest mf = jar.getManifest();
                //Attributes att = mf.getMainAttributes();
                //String cname = att.getValue("Plugin-Class");
				try {
					URL url = file.getCanonicalFile().toURI().toURL();
					URLClassLoader loader = new URLClassLoader( new URL[] { url });
					Class cobj = loader.loadClass("sample.Server");
					
					Server a = (Server) cobj.newInstance();

					int x=0;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}*/
	}

	public int length() {
		return ar.size();
	}

}
