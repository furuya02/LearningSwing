package bjd.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;


public final class Ip {

	private boolean status = false;

	private InetKind inetKind;
	private boolean any;
	private byte[] ipV4;
	private byte[] ipV6;
	private int scopeId;

	public int getScopeId() {
		return scopeId;
	}

	public byte[] getIpV4() {
		return ipV4;
	}

	public byte[] getIpV6() {
		return ipV6;
	}

	public boolean isStatus() {
		return status;
	}

	public boolean isAny() {
		return any;
	}

	public InetKind getInetKind() {
		return inetKind;
	}

	// デフォルト値の初期化
	void init(InetKind inetKind) {
		this.inetKind = inetKind;
		ipV4 = new byte[] { 0, 0, 0, 0 };
		ipV6 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		any = false;
		scopeId = 0;
	}

	// コンストラクタ(隠蔽)
	private Ip() {

	}

	// コンストラクタ
	public Ip(String ipStr) {
		init(InetKind.V4);
		
		if (ipStr == null) {
			throwException(ipStr); //例外終了
		}
		
		if (ipStr == "INADDR_ANY") { // IPV4
			any = true;
		} else if (ipStr == "IN6ADDR_ANY_INIT") { // IPV6
			init(InetKind.V6);
			any = true;
		} else if (ipStr.indexOf('.') > 0) { // IPV4
			// 名前で指定された場合は、例外に頼らずここで処理する（高速化）
			for (int i = 0; i < ipStr.length(); i++) {
				char c = ipStr.charAt(i);
				if (c != '.' && (c < '0' || '9' < c)) {
					throwException(ipStr); //例外終了
				}
			}
			String[] tmp = ipStr.split("\\.");
			try {
				// length==3 ネットアドレスでnewされた場合
				if (tmp.length == 4 || tmp.length == 3) {
					for (int i = 0; i < tmp.length; i++) {
						int n = Integer.valueOf(tmp[i]);
						if (n < 0 || 255 < n) {
							init(inetKind); // デフォルト値での初期化
							throwException(ipStr); //例外終了
						}
						ipV4[i] = (byte) n;
					}
				} else {
					throwException(ipStr); //例外終了
				}
			} catch (Exception ex) {
				throwException(ipStr); //例外終了
			}
		} else if (ipStr.indexOf(":") >= 0) { // IPV6
			init(InetKind.V6);

			// Ver5.1.2 もし、[xxxx:xxxx:xxxx:xxxx:xxxx:xxxx]で囲まれている場合は、除去する
			// String[] tmp = ipStr.split(new[] { '[',']' });
			String[] tmp = ipStr.split("\\[|\\]");
			if (tmp.length == 2) {
				ipStr = tmp[1];
			}
			// Ver5.4.9 %の付いたV6アドレスに対応
			int index = ipStr.indexOf('%');
			if (index >= 0) {
				try {
					// scopeId = Int32.Parse(ipStr.substring(index + 1));
					scopeId = Integer.valueOf(ipStr.substring(index + 1));
				} catch (Exception ex) {
					scopeId = 0;
				}
				ipStr = ipStr.substring(0, index);
			}
			tmp = ipStr.split(":");

			int n = ipStr.indexOf("::");
			if (0 <= n) {
				StringBuilder sb = new StringBuilder();
				sb.append(ipStr.substring(0, n));
				for (int i = tmp.length; i < 8; i++) {
					sb.append(":");
				}
				sb.append(ipStr.substring(n));
				tmp = sb.toString().split(":", -1);
				if (tmp.length != 8) {
					String[] m = new String[] { "", "", "", "", "", "", "", "" };
					for (int i = 0; i < 8; i++) {
						m[i] = tmp[i];
					}
					tmp = m;
				}
			}
			if (tmp.length != 8) {
				throwException(ipStr); //例外終了
			}
			for (int i = 0; i < 8; i++) {
				if(tmp[i].length()>4){
					throwException(ipStr); //例外終了
				}
				
				if (tmp[i].equals("")) {
					ipV6[i * 2] = 0;
					ipV6[i * 2 + 1] = 0;
				} else {
					//short u = Short.valueOf(tmp[i], 16);
					int u = Integer.valueOf(tmp[i], 16);
					// UInt16 u = Convert.ToUInt16(tmp[i], 16);
					byte[] b = ByteBuffer.allocate(2).putShort((short) u).array();
					// byte[] b = BitConverter.GetBytes(u);
					ipV6[i * 2] = b[0];
					ipV6[i * 2 + 1] = b[1];
				}
			}
		} else {
			throwException(ipStr); //例外終了
		}
		status = true; // 初期化成功
	}

