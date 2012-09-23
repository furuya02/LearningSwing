package bjd.net;

public final class OneBind {
	private Ip addr;
	private ProtocolKind protocol;

	public Ip getAddr() {
		return addr;
	}

	public ProtocolKind getProtocol() {
		return protocol;
	}

	public OneBind(Ip addr, ProtocolKind protocol) {
		this.addr = addr;
		this.protocol = protocol;
	}
}
