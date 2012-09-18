package bjd.acl;

import bjd.net.Ip;

public abstract class Acl {
    protected boolean status;// { get; protected set; }
    protected String name; // { get; protected set; }
    protected Ip start; //  { get; protected set; }
    protected Ip end; // { get; protected set; }
    public abstract boolean isHit(Ip ip);

    protected Acl(String name) {
        this.name = name;
        this.status = false;
    }

	public final boolean getStatus() {
		return status;
	}

	public final String getName() {
		return name;
	}

	public final Ip getStart() {
		return start;
	}

	public final Ip getEnd() {
		return end;
	}
}
