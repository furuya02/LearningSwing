package bjd.net;

public class BindAddr {
    private Ip ipV4;
	private Ip ipV6;
	private BindStyle bindStyle;

	public Ip getIpV4() {
		return ipV4;
	}

    public Ip getIpV6() {
		return ipV6;
	}

    public BindStyle getBindStyle() {
		return bindStyle;
	}
    
    //デフォルト値の初期化
    private void init() {
        bindStyle = BindStyle.V4ONLY;
        ipV4 = new Ip("INADDR_ANY");
        ipV6 = new Ip("IN6ADDR_ANY_INIT");
    }
    
    //コンストラクタ
	public BindAddr() {
		init(); // デフォルト値での初期化
    }

    //コンストラクタ
	public BindAddr(BindStyle bindStyle, Ip ipV4, Ip ipV6) {
        this.bindStyle = bindStyle;
        this.ipV4 = ipV4;
        this.ipV6 = ipV6;
    }

	//コンストラクタ
    public BindAddr(String str) {
		try {
			String[] tmp = str.split(",");
			if (tmp.length == 3) {

				if (tmp[0].equals("V4_ONLY") || tmp[0].equals("V4Only")) {
					tmp[0] = "V4ONLY";
				} else if (tmp[0].equals("V6_ONLY") || tmp[0].equals("V6Only")) {
					tmp[0] = "V6ONLY";
				} else if (tmp[0].equals("V46_DUAL") || tmp[0].equals("V46Dual")) {
					tmp[0] = "V46DUAL";
				}
				bindStyle = BindStyle.valueOf(tmp[0]);
				ipV4 = new Ip(tmp[1]);
				ipV6 = new Ip(tmp[2]);
				if (ipV4.getInetKind() != InetKind.V4) {
					throw new IllegalArgumentException("ipV4!=IPV4");
				}
				if (ipV6.getInetKind() != InetKind.V6) {
					throw new IllegalArgumentException("ipV6!=IPV6");
				}
			} else {
				init(); // デフォルト値での初期化
				throw new IllegalArgumentException(str);
			}
		} catch (Exception ex) {
			init(); // デフォルト値での初期化
			throw new IllegalArgumentException(ex);
		}
		
    }
    
    @Override
    public String toString() {
		return String.format("%s,%s,%s", bindStyle, ipV4, ipV6);
    }

    @Override
	public boolean equals(Object o) {
		// 非NULL及び型の確認
		if (o == null || !(o instanceof BindAddr)) {
			return false;
		}
		BindAddr b = (BindAddr) o;

		if (bindStyle == b.getBindStyle()) {
			if (ipV4.equals(b.getIpV4())) {
				if (ipV6.equals(b.getIpV6())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		assert false : "Use is not assumed.";
		return 101;
	}

	// 競合があるかどうかの確認
	public boolean checkCompetition(BindAddr b) {
		boolean v4Competition = false; // V4競合の可能性
		boolean v6Competition = false; // V6競合の可能性
		switch (bindStyle) {
            case V46DUAL:
                if (b.getBindStyle().equals(BindStyle.V46DUAL)) {
                    v4Competition = true;
                    v6Competition = true;
                } else if (b.getBindStyle().equals(BindStyle.V4ONLY)) {
                    v4Competition = true;
				} else {
                    v6Competition = true;
                }
                break;
            case V4ONLY:
                if (!b.getBindStyle().equals(BindStyle.V6ONLY)) {
                    v4Competition = true;
                }
                break;
            case V6ONLY:
                if (!b.getBindStyle().equals(BindStyle.V4ONLY)) {
                    v6Competition = true;
                }
                break;
            default:
            	break;
        }
		
        //V4競合の可能性がある場合
        if (v4Competition) {
			// どちらかがANYの場合は、競合している
			if (ipV4.getAny() || b.getIpV4().getAny()) {
				return true;
			}
			if (ipV4.equals(b.getIpV4())) {
				return true;
			}
		}
		// V6競合の可能性がある場合
		if (v6Competition) {
			// どちらかがANYの場合は、競合している
			if (ipV6.getAny() || b.ipV6.getAny()) {
				return true;
			}
			if (ipV6.equals(b.getIpV6())) {
				return true;
			}
		}
        return false;
    }
}
