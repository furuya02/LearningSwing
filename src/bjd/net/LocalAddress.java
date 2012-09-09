package bjd.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public final class LocalAddress {

	private ArrayList<Ip> v4 = new ArrayList<Ip>();
	private ArrayList<Ip> v6 = new ArrayList<Ip>();

	public ArrayList<Ip> getV4() {
		return v4;
	}

	public ArrayList<Ip> getV6() {
		return v6;
	}

	//インターフェースの状態からオブジェクトを生成する
	public LocalAddress() {
		v4.add(new Ip("INADDR_ANY"));
		v6.add(new Ip("IN6ADDR_ANY_INIT"));

		Enumeration<NetworkInterface> interfaceList;
		try {
			interfaceList = NetworkInterface.getNetworkInterfaces();
			if (interfaceList != null) {
				while (interfaceList.hasMoreElements()) {
					NetworkInterface iface = interfaceList.nextElement();
					Enumeration<InetAddress> addrList = iface.getInetAddresses();
					if (!addrList.hasMoreElements()) {
						continue;
					}
					while (addrList.hasMoreElements()) {
						InetAddress inetAddress = addrList.nextElement();
	
						if (inetAddress instanceof Inet4Address) {
							String s = inetAddress.getHostAddress();
							v4.add(new Ip(s));
						} else if (inetAddress instanceof Inet6Address) {
	                        boolean linkLocal = inetAddress.isLinkLocalAddress();
	                        boolean multicast = inetAddress.isMulticastAddress();
	                        boolean siteLocal = inetAddress.isSiteLocalAddress();
							if (!linkLocal && !multicast && !siteLocal) {
								String s = inetAddress.getHostAddress();
								v6.add(new Ip(s));
	                        }
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	//Remoteでら取得した文字列からオブジェクトを生成する
	public LocalAddress(String str) {
		String[] tmp = str.split("\t");

		for (String s : tmp[0].split("\b", 0)) {
			v4.add(new Ip(s));
		}
		for (String s : tmp[1].split("\b", 0)) {
			v6.add(new Ip(s));
		}
	}

	//Remoteへの送信文字列
	public String remoteStr() {
		StringBuilder sb = new StringBuilder();
		for (Ip ip : v4) {
			sb.append(ip + "\b");
		}
		sb.append("\t");
		for (Ip ip : v6) {
			sb.append(ip + "\b");
		}
		return sb.toString();
	}
}
