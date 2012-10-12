package bjd.util;

import java.io.File;
import java.util.ArrayList;

public final class FileSearch {
	
	private String path;
	private ArrayList<File> ar;

	public FileSearch(String path) {
		this.path = path;
	}

	public File[] listFiles(String pattern) {
		ar = new ArrayList<>();
		if (pattern != null) {
			pattern = pattern.replace(".", "\\.");
			pattern = pattern.replace("*", ".*");
			pattern = pattern.replace("?", ".");
		}
		return func(path, pattern);
	}

	private File[] func(String path, String pattern) {
		for (File file : (new File(path)).listFiles()) {
			if (pattern != null && !file.getName().matches(pattern)) {
				continue;
			}
			ar.add(file);
			if (file.isDirectory()) { //再帰処理
				func(file.getAbsolutePath(), pattern);
			}
		}
		return (File[]) ar.toArray(new File[ar.size()]);
	}
}
