package bjd.option;

import bjd.Kernel;
import bjd.util.ListBase;
import bjd.util.Util;

public final class ListOption extends ListBase<OneOption> {

	private Kernel kernel;

	public ListOption(Kernel kernel) {
		this.kernel = kernel;
	}

	public OneOption get(String nameTag) {
		for (OneOption o : ar) {
			if (o.getNameTag().equals(nameTag)) {
				return o;
			}
		}
		//TODO DEBUGのためにとりあえずnullを返す
		if (nameTag.equals("Basic")) {
			return null;
		}
		Util.designProblem(String.format("nameTag=%s", nameTag));
		return null;
	}

	//null追加を回避するために、ar.add()は、このファンクションを使用する
	private boolean add(OneOption o) {
		if (o == null) {
			return false;
		}
		ar.add(o);
		return true;
	}

	//Kernel.Dispose()で、有効なオプションだけを出力するために使用する
	public void save() {
		for (OneOption o : ar) {
			o.save();
		}
	}

	//オプションの読み込み
	//    public boolean read2(String nameTag) {
	//        for(OneOption o : ar) {
	//            if (o.getNameTag().equals(nameTag)) {
	//                o..read2(nameTag);
	//                return true;
	//            }
	//        }
	//        return false;
	//    }	

	//オプションリストの初期化
	public void initialize() {

		ar.clear();

		//固定的にBasicとLogを生成する
		String executePath = ""; // Application.ExecutablePath
		add(new OptionBasic(kernel, executePath, "Basic")); //「基本」オプション
		add(new OptionLog(kernel, executePath, "Log")); //「ログ」オプション

		/*
		//DLLを検索し、各オプションを生成する
		//Ver5.2.4 関係ない*Server.dll以外は、対象外とする
		//var list = Directory.GetFiles(kernel.ProgDir(), "*.dll").ToList();
		var list = Directory.GetFiles(kernel.ProgDir(), "*Server.dll").ToList();
		list.Sort();
		//foreach (var path in Directory.GetFiles(kernel.ProgDir(), "*.dll")) {
		foreach (var path in list) {

		    //テスト時の関連ＤＬＬを読み飛ばす
		    if (path.IndexOf("TestDriven") != -1)
		        continue;

		    string nameTag = Path.GetFileNameWithoutExtension(path);

		    //DLLバージョン確認
		    var vi = FileVersionInfo.GetVersionInfo(path);
		    if (vi.FileVersion != Define.ProductVersion()) {
		        throw new Exception(string.Format("A version of DLL is different [{0} {1}]", nameTag, vi.FileVersion));
		    }
		    
		    if (nameTag == "WebServer") {
		        var op = (OneOption)Util.CreateInstance(kernel, path, "OptionVirtualHost", new object[] { kernel, path, "VirtualHost" });
		        if (Add(op)) {
		            //WebServerの場合は、バーチャルホストごとに１つのオプションを初期化する
		            foreach (var o in (Dat)op.GetValue("hostList")) {
		                if (o.Enable) {
		                    string name = string.Format("Web-{0}:{1}", o.StrList[1], o.StrList[2]);
		                    Add((OneOption)Util.CreateInstance(kernel, path, "Option", new object[] { kernel, path, name }));
		                }
		            }
		        }
		    } else if (nameTag == "TunnelServer") {
		        //TunnelServerの場合は、１トンネルごとに１つのオプションを初期化する
		        var op = (OneOption)Util.CreateInstance(kernel, path, "OptionTunnel", new object[] { kernel, path, "TunnelList" });
		        if (Add(op)) {
		            //トンネルのリスト
		            foreach (var o in (Dat)op.GetValue("tunnelList")) {
		                if (o.Enable) {

		                    //int protocol = (int)o[0].Obj;//プロトコル
		                    //int port = (int)o[1].Obj;//クライアントから見たポート
		                    //string targetServer = (string)o[2].Obj;//接続先サーバ
		                    //int targetPort = (int)o[3].Obj;//接続先ポート
		                    string name = string.Format("{0}:{1}:{2}:{3}", (o.StrList[0] == "0") ? "TCP" : "UDP", o.StrList[1], o.StrList[2], o.StrList[3]);
		                    Add((OneOption)Util.CreateInstance(kernel, path, "Option", new object[] { kernel, path, "Tunnel-" + name }));
		                }
		            }
		        }
		    } else {  //上記以外
		        //DLLにclass Optionが含まれていない場合、Util.CreateInstanceはnulllを返すため、以下の処理はスキップされる
		        if (Add((OneOption)Util.CreateInstance(kernel, path, "Option", new object[] { kernel, path, nameTag }))) {
		            //DnsServerがリストされている場合 ドメインリソースも追加する
		            if (nameTag == "DnsServer") {
		                var o = (OneOption)Util.CreateInstance(kernel, path, "OptionDnsDomain", new object[] { kernel, path, "DnsDomain" });
		                if (Add(o)) {
		                    foreach (var e in (Dat)o.GetValue("domainList")) {
		                        if (e.Enable) {
		                            Add((OneOption)Util.CreateInstance(kernel, path, "OptionDnsResource", new object[] { kernel, path, "Resource-" + e.StrList[0] }));
		                        }
		                    }
		                }
		            }else if (nameTag == "SmtpServer") {
		#if ML_SERVER
		                var o = (OneOption)Util.CreateInstance(kernel,path, "OptionMl", new object[] { kernel, path, "Ml" });
		                if (Add(o)) {
		                    foreach (var e in (Dat)o.GetValue("mlList")) {
		                        if (e.Enable) {
		                            Add((OneOption)Util.CreateInstance(kernel,path, "OptionOneMl", new object[] { kernel, path, "Ml-" + e.StrList[0] }));
		                        }
		                    }
		                }
		#endif
		            }
		        }
		    }
		}
		//SmtpServer若しくはPopServerがリストされている場合、MailBoxを生成する
		if (Get("SmtpServer")!=null || Get("PopServer")!=null){
		    Add(new OptionMailBox(kernel, Application.ExecutablePath, "MailBox"));//メールボックス
		}

		}
		*/
	}
}
