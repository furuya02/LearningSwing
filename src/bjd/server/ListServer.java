package bjd.server;

import bjd.util.IDispose;
import bjd.util.ListBase;
import bjd.util.Util;

public final class ListServer extends ListBase<OneServer2> implements IDispose {

	public OneServer2 get(String nameTag) {
		for (OneServer2 oneServer : ar) {
			if (oneServer.getNameTag().equals(nameTag)) {
				return oneServer;
			}
		}
		//TODO DEBUG RemoteServerを検索されたら、とりあえずnullを返しておく
        if (nameTag.equals("RemoteServer")) {
            return null;
		}
		Util.designProblem(String.format("nameTag=%s", nameTag));
		return null;
	}

	@Override
	public void dispose() {
		//Stop();
		super.dispose();
	}

}
