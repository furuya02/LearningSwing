package bjd.net;

public enum SocketObjState {
	//TODO 移植完了後　リファクタリングで大文字に変更
	Idle, //初期
	Connect, //接続
	Disconnect, //切断
	Error //エラー発生
}
