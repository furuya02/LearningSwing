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
		ByteBuffer data = ByteBuffer.allocate(len);
		data.order(ByteOrder.LITTLE_ENDIAN);

		for (Object o : list) {
			if (o == null) {
				continue;
			}
			switch (o.getClass().getName()) {
				case "[B":
					data.put(((byte[]) o));
					break;
				case "java.lang.String":
					data.put(((String) o).getBytes());
					break;
				case "java.lang.Integer":
					data.putInt((int) o);
					break;
				case "java.lang.Short":
					data.putShort((short) o);
					break;
				case "java.lang.Long":
					data.putLong((long) o);
					break;
				case "java.lang.Byte":
					data.put((byte) o);
					break;
				default:
					Util.designProblem(o.getClass().getName());
					return new byte[0];
			}
		}
		return data.array();
	}

	//*********************************************************
	// 検索
	//*********************************************************
	//bufferの中でtargetが始まる位置を検索する
	//int off 検索開始位置
	public static int indexOf(byte[] buffer, int off, byte[] target) {
		for (int i = off; i + target.length < buffer.length; i++) {
			boolean any = false;
			for (int t = 0; t < target.length; t++) {
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
