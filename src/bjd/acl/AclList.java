package bjd.acl;

import java.util.ArrayList;

import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.option.Dat;
import bjd.option.OneDat;

public class AclList {
	private static final String OneDat = null;
	private ArrayList<Acl> arV4 = new ArrayList<>();
	private ArrayList<Acl> arV6 = new ArrayList<>();

	private boolean enable;//許可:0 不許可:1
	private Logger logger;

	public AclList(Dat dat,int enableNum,Logger logger) {
		this.enable = (enableNum == 1);
		this.logger = logger;
		for (OneDat o : dat) {
			if (o.isEnable()) {//有効なデータだけを対象にする
				String name = o.getStrList().get(0);
				String ipStr= o.getStrList().get(1);

				if (ipStr == "*") {//全部
					Acl acl = new AclV4(name, ipStr);
					if (!acl.getStatus()) {
						logger.set(LogKind.Error, null, 9000034, String.format("Name:{0} Address{1}", name, ipStr));
					} else {
						arV4.add(acl);
					}
					acl = new AclV6(name, ipStr);
					if (!acl.getStatus()) {
						logger.set(LogKind.Error, null, 9000034, String.format("Name:{0} Address{1}", name, ipStr));
					} else {
						arV6.add(acl);
					}
				} else if (ipStr.indexOf('.') != -1) {//IPv4ルール
					Acl acl = new AclV4(name, ipStr);
					if (!acl.getStatus()) {
						logger.set(LogKind.Error, null, 9000034, String.format("Name:{0} Address{1}", name, ipStr));
					} else {
						arV4.add(acl);
					}
				} else {//IPv6ルール
					Acl acl = new AclV6(name, ipStr);
					if (!acl.getStatus()) {
						logger.set(LogKind.Error, null, 9000034, String.format("Name:{0} Address{1}", name, ipStr));
					} else {
						arV6.add(acl);
					}
				}
			}
		}
	}
	//リストへの追加
	public boolean Append(Ip ip) {
		if(!enable)
			return false;

		if (ip.getInetKind() == InetKind.V4) {
			if (arV4.Any(p => p.IsHit(ip))){
				return false;
			}
			Acl acl = new AclV4(String.format("AutoDeny-{0}", DateTime.Now), ip.toString());
			arV4.add(acl);
		} else {
			if (arV6.Any(p => p.IsHit(ip))){
				return false;
			}
			Acl acl = new AclV6(String.format("AutoDeny-{0}", DateTime.Now), ip.toString());
			arV6.add(acl);
		}
		return true;
	}

	public boolean Check(Ip ip) {

		//ユーザリストの照合
		Acl acl = null;
		if (ip.InetKind == InetKind.V4) {
			for (Acl p : arV4) {
				if (p.isHit(ip)) {
					acl = p;
					break;
				}
			}
		} else {
			for (Acl p : arV6) {
				if (p.isHit(ip)) {
					acl = p;
					break;
				}
			}
		}

		if (!enable && acl==null) {
			logger.set(LogKind.Secure,null,9000017,String.format("address:{0}",ip));//このアドレスからのリクエストは許可されていません
			return false;
		}
		if (enable && acl!=null) {
			logger.set(LogKind.Secure,null,9000018,String.format("user:{0} address:{1}",acl.getName(),ip));//この利用者のアクセスは許可されていません
			return false;
		}
		return true;
	}
}

