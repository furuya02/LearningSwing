package bjd.util;

import java.util.ArrayList;

import javax.swing.text.AbstractDocument.LeafElement;

public class Inet {

        private Inet(){}//デフォルトコンストラクタの隠蔽


        //バイナリ-文字列変換
        static public byte[] GetBytes(String str) {
            //Ver5.1.5
            return Encoding.Unicode.GetBytes(str);
        }
        static public String GetString(byte [] buf){
            //Ver5.1.5
            return Encoding.Unicode.GetString(buf);
        }

        //Ver5.0.0-a11 高速化
        //テキスト処理クラス　Stringを\r\nでArrayList<String>に分割する
        static public ArrayList<String> GetLines(String str){
            return str.Split(new[]{"\r\n"}, StringSplitOptions.None).ToList();
        }

        //行単位での切り出し(\r\nは削除しない)
        static public ArrayList<byte []> GetLines(byte [] buf) {
        	 ArrayList<byte[]> lines = new ArrayList<>();
            int start = 0;
            for (int end = 0;; end++) {
                if (buf[end] == '\n') {
                    if (1 <= end && buf[end - 1] == '\r') {
                        byte [] tmp = new byte[end - start + 1];//\r\nを削除しない
                        Buffer.BlockCopy(buf,start,tmp,0,end - start + 1);//\r\nを削除しない
                        lines.Add(tmp);
                        //String str = Encoding.ASCII.GetString(tmp);
                        //ar.Add(str);
                        start = end + 1;
                    //Unicode
                    } else if(2 <= end && end + 1 < buf.Length && buf[end + 1] == '\0' && buf[end - 1] == '\0' && buf[end - 2] == '\r') {
                        byte [] tmp = new byte[end - start + 2];//\r\nを削除しない
                        Buffer.BlockCopy(buf,start,tmp,0,end - start + 2);//\r\nを削除しない
                        lines.Add(tmp);
                        start = end + 2;
                    } else {//\nのみ
                        byte [] tmp = new byte[end - start + 1];//\nを削除しない
                        Buffer.BlockCopy(buf,start,tmp,0,end - start + 1);//\nを削除しない
                        lines.add(tmp);
                        start = end + 1;
                    }
                }
                if (end >= buf.length-1) {
                    if (0 < (end - start + 1)) {
                        byte [] tmp = new byte[end - start + 1];//\r\nを削除しない
                        Buffer.BlockCopy(buf,start,tmp,0,end - start + 1);//\r\nを削除しない
                        lines.add(tmp);
                    }
                    break;
                }
            }
            return lines;
        }
        //\r\nの削除
        static public byte[] TrimCrlf(byte[] buf) {
            if(buf.length >= 1 && buf[buf.length - 1] == '\n') {
                int count=1;
                if(buf.length >= 2 && buf[buf.length - 2] == '\r') {
                    count++;
                }
                byte [] tmp = new byte[buf.length-count];
                Buffer.BlockCopy(buf,0,tmp,0,buf.length - count);
                return tmp;
            }
            return buf;
        }
        //\r\nの削除
        static public String TrimCrlf1(String str) {
            if(str.length() >= 1 && str.charAt(str.length() - 1) == '\n') {
                int count = 1;
                if(str.length() >= 2 && str.charAt(str.length() - 2) == '\r') {
                    count++;
                }
                return str.substring(0,str.length() - count);
            }
            return str;
        }
        
        //サニタイズ処理(１行対応)
        public static String Sanitize(String str) {
        	str = str.replaceAll("&", "&amp;");
        	str = str.replaceAll("<", "&lt;");
        	str = str.replaceAll(">", "&gt;");
        	str = str.replaceAll("~", "&7E;");
//            str = Util.SwapStr("&", "&amp;", str);
//            str = Util.SwapStr("<", "&lt;", str);
//            str = Util.SwapStr(">", "&gt;", str);
//            str = Util.SwapStr("~", "%7E", str);
            return str;

        }
      

        //クライアントソケットを作成して相手先に接続する
        static public TcpObj Connect(ref boolean life, Kernel kernel, Logger logger, Ip ip, Int32 port, Ssl ssl) {
            //float fff = 0;

            //TcpObj tcpObj = new TcpObj(kernel, logger, ip, port, fff, ssl);
            var tcpObj = new TcpObj(kernel, logger, ip, port, ssl);

            Thread.Sleep(0);
            while (life) {
                if (tcpObj.State == SocketObjState.Connect) {
                    return tcpObj;
                }
                if (tcpObj.State == SocketObjState.Error) {
                    tcpObj.Close();//2009.06.01追加
                    return null;
                }
                if (tcpObj.State == SocketObjState.Disconnect) {
                    //相手から即効で切られた場合
                    tcpObj.Close();//2009.06.10追加
                    return null;
                }
                //Ver5.0.0-a11 勝負
                //Thread.Sleep(100);
                Thread.Sleep(10);
            }
            tcpObj.Close();//2009.06.01追加
            return null;
        }
        

        //指定した長さのランダム文字列を取得する（チャレンジ文字列用）
        static public String ChallengeStr(int len) {
            final String val = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            byte[] bytes = new byte[len];
            var rngcsp = new RNGCryptoServiceProvider();
            rngcsp.GetNonZeroBytes(bytes);
            
            // 乱数をもとに使用文字を組み合わせる
            String str = String.Empty;
            for (int b : bytes) {
                var rnd = new Random(b);
                int index = rnd.Next(val.Length);
                str += val[index];
            }
            return str;
        }
        //ハッシュ文字列の作成（MD5）
        static public String Md5Str(String str) {
            var md5EncryptionObject = new MD5CryptoServiceProvider();
            var originalStringBytes = Encoding.Default.GetBytes(str);
            var encodedStringBytes = md5EncryptionObject.ComputeHash(originalStringBytes);
            return BitConverter.ToString(encodedStringBytes);
        }

        //リクエスト行がURLエンコードされている場合は、その文字コードを取得する
        static public Encoding GetUrlEncoding(String str) {
            String[] tmp = str.split(" ");
            if(tmp.length >= 3){
                str = tmp[1];
            }
            byte [] buf = new byte[str.length()];
            int len = 0;
            boolean find = false;
            for(int i = 0;i < str.length();i++) {
                if(str.charAt(i) == '%') {
                    find = true;
                    String hex = String.format("%c%c",str.charAt(i + 1),str.charAt(i + 2));
                    int n = Convert.ToInt32(hex,16);
                    buf[len++] = (byte)n;
                    i += 2;
                } else {
                    buf[len++] = (byte)str.charAt(i);
                }
            }
            if(!find){
                return Encoding.ASCII;
            }
            byte [] buf2 = new byte[len];
            Buffer.BlockCopy(buf,0,buf2,0,len);
            return MLang.GetEncoding(buf2);
        }



    }
}
