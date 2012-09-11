package bjd.server;

import bjd.util.IDispose;
import bjd.util.ListBase;
import bjd.util.Util;

public final class ListServer extends ListBase<OneServer> implements IDispose {

	public OneServer get(String nameTag) {
		for (OneServer oneServer : ar) {
			if (oneServer.getNameTag().equals(nameTag)) {
				return oneServer;
			}
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
