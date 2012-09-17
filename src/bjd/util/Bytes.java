package bjd.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//byte[]配列の操作クラス
public final class Bytes {
	private Bytes() {
		//インスタンスの生成を禁止する
	}

	//*********************************************************
	// byte[] の生成
	//*********************************************************
	//複数のオブジェクトを並べて、byte[]に変換する
	//null指定可能 / Stringは、Encoding.ASCCでバイト化される
	public static byte[] create(Object ... list) {
		int len = 0;
		for (Object o : list) {
			if (o == null) {
				continue;
			}
			switch (o.getClass().getName()) {
				case "[B":
					len += ((byte[]) o).length;
					break;
				case "java.lang.String":
					len += ((String) o).length();
					break;
				case "java.lang.Integer":
					len += 4;
					break;
				case "java.lang.Short":
					len += 2;
					break;
				case "java.lang.Long":
					len += 8;
					break;
				case "java.lang.Byte":
					len += 1;
					break;
				default:
					Util.designProblem(o.getClass().getName());
					return new byte[0];
			}
		}
		byte[] data = new byte[len];
		int offset = 0;

		for (Object o : list) {

			if (o == null) {
				continue;
			}

			switch (o.getClass().getName()) {
				case "[B":
					System.arraycopy(((byte[]) o), 0, data, offset, ((byte[]) o).length);
					//Buffer.BlockCopy(((byte[]) o), 0, data, offset, ((byte[]) o).Length);
					offset += ((byte[]) o).length;
					break;
				case "java.lang.String":
					System.arraycopy(((String) o).getBytes(), 0, data, offset, ((String) o).length());
					//Buffer.BlockCopy(Encoding.ASCII.GetBytes((string) o), 0, data, offset, ((string) o).Length);
					offset += ((String) o).length();
					break;
				case "java.lang.Integer":
//					System.arraycopy(((int) o).getBytes(), 0, data, offset,4);
//					//Buffer.BlockCopy(BitConverter.GetBytes((Int32) o), 0, data, offset, 4);
//					offset += 4;
					break;
				case "java.lang.Short":
//					Buffer.BlockCopy(BitConverter.GetBytes((Int16) o), 0, data, offset, 2);
//					offset += 2;
					break;
				case "java.lang.Long":
//					Buffer.BlockCopy(BitConverter.GetBytes((Int64) o), 0, data, offset, 8);
//					offset += 8;
					break;
				case "java.lang.Byte":
					data[offset] = (byte) o;
					offset += 1;
					break;
				default:
					Util.designProblem(o.getClass().getName());
					return new byte[0];
			}
		}
		return data;
	}

	public static byte[] GetBytes(int value){
	    ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());
	    buffer.putInt(value);
	    return buffer.array();
	}

	//*********************************************************
	// 検索
	//*********************************************************
	//bufferの中でtargetが始まる位置を検索する
	//int off 検索開始位置
	public static int indexOf(byte[] buffer, int off, byte[] target) {
		for (int i = off; i + target.length < buffer.length; i++) {
			boolean any = false;
			for (int t = 0; t < target.length; t++){
				if (buffer[i + t] != target[t]) {
					any = true;
					break;
				}
			}
			boolean match = !any;
			if (match) {
				return i;
			}
		}
		return -1;
	}
}