	//データを初期化し、例外を発生させる
	private void throwException(String ipStr) {
		init(inetKind); // デフォルト値での初期化
		throw new IllegalArgumentException(String.format("引数が不正です \"%s\"", ipStr));
	}

	// ホストバイトオーダのデータで初期化する
	// public Ip(uint ip) {
	public Ip(int ip) {
		init(InetKind.V4); // デフォルト値での初期化

		byte[] tmp = ByteBuffer.allocate(4).putInt(ip).array();
		// byte[] tmp = BitConverter.GetBytes(ip);

		for (int i = 0; i < 4; i++) {
			ipV4[i] = tmp[3 - i];
		}
		if (isAllZero(ipV4)) {
			any = true;
		}
		status = true; // 初期化成功

		ByteBuffer bb = ByteBuffer.wrap(tmp);
		bb.array();

	}

	// ホストバイトオーダのデータで初期化する
	// public Ip(UInt64 h, UInt64 l) {
	public Ip(long h, long l) {

		init(InetKind.V6); // デフォルト値での初期化
		byte[] b = ByteBuffer.allocate(8).putLong(h).array();
		// byte [] b = BitConverter.GetBytes(h);
		for (int i = 0; i < 8; i++) {
			ipV6[7 - i] = b[i];
		}
		b = ByteBuffer.allocate(8).putLong(l).array();
		// b = BitConverter.GetBytes(l);
		for (int i = 0; i < 8; i++) {
			ipV6[15 - i] = b[i];
		}
		status = true; // 初期化成功
	}

	@Override
	public boolean equals(Object o) {
		// 非NULL及び型の確認
		if (o == null || !(o instanceof Ip)) {
			return false;
		}
		Ip ip = (Ip) o;
		if (ip.getInetKind() == inetKind) {
			if (inetKind == InetKind.V4) {
				byte[] b = ip.getIpV4();
				for (int i = 0; i < 4; i++) {
					if (ipV4[i] != b[i]) {
						return false;
					}
				}
			} else {
				if (ip.getScopeId() != scopeId) {
					return false;
				}
				byte[] b = ip.getIpV6();
				for (int i = 0; i < 16; i++) {
					if (ipV6[i] != b[i]) {
						return false;
					}
				}
			}
		}
		return true;
		
	}

	@Override
	public String toString() {
		if (inetKind == InetKind.V4) {
			if (any) {
				return "INADDR_ANY";
			}
			return String.format("%d.%d.%d.%d", (ipV4[0] & 0xff), (ipV4[1] & 0xff), (ipV4[2] & 0xff), (ipV4[3] & 0xff));
		}
		if (any) {
			return "IN6ADDR_ANY_INIT";
		}

		if (isAllZero(ipV6)) {
			return "::0";
		}

		StringBuilder sb = new StringBuilder();
		int flg = 0; // 1回だけ省略表記を使用する 0:未使用 1:使用中 2:使用済
		for (int i = 0; i < 8; i++) {
			//short h = ByteBuffer.allocate(4).put(ipV6[i * 2]).getShort();
			//short l = ByteBuffer.allocate(4).put(ipV6[i * 2 + 1]).getShort();
			//short u = (short) ((h << 8) | l);
			ByteBuffer b = ByteBuffer.allocate(2).put(ipV6, i * 2, 2);
			b.position(0);
			short u = b.getShort();
			//int u = ByteBuffer.allocate(4).put(ipV6, i*2,2).getInt();

			// var h = Convert.ToUInt16(IpV6[i * 2]);
			// var l = Convert.ToUInt16(IpV6[i * 2 + 1]);
			// var u = (UInt16) ((h << 8) | l);
			if (u == 0) {
				if (flg == 0) { // 未使用の場合
					flg = 1; // 使用中に設定する
					sb.append(":");
				} else if (flg == 1) { // 使用中の場合
					// 処理なし
				} else { // 使用済の場合、0を表記する
					sb.append(String.format(":%x", u));
					// sb.AppendFormat(":{0:x}", u);
				}
			} else {
				if (flg == 1) { // 使用中の場合は
					flg = 2; // 使用済に設定する
				}
				if (i == 0) {
					sb.append(String.format("%x", u));
				} else {
					sb.append(String.format(":%x", u));
				}
				// sb.AppendFormat(i == 0 ? "{0:x}" : ":{0:x}", u);
			}
		}
		if (flg == 1) { // 使用中で終了した場合は:を足す
	sb.append(":");
		}
		// Ver5.4.9
		if (scopeId != 0) {
			sb.append(String.format("%%%d", scopeId));
			// sb.AppendFormat("%{0}", scopeId);
		}
		return sb.toString();
	}

