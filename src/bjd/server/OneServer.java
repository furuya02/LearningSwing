package bjd.server;

import bjd.log.OneLog;
import bjd.util.IDispose;

public final class OneServer implements IDispose {
    private String nameTag;

    public String getNameTag() {
        return nameTag;
    }

    public OneServer(String nameTag) {
        this.nameTag = nameTag;
    }

    @Override
    public void dispose() {
        // TODO 自動生成されたメソッド・スタブ

    }

    public void append(OneLog oneLog) {
        // TODO 自動生成されたメソッド・スタブ
        
    }

}
