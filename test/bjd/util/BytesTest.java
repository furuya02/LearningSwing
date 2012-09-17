package bjd.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class BytesTest {

	@Test
	public void test() {
		fail("まだ実装されていません");
	}

}
byte[] _data;

[SetUp]
public void SetUp() {

    //Create()
    const int max = 100;
    var dmy = new byte[max];
    foreach (byte i in Enumerable.Range(0, 100)) {
        dmy[i] = i;
    }

    //Create(params object[] list)
    byte b = 1;
    Int16 a1 = 2;
    Int32 a2 = 3;
    Int64 a3 = 4;
    string s = "123";

    _data = Bytes.Create(dmy, b, a1, a2, a3, s, dmy);

}

[TearDown]
public void TearDown(){

}

[TestCase(100, (byte)1)]
[TestCase(100 + 1 , (byte)2)]
[TestCase(100 + 1 + 2, (byte)3)]
[TestCase(100 + 1 + 2 + 4, (byte)4)]
[TestCase(100 + 1 + 2 + 4 + 8 + 0, (byte)'1')]
[TestCase(100 + 1 + 2 + 4 + 8 + 1, (byte)'2')]
[TestCase(100 + 1 + 2 + 4 + 8 + 2, (byte)'3')]
[TestCase(100 + 1 + 2 + 4 + 8 + 3, (byte)0)]
public void BytesTest(int index,byte val) {
    Assert.AreEqual(_data[index],val);
}

[TestCase(0, 49)]//１つ目dmyの中に存在する
[TestCase(50, 100 + 1 + 2 + 4 + 8)]
[TestCase(100, 100 + 1 + 2 + 4 + 8)]
[TestCase(150,167)] //２つ目dmyの中に存在する
[TestCase(200,-1)] //存在しない
public void SearchTest(int offset, int index) {
    var src = Encoding.ASCII.GetBytes("123");
    var n = Bytes.IndexOf(_data, offset, src);
    Assert.AreEqual(index, n);
}
}
}
