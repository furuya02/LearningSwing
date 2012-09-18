package bjd.net;

public final class Mac {

	private byte[] m = new byte[6];

	public Mac(String macStr) {
		for (int i = 0; i < 6; i++) {
			String str = macStr.substring(i * 3, i*3+2);
			m[i] = (byte) Integer.parseInt(str, 0x10);  
			//m[i] = Byte.parseByte(str, 16);
		}
	}

	public Mac(byte[] buf) {
		for (int i = 0; i < 6; i++) {
			m[i] = buf[i];
		}
	}

	public byte[] getBytes() {
		return m;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Mac) {
			byte[] o = ((Mac) obj).getBytes();
			for (int i = 0; i < 6; i++) {
				if (o[i] != m[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return -1; //super.hashCode();
	}

	@Override
	public String toString() {
		return String.format("%02X-%02X-%02X-%02X-%02X-%02X", m[0], m[1], m[2], m[3], m[4], m[5]);
	}
}
