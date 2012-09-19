package bjd.acl;

import java.math.BigInteger;

import bjd.net.Ip;

public final class AclV6 extends Acl {

	//****************************************************************
	//オーバーライド
	//****************************************************************
	@Override
	public boolean isHit(Ip ip) {
		
		BigInteger bigIp = new BigInteger(ip.getIpV6());
		BigInteger bigStart = new BigInteger(start.getIpV6());
		BigInteger bigEnd = new BigInteger(end.getIpV6());

		if (bigIp.compareTo(bigStart) < 0) {
			return false;
		}
		if (bigEnd.compareTo(bigIp) < 0) {
			return false;
		}

		
		
//		if (ip.getAddrV6H() < start.getAddrV6H()) {
//			return false;
//		}
//		if (ip.getAddrV6H() == start.getAddrV6H()) {
//			if (ip.getAddrV6L() < start.getAddrV6L()) {
//				return false;
//			}
//		}
//		if (end.getAddrV6H() < ip.getAddrV6H()) {
//			return false;
//		}
//		if (end.getAddrV6H() == ip.getAddrV6H()) {
//			if (end.getAddrV6L() < ip.getAddrV6L()) {
//				return false;
//			}
//		}
		return true;
	}

	//コンストラクタ
	public AclV6(String name, String ipStr) {
		super(name);

		//「*」によるALL指定
		if (ipStr.equals("*") || ipStr.equals("*:*:*:*:*:*:*:*")) {
			start = new Ip("::0");
			end = new Ip("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
			status = true;
			return; //初期化成功
		}

		String[] tmp;
		if (ipStr.indexOf('-') != -1) {
			//************************************************************
			// 「-」による範囲指定
			//************************************************************
			tmp = ipStr.split("-");
			if (tmp.length != 2) {
				return; //初期化失敗
			}

			start = new Ip(tmp[0]);
			end = new Ip(tmp[1]);

			//開始アドレスが終了アドレスより大きい場合、入れ替える
			if (start.getAddrV6H() == end.getAddrV6H()) {
				if (start.getAddrV6L() > end.getAddrV6L()) {
					//入れ替え
					Ip ip = start;
					start = end;
					end = ip;
				}
			} else {
				if (start.getAddrV6H() > end.getAddrV6H()) {
					//入れ替え
					Ip ip = start;
					start = end;
					end = ip;
				}
			}

		} else if (ipStr.indexOf("/") != -1) {
			//************************************************************
			// 「/」によるマスク指定
			//************************************************************
			tmp = ipStr.split("/");
			if (tmp.length != 2) {
				return; //初期化失敗
			}

			String strIp = tmp[0];
			String strMask = tmp[1];

			//DOTO 動作未確認 YInt64->Long
//			UInt64 maskH = 0;
//			UInt64 maskL = 0;
//			UInt64 xorH;
//			UInt64 xorL;
			long maskH = 0;
			long maskL = 0;
			long xorH;
			long xorL;
			try {
				long m = Long.valueOf(strMask);
				for (long i = 0; i < 64; i++) {
					if (i != 0) {
						maskH = maskH << 1;
					}
					if (i < m) {
						maskH = (maskH | 1);
					}
				}
				xorH = (0xffffffffffffffffL ^ maskH);

				for (long i = 64; i < 128; i++) {
					if (i != 0) {
						maskL = maskL << 1;
					}
					if (i < m) {
						maskL = (maskL | 1);
					}
				}
				xorL = (0xffffffffffffffffL ^ maskL);
			} catch (Exception ex) {
				return; //初期化失敗
			}
			Ip ip = new Ip(strIp);
			if (!ip.getStatus()) {
				return; //初期化失敗
			}
			start = new Ip(ip.getAddrV6H() & maskH, ip.getAddrV6L() & maskL);
			end = new Ip(ip.getAddrV6H() | xorH, ip.getAddrV6L() | xorL);
		} else {
			//************************************************************
			// 通常指定
			//************************************************************
			start = new Ip(ipStr);
			end = new Ip(ipStr);
		}
		if (!start.getStatus()) {
			return; //初期化失敗
		}
		if (!end.getStatus()) {
			return; //初期化失敗
		}

		//最終チェック
		status = true; //初期化成功
	}
}
