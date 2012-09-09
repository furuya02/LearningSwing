package bjd.log;

public final class OneLog {
    private String dateStr;
    private String logKind;
    private String nameTag;
    private String threadId;
    private String remoteAddr;
    private String messageNo;
    private String message;
    private String detailInfomation;

    public OneLog(String dateStr, String logKind, String nameTag, String threadId, String remoteAddr, String messageNo, String message, String detailInfomation) {
        this.dateStr = dateStr;
        this.logKind = logKind;
        this.nameTag = nameTag;
        this.threadId = threadId;
        this.remoteAddr = remoteAddr;
        this.messageNo = messageNo;
        this.message = message;
        this.detailInfomation = detailInfomation;
    }
    public OneLog(String str) {
        String[] tmp = str.split("'\t");
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
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s"
                    , dateStr
                    , logKind
                    , threadId
                    , nameTag
                    , remoteAddr
                    , messageNo
                    , message
                    , detailInfomation);
    }
}

