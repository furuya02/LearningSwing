package bjd.option;
/*
public class Dat extends ListBase<OneDat> {
        public void Add(bool enable, string str) {
            Ar.Add(new OneDat(enable, str));
        }
        public OneDat this[int index] {
            get {
                return Ar[index];
            }
            set {
                Ar[index] = value;
            }
        }

        //TODO: ToReg FromRegは、BidAddrのようにToString()とConstractor(string)に統一するべき？
        //設定ファイル(Option.ini)への読み書きのため
        public string ToReg(bool isDebug,ListVal listVal){
            var sb = new StringBuilder();
            foreach (var o in Ar) {
                var str = o.Str;
                if (isDebug) {
                    //ZIPでlistValのOneCtrlと混合しisDebug+HIDENNの場合は、***を出力する（Option.txt用）
                    //Datの１データである文字列のうち、HIDDENのテキストを***に変更する
                    var tmpStr = o.Str.Split('\t');
                    var tmp = new StringBuilder();
                    foreach (var a in tmpStr.Zip(listVal, (s, l) => new { s, l.OneCtrl })){
                        //if (tmp.Length == 0)
                        //    tmp.Append('\t');
                        if (tmp.Length != 0)
                                tmp.Append("\t");
                        tmp.Append(a.OneCtrl.GetType() == CtrlType.Hidden ? "***" : a.s);
                    }
                    str = tmp.ToString();
                }

                if (sb.Length != 0)
                    sb.Append("\b");
                sb.Append(o.Enable ? "" : "#");
                sb.Append(str);
            }
            return sb.ToString();
        }
        public void FromReg(string str){
            Ar.Clear();
            if (str != "") {
                foreach (var l in str.Split('\b')) {
                    if (l.Length>0 && l[0] == '#') {
                        Ar.Add(new OneDat(false, l.Substring(1)));
                    } else {
                        Ar.Add(new OneDat(true, l));
                    }
                }
            }
        }

    }
}
*/