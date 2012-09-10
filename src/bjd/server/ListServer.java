package bjd.server;

import bjd.util.IDispose;
import bjd.util.ListBase;

public final class ListServer extends ListBase<OneServer> implements IDispose {

    public OneServer get(String nameTag) {
        for (OneServer oneServer : ar) {
            if (oneServer.getNameTag().equals(nameTag)) {
                return oneServer;
            }
        }
        throw new UnsupportedOperationException("ListServer.java get() 設計に問題があります nameTag=" + nameTag);
    }
    
    @Override
    public void dispose() {
        //Stop();
        super.dispose();
    }

}
