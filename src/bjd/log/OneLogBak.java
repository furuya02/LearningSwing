package bjd.log;

public final class OneLogBak {
	private String dateStr;
	private String logKind;
	private String nameTag;
	private String threadId;
	private String remoteAddr;
	private String messageNo;
	private String message;
	private String detailInfomation;

	public String getDetailInfomation() {
		return detailInfomation;
	}

	public String getLogKind() {
		return logKind;
	}

	public String getNameTag() {
		return nameTag;
	}

	public String getThreadId() {
		return threadId;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getMessageNo() {
		return messageNo;
	}

	public String getMessage() {
		return message;
	}


	/**
	 * コンストラクタ<br>
	 * すべて文字列で指定する<br>
	 * 
	 * @param dateStr 日付
	 * @param logKind　ログの種類
	 * @param nameTag 名前タグ
	 * @param threadId スレッドID
	 * @param remoteAddr リモートアドレス
	 * @param messageNo メッセージ番号
	 * @param message メッセージ
	 * @param detailInfomation 詳細情報
	 */
	public OneLogBak(String dateStr, String logKind, String nameTag, String threadId, String remoteAddr, String messageNo, String message, String detailInfomation) {
		this.dateStr = dateStr;
		this.logKind = logKind;
		this.nameTag = nameTag;
		this.threadId = threadId;
		this.remoteAddr = remoteAddr;
		this.messageNo = messageNo;
		this.message = message;
		this.detailInfomation = detailInfomation;
	}

	/**
	 * コンストラクタ<br>
	 * １行の文字列(\t区切り)で指定される<br>
	 * 
	 * @param str
	 */
	public OneLogBak(String str) {
		String[] tmp = str.split("\t");
		if (tmp.length != 8) {
			return;
		}
		dateStr = tmp[0];
		logKind = tmp[1];
		threadId = tmp[2];
		nameTag = tmp[3];
		remoteAddr = tmp[4];
		messageNo = tmp[5];
		message = tmp[6];
		detailInfomation = tmp[7];
	}

	/**
	 * 文字列化<br>
	 * \t区切りで出力される<br>
	 */
	public String toString() {
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", dateStr, logKind, threadId, nameTag, remoteAddr, messageNo, message, detailInfomation);
	}

	/**
	 * セキュリティログかどうかの確認
	 * @return セキュリティログの場合 true
	 */
	public boolean isSecure() {
		if (logKind.equals((LogKind.SECURE).toString())) {
			return true;
		}
		return false;
	}
}
