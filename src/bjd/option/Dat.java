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

        //TODO: ToReg FromReg�́ABidAddr�̂悤��ToString()��Constractor(string)�ɓ��ꂷ��ׂ��H
        //�ݒ�t�@�C��(Option.ini)�ւ̓ǂݏ����̂���
        public string ToReg(bool isDebug,ListVal listVal){
            var sb = new StringBuilder();
            foreach (var o in Ar) {
                var str = o.Str;
                if (isDebug) {
                    //ZIP��listVal��OneCtrl�ƍ�����isDebug+HIDENN�̏ꍇ�́A***���o�͂���iOption.txt�p�j
                    //Dat�̂P�f�[�^�ł��镶����̂����AHIDDEN�̃e�L�X�g��***�ɕύX����
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