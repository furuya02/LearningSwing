package bjd.acl;

import bjd.net.Ip;

public final class AclV4 extends Acl {
	//指定の要領
	//192.168.0.1
	//192.168.0.1-200
	//192.168.0.1-192.168.10.254
	//192.168.10.254-192.168.0.1（開始と終了が逆転してもＯＫ）
	//192.168.0.1/24
	//192.168.*.* 
	//*.*.*,*
	//*

	//****************************************************************
	//オーバーライド
	//****************************************************************
	@Override
	public boolean isHit(Ip ip) {
		if (ip.getAddrV4() < start.getAddrV4()) {
			return false;
		}
		if (end.getAddrV4() < ip.getAddrV4()) {
			return false;
		}
		return true;
	}

	//コンストラクタ
	public AclV4(String name, String ipStr) {
		super(name);

		//「*」によるALL指定
		if (ipStr.equals("*") || ipStr.equals("*.*.*.*")) {
			start = new Ip("0.0.0.0");
			end = new Ip("255.255.255.255");
			status = true;
			return; //初期化成功
		}

		//「*」表現を正規化する
		String[] tmp = ipStr.split("\\.");
		if (tmp.length == 4) {
			if (tmp[1] == "*" && tmp[2] == "*" && tmp[3] == "*") { //192.*.*.*
				ipStr = String.format("{0}.0.0.0/8", tmp[0]);
			} else if (tmp[2] == "*" && tmp[3] == "*") { //192.168.*.*
				ipStr = String.format("{0}.{1}.0.0/16", tmp[0], tmp[1]);
			} else if (tmp[3] == "*") { //192.168.0.*
				ipStr = String.format("{0}.{1}.{2}.0/24", tmp[0], tmp[1], tmp[2]);
			}
		}

		if (ipStr.indexOf('-') != -1) {
			//************************************************************
			// 「-」による範囲指定
			//************************************************************
			tmp = ipStr.split("-");
			if (tmp.length != 2) {
				return; //初期化失敗
			}
			start = new Ip(tmp[0]);
			String strTo = tmp[1];
			//to（終了アドレス）が192.168.2.254のように４オクテットで表現されているかどうかの確認
			tmp = strTo.split("\\.");
			if (tmp.length == 4) { //192.168.0.100
				end = new Ip(strTo);
			} else if (tmp.length == 1) { //100
				try {
					int n = Integer.valueOf(strTo);
					//if(n < 0 || 255 < n)
					//    return;//初期化失敗
					strTo = String.format("%d.%d.%d.%d", start.getIpV4()[0], start.getIpV4()[1], start.getIpV4()[2], n);
					end = new Ip(strTo);
				} catch (Exception ex) {
					return; //初期化失敗
				}
			} else {
				return; //初期化失敗
			}

			//開始アドレスが終了アドレスより大きい場合、入れ替える
			if (start.getAddrV4() > end.getAddrV4()) {
				Ip ip = start;
				start = end;
				end = ip;
			}
		} else if (ipStr.indexOf('/') != -1) {
			//************************************************************
			// 「/」によるマスク指定
			//************************************************************
			tmp = ipStr.split("/");
			if (tmp.length != 2) {
				return; //初期化失敗
			}
			String strIp = tmp[0];
			String strMask = tmp[1];

			//TODO uint から intにした場合の影響はまだ未確認
			//uint mask = 0;
			//uint xor;
			int mask = 0;
			int xor;
			try {
				int m = Integer.valueOf(strMask);
				for (int i = 0; i < 32; i++) {
					if (i != 0) {
						mask = mask << 1;
					}
					if (i < m) {
						mask = (mask | 1);
					}
				}
				xor = (0xffffffff ^ mask);
			} catch (Exception ex) {
				return; //初期化失敗
			}
			Ip ip = new Ip(strIp);
			if (!ip.getStatus()) {
				return; //初期化失敗
			}
			start = new Ip(ip.getAddrV4() & mask);
			end = new Ip(ip.getAddrV4() | xor);
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
		if (start.getAddrV4() != 0 || end.getAddrV4() != 0) {
			if (start.getAddrV4() <= end.getAddrV4()) {
				status = true; //初期化成功
			}
		}
	}
}