	// ネットワークバイトオーダ
	public byte[] netBytes() {
		if (inetKind == InetKind.V4) {
			return ipV4;
		}
		return ipV6;
		// return inetKind == InetKind.V4 ? ipV4.ToArray() : ipV6.ToArray();
	}

	// ホストバイトオーダ
	public int getAddrV4() {
		if (inetKind == InetKind.V4) {
			byte[] tmp = new byte[4];
			tmp[3] = ipV4[0];
			tmp[2] = ipV4[1];
			tmp[1] = ipV4[2];
			tmp[0] = ipV4[3];
			return ByteBuffer.wrap(tmp).getInt();
			// return BitConverter.ToUInt32(tmp,0);
		}
		return 0;
	}

	// ホストバイトオーダ
	public long getAddrV6H() {
		if (inetKind == InetKind.V6) {
			byte[] tmp = new byte[8];
			tmp[7] = ipV6[0];
			tmp[6] = ipV6[1];
			tmp[5] = ipV6[2];
			tmp[4] = ipV6[3];
			tmp[3] = ipV6[4];
			tmp[2] = ipV6[5];
			tmp[1] = ipV6[6];
			tmp[0] = ipV6[7];
			return ByteBuffer.wrap(tmp).getLong();
			// return BitConverter.ToUInt64(tmp, 0);
		}
		return 0;
	}

	// ホストバイトオーダ
	public long getAddrV6L() {
		if (inetKind == InetKind.V6) {
			byte[] tmp = new byte[8];
			tmp[7] = ipV6[8];
			tmp[6] = ipV6[9];
			tmp[5] = ipV6[10];
			tmp[4] = ipV6[11];
			tmp[3] = ipV6[12];
			tmp[2] = ipV6[13];
			tmp[1] = ipV6[14];
			tmp[0] = ipV6[15];
			return ByteBuffer.wrap(tmp).getLong();
			// return BitConverter.ToUInt64(tmp, 0);
		}
		return 0;
	}

	public InetAddress getInetAddress() {
		// public IPAddress getIPAddress() {
		try {
			if (any) {
				if (inetKind == InetKind.V4) {
					// return Inet4Address.getByAddress(new byte[] { (byte)
					// 0xff,
					// (byte) 0xff, (byte) 0xff, (byte) 0xff });
					return Inet4Address.getByName("0.0.0.0");
				} else {
					return Inet6Address.getByName("0::0");
				}
				// return (inetKind == InetKind.V4) ? IPAddress.Any :
				// IPAddress.IPv6Any;
			}
			if (inetKind == InetKind.V4) {
				return Inet4Address.getByAddress(netBytes());
			}
			return Inet6Address.getByAddress("", netBytes(), scopeId);
			// IPAddress ipaddress = new IPAddress(netBytes());
			// if (scopeId != 0) {
			// ipaddress.ScopeId = scopeId;
			// }
			// return inetAddress;
		} catch (Exception ex) {
			return null;
		}
	}

	private boolean isAllZero(byte[] buf) {
		for (byte b : buf) {
			if (b != 0) {
				return false;
			}
		}
		return true;
	}
}
