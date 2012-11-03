package bjd.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import bjd.Kernel;
import bjd.net.BindAddr;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneOption;
import bjd.plugins.sample.Option;
import bjd.server.OneServer;
import bjd.util.FileSearch;

public final class Plugin {

	private ArrayList<String> ar = new ArrayList<>();

	private String[] getClassNameList(File file) {

		//パッケージ名の生成
		//sample.jar
		String packageName = file.getName();
		//sample
		packageName = packageName.substring(0, packageName.length() - 4);
		//bjd.plubgins.sample
		packageName = String.format("bjd.plugins.%s", packageName);

		ArrayList<String> ar = new ArrayList<>();
		try {
			//jarファイル内のファイルを列挙
			JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
			JarEntry entry;
			while ((entry = jarIn.getNextJarEntry()) != null) {
				if (!entry.isDirectory()) { //ディレクトリは対象外
					//　Server.class
					String className = entry.getName();
					//　Server　　.classを外す
					int index = className.indexOf(".class");
					if (index != -1) {
						className = className.substring(0, index);
					}
					//package.Server
					ar.add(String.format("%s.%s", packageName, className));
				}
			}
			return (String[]) ar.toArray(new String[0]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new String[0];
	}

	private OneOption createOption(File file, String className, Kernel kernel) {
		try {
			URL url = file.getCanonicalFile().toURI().toURL();
			URLClassLoader loader = new URLClassLoader(new URL[] { url });
			Class cobj = loader.loadClass(className);

			Constructor constructor = cobj.getConstructor(new Class[] { Kernel.class, String.class });
			return (OneOption) constructor.newInstance(new Object[] { kernel, file.getPath() });
			//				} catch (MalformedURLException e) {
			//					e.printStackTrace();
			//				} catch (IOException e) {
			//					e.printStackTrace();
			//				} catch (ClassNotFoundException e) {
			//					e.printStackTrace();
			//				} catch (InstantiationException e) {
			//					e.printStackTrace();
			//				} catch (IllegalAccessException e) {
			//					e.printStackTrace();
		} catch (Exception e) {
			//何の例外が発生しても、プラグインとしては受け付けない
			return null;
		}
	}

	private OneServer createServer(File file, String className, Kernel kernel, Conf conf, OneBind oneBind) {
		try {
			URL url = file.getCanonicalFile().toURI().toURL();
			URLClassLoader loader = new URLClassLoader(new URL[] { url });
			Class cobj = loader.loadClass(className);

			Constructor constructor = cobj.getConstructor(new Class[] { Kernel.class, Conf.class, OneBind.class });
			return (OneServer) constructor.newInstance(new Object[] { kernel, conf, oneBind });
			//				} catch (MalformedURLException e) {
			//					e.printStackTrace();
			//				} catch (IOException e) {
			//					e.printStackTrace();
			//				} catch (ClassNotFoundException e) {
			//					e.printStackTrace();
			//				} catch (InstantiationException e) {
			//					e.printStackTrace();
			//				} catch (IllegalAccessException e) {
			//					e.printStackTrace();
		} catch (Exception e) {
			//何の例外が発生しても、プラグインとしては受け付けない
			return null;
		}
	}

	public Plugin(Kernel kernel, String dir) {
		FileSearch fileSearch = new FileSearch(dir);
		//pluginsの中のjarファイルの検索
		for (File file : fileSearch.listFiles("*.jar")) {
			ar.add(file.getPath());

			String[] classNameList = getClassNameList(file);

			OneServer oneServer = null;
			OneOption oneOption = null;

			for (String className : classNameList) {
				if (className.indexOf("Option") != -1) {
					oneOption = createOption(file, className, kernel);
				}
			}
			if (oneOption != null) {

				Conf conf = new Conf(oneOption);
//				int protocolKind = (int)conf.get("protocolKind");
//				BindAddr bindAddr = (BindAddr)conf.get("bindAddress2");
//				boolean useServer = (boolean)conf.get("useServer");
				//OneBind oneBind = new OneBind(addr, protocol);

				for (String className : classNameList) {
					if (className.indexOf("Server") != -1) {
						oneServer = createServer(file, className, kernel, conf, null);
					}
				}
			}
			if (oneServer != null) {

			}

		}
	}

	public int length() {
		return ar.size();
	}

}
