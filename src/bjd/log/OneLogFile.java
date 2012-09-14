package bjd.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bjd.util.IDispose;

final class OneLogFile implements IDispose {

    private FileWriter fw;

    public OneLogFile(String fileName) {
        try {
            File file = new File(fileName);
            fw = new FileWriter(file, true); // 追加モード
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String str) {
        try {
            fw.write(str);
            fw.write("\r\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

