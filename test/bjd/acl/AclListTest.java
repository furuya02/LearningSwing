package bjd.acl;

import static org.junit.Assert.*;

import org.junit.Test;

public class AclListTest {

	@Test
	public void test() {
		fail("まだ実装されていません");
	}

}
/*
 * using NUnit.Framework;
using Bjd;



namespace BjdTest {
    public class AclListTest {

        Logger _logger;

        [SetUp]
        public void SetUp() {
            var kernel = new Kernel(null, null, null, null);
            _logger = new Logger(kernel, "TEST", false, null);

        }

        [TearDown]
        public void TearDown() {

        }

        [TestCase(1, true)]//enableNum=1 のみを禁止する
        [TestCase(0, false)]//enableNum=0 のみを許可する
        public void AclList1Test(int enableNum, bool check) {
            var ip = new Ip("192.168.0.1");
            var dat = new Dat();
            var o = new AclList(dat, enableNum, _logger);//enableNum=1 のみを禁止する
            Assert.AreEqual(o.Check(ip), check);
        }

        //192.168.0.1が範囲に含まれるかどうかのチェック
        [TestCase("192.168.0.1", true)]
        [TestCase("192.168.0.0/24", true)]
        [TestCase("192.168.1.0/24", false)]
        [TestCase("192.168.0.0-192.168.0.100", true)]
        [TestCase("192.168.0.2-192.168.0.100", false)]
        [TestCase("192.168.0.0-192.168.2.100", true)]
        [TestCase("192.168.0.1-5", true)]
        [TestCase("192.168.0.2-5", false)]
        [TestCase("192.168.0.*", true)]
        [TestCase("192.168.1.*", false)]
        [TestCase("192.168.*.*", true)]
        [TestCase("192.*.*.*", true)]
        [TestCase("*.*.*.*", true)]
        [TestCase("*", true)]
        [TestCase("172.*.*.*", false)]
        public void CheckTest(string aclStr, bool isRange) {
            var ip = new Ip("192.168.0.1");
            var dat = new Dat();
            dat.Add(true, string.Format("NAME\t{0}", aclStr));

            var enableNum = 0;//enableNum=0 のみを許可する
            var o = new AclList(dat, enableNum, _logger);
            Assert.AreEqual(o.Check(ip), isRange);

            enableNum = 1;//enableNum=1 のみを禁止する
            o = new AclList(dat, enableNum, _logger);
            Assert.AreEqual(o.Check(ip), !isRange);

        }


        [Test]
        public void AclList2Test() {
            var ip = new Ip("192.168.0.1");

            //追加なし
            var dat = new Dat();
            var o = new AclList(dat, 0, _logger);//enableNum=0 のみを許可する
            Assert.AreEqual(o.Append(ip), false);//追加は失敗する
            Assert.AreEqual(o.Check(ip), false);

            o = new AclList(dat, 1, _logger);//enableNum=1 のみを禁止する
            Assert.AreEqual(o.Append(ip), true);//追加は成功する
            Assert.AreEqual(o.Check(ip), false);

        }
    }
}

 * */
