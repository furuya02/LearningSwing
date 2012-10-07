package bjd.sock;

public enum SockState {
	//TODO 移植完了後　リファクタリングで大文字に変更
	Idle, //初期
	Connect, //接続
	//Disconnect, //切断
	Bind, //bind中
	Error, //エラー発生
}

//SockServerの場合
//Idle -> BInd or Error -> Error
//SockAcceptの場合
//Idle => connect -> Disconnet -> Error
