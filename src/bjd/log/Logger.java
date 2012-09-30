package bjd.log;

import java.util.Calendar;

import bjd.Kernel;
import bjd.sock.SockBase;

public final class Logger {
	private Kernel kernel;
	private String nameTag;
	private boolean useDetailsLog;
	private ILogger logger;

	public Logger(Kernel kernel, String nameTag, boolean useDetailsLog, ILogger logger) {
		this.kernel = kernel;
		this.nameTag = nameTag;
		this.useDetailsLog = useDetailsLog;
		this.logger = logger;
    }

	public void set(LogKind logKind, SockBase sockBase, int messageNo, String detailInfomation) {
		if (logKind == LogKind.Detail) {
			if (!useDetailsLog) {
				return;
			}
		}
		
		long threadId = Thread.currentThread().getId(); //TODO DEBUG GetCurrentThreadId();
		String message = kernel.getJp() ? "定義されていません" : "Message is not defined";
		if (messageNo < 9000000) {
			if (logger != null) {
				message = logger.getMsg(messageNo); //デリゲートを使用した継承によるメッセージ取得
			} else { //(9000000以上)共通番号の場合の処理
				switch (messageNo) {
					case 9000000: message = kernel.getJp() ? "サーバ開始" : "Server started it"; break;
					case 9000001: message = kernel.getJp() ? "サーバ停止" : "Server stopped"; break;
					case 9000002: message = "_subThread() started."; break;
					case 9000003: message = "_subThread() stopped."; break;
					case 9000004: message = kernel.getJp() ? "同時接続数を超えたのでリクエストをキャンセルします" : "Because the number of connection exceeded it at the same time, the request was canceled."; break;
					case 9000005: message = kernel.getJp() ? "受信文字列が長すぎます（不正なリクエストの可能性があるため切断しました)" : "Reception character string is too long (cut off so that there was possibility of an unjust request in it)"; break;
					case 9000006: message = kernel.getJp() ? "Socket.Bind()でエラーが発生しました。[UDP]" : "An error occurred in Socket.Bind() [UDP]"; break;
					case 9000007: message = kernel.getJp() ? "callBack関数が指定されていません[UDP]" : "It is not appointed in callback function [UDP]"; break;
					case 9000008: message = kernel.getJp() ? "BeginReceiveFrom()でエラーが発生しました[UDP]" : "An error occurred in BeginReceiveFrom() [UDP]"; break;
					case 9000009: message = kernel.getJp() ? "Socket.Bind()でエラーが発生しました。[TCP]" : "An error occurred in Socket.Bind() [TCP]"; break;
					case 9000010: message = kernel.getJp() ? "Socket.Listen()でエラーが発生しました。[TCP]" : "An error occurred in Socket..Listen() [TCP]"; break;
					case 9000011: message = "tcpQueue().Dequeue()=null"; break;
					case 9000012: message = "tcpQueue().Dequeue() SocektObjState != SOCKET_OBJ_STATE.CONNECT break"; break;
					case 9000013: message = "tcpQueue().Dequeue()"; break;
					case 9000014: message = "SendBinaryFile(string fileName) socket.Send()"; break;
					case 9000015: message = "SendBinaryFile(string fileName,long rangeFrom,long rangeTo) socket.Send()"; break;
					case 9000016: message = kernel.getJp() ? "このアドレスからの接続は許可されていません(ACL)" : "Connection from this address is not admitted.(ACL)"; break;
					case 9000017: message = kernel.getJp() ? "このアドレスからの接続は許可されていません(ACL)" : "Connection from this address is not admitted.(ACL)"; break;
					case 9000018: message = kernel.getJp() ? "この利用者のアクセスは許可されていません(ACL)" : "Access of this user is not admitted (ACL)"; break;
					case 9000019: message = kernel.getJp() ? "アイドルタイムアウト" : "Timeout of an idle"; break;
					case 9000020: message = kernel.getJp() ? "送信に失敗しました" : "Transmission of a message failure"; break;
					case 9000021: message = kernel.getJp() ? "ThreadBase::loop()で例外が発生しました" : "An exception occurred in ThreadBase::Loop()"; break;
					//case 9000022: message = kernel.getJp() ? "秘密鍵の読み込みに失敗しました" : "Reading of private key made a blunder"; break;
					case 9000023: message = kernel.getJp() ? "証明書の読み込みに失敗しました" : "Reading of a certificate made a blunder"; break;
					//case 9000024: message = kernel.getJp() ? "SSLネゴシエーションに失敗しました" : "SSL connection procedure makes a blunder"; break;
					//case 9000025: message = kernel.getJp() ? "ファイル（秘密鍵）が見つかりません" : "Private key is not found"; break;
					case 9000026: message = kernel.getJp() ? "ファイル（証明書）が見つかりません" : "A certificate is not found"; break;
					//case 9000027: message = kernel.getJp() ? "OpenSSLのライブラリ(ssleay32.dll,libeay32.dll)が見つかりません" : "OpenSSL library (ssleay32.dll,libeay32.dll) is not found"; break;
					case 9000028: message = kernel.getJp() ? "SSLの初期化に失敗しています" : "Initialization of SSL made a blunder"; break;
					case 9000029: message = kernel.getJp() ? "指定された作業ディレクトリが存在しません" : "A work directory is not found"; break;
					case 9000030: message = kernel.getJp() ? "起動するサーバが見つかりません" : "A starting server is not found"; break;
					case 9000031: message = kernel.getJp() ? "「ログの保存場所」が見つかりません" : "Log Save place(directory) is not found"; break;
					case 9000032: message = kernel.getJp() ? "ログ削除" : "Delete LogFile"; break;
					//case 9000033: message = kernel.getJp() ?"メールボックスにメールが格納されました" : "An email was stored to a mailbox";break;
					case 9000034: message = kernel.getJp() ? "ACL指定に問題があります" : "ACL configuration failure"; break;
					case 9000035: message = kernel.getJp() ? "Socket()でエラーが発生しました。[TCP]" : "An error occurred in Socket() [TCP]"; break;
					case 9000036: message = kernel.getJp() ? "Socket()でエラーが発生しました。[UDP]" : "An error occurred in Socket() [UDP]"; break;
					case 9000037: message = kernel.getJp() ? "_subThread()で例外が発生しました" : "An exception occurred in _subThread()"; break;
					case 9000038: message = kernel.getJp() ? "【例外】" : "[Exception]"; break;
					case 9000039: message = kernel.getJp() ? "【STDOUT】" : "[STDOUT]"; break;
					case 9000040: message = kernel.getJp() ? "拡張SMTP適用範囲の指定に問題があります" : "ESMTP range configuration failure"; break;
					case 9000041: message = kernel.getJp() ? "disp2()で例外が発生しました" : "An exception occurred in disp2()"; break;
					case 9000042: message = kernel.getJp() ? "初期化に失敗しているためサーバを開始できません" : "Can't start a server in order to fail in initialization"; break;
					case 9000043: message = kernel.getJp() ? "クライアント側が切断されました" : "The client side was cut off"; break;
					case 9000044: message = kernel.getJp() ? "サーバ側が切断されました" : "The server side was cut off"; break;
					case 9000045: message = kernel.getJp() ? "ログ削除処理で日付変換に例外が発生しました" : "An exception occurred in date conversion by log elimination processing"; break;
					case 9000046: message = kernel.getJp() ? "socket.send()でエラーが発生しました" : "socket.send()"; break;
					case 9000047: message = kernel.getJp() ? "ユーザ名が無効です" : "A user name is null and void"; break;
					case 9000048: message = kernel.getJp() ? "ThreadBase::Loop()で例外が発生しました" : "An exception occurred in ThreadBase::Loop()"; break;
					case 9000049: message = kernel.getJp() ? "【例外】" : "[Exception]"; break;
					case 9000050: message = kernel.getJp() ? "ファイルにアクセスできませんでした" : "Can't open a file"; break; 
					case 9000051: message = kernel.getJp() ? "インスタンスの生成に失敗しました" : "Can't create instance"; break; 
					case 9000052: message = kernel.getJp() ? "名前解決に失敗しました" : "Non-existent domain"; break; 
					case 9000053: message = kernel.getJp() ? "【例外】SockObj.Resolve()" : "[Exception] SockObj.Resolve()"; break;
					case 9000054: message = kernel.getJp() ? "Apache Killerによる攻撃の可能性があります" : "There is possibility of attack by Apache Killer in it"; break;
					case 9000055: message = kernel.getJp() ? "【自動拒否】「ACL」の禁止する利用者（アドレス）に追加しました" : "Add it to a deny list automatically"; break;
					case 9000056: message = kernel.getJp() ? "不正アクセスを検出しましたが、ACL「拒否」リストは追加されませんでした" : "I detected possibility of Attack, but the ACL [Deny] list was not added"; break;
					case 9000057: message = kernel.getJp() ? "【例外】" : "[Exception]"; break;
					case 9000058: message = kernel.getJp() ? "メールの送信に失敗しました" : "Failed in the transmission of a message of an email"; break;
					case 9000059: message = kernel.getJp() ? "メールの保存に失敗しました" : "Failed in a save of an email"; break;
					case 9000060: message = kernel.getJp() ? "【例外】" : "[Exception]"; break;
					default:
						break;
				}
			}
		}
    	Calendar c = Calendar.getInstance(); //現在時間で初期化される
    	String dateStr = String.format("%04d/%02d/%02d %02d:%02d:%02d",
    			c.get(Calendar.YEAR),
    			c.get(Calendar.MONTH) + 1,
    			c.get(Calendar.DATE),
    			c.get(Calendar.HOUR),
    			c.get(Calendar.MINUTE),
    			c.get(Calendar.SECOND));
		String messageNoStr = String.format("%7d", messageNo);
        String remoteAddrStr = (sockBase == null) ? "-" : sockBase.getRemoteHost();
		OneLog oneLog = new OneLog(dateStr, logKind.toString(), nameTag, String.valueOf(threadId), remoteAddrStr, messageNoStr, message, detailInfomation);

		//ログファイルへの追加
		if (kernel.getLogFile() != null) {
            kernel.getLogFile().append(oneLog);
        }
       
    }

	public void exception(Exception ex) {
		int messageNo = 9000060;
        set(LogKind.Error, null, messageNo, ex.getMessage());
        //TODO Logger 例外メッセージが投かんされた時のスタックトレース　未実装
//        string[] tmp = ex.StackTrace.Split(new[] { '\r', '\n' }, StringSplitOptions.RemoveEmptyEntries);
//        for (String s : tmp) {
//            var lines = new List<string>();
//            var l = Util.SwapStr("\r\n", "", s);
//            while (true) {
//                if (l.Length < 80) {
//                    lines.Add(l);
//                    break;
//                }
//                lines.Add(l.Substring(0, 80));
//                l = l.Substring(80);
//            }
//            for (int i = 0; i < lines.Count; i++) {
//                if (i == 0) {
//                    Set(LogKind.Error, sockObj, messageNo, lines[i]);
//                } else {
//                    Set(LogKind.Error, sockObj, messageNo, "   -" + lines[i]);
//                }
//            }
//        }
    }
}

