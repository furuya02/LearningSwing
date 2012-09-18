package bjd.acl;

import static org.junit.Assert.*;

import org.junit.Test;

public class AclV4Test {

	@Test
	public void test() {
		fail("まだ実装されていません");
	}

}


/*    
    [TestFixture]
    class AclV4Test {

        [SetUp]
        public void SetUp() {
        }

        [TearDown]
        public void TearDown() {
        
        }

        //指定の要領
        //192.168.0.1
        //192.168.0.1-200
        //192.168.0.1-192.168.10.254
        //192.168.10.254-192.168.0.1（開始と終了が逆転してもＯＫ）
        //192.168.0.1/24
        //192.168.*.* 
        //*.*.*,*
        //*

        [TestCase("192.168.0.1-192.168.10.254", "192.168.0.1", "192.168.10.254")]
        [TestCase("192.168.0.1-200", "192.168.0.1", "192.168.0.200")]
        [TestCase("*", "0.0.0.0", "255.255.255.255")]
        [TestCase("192.168.*.*", "192.168.0.0", "192.168.255.255")]
        [TestCase("192.168.0.*", "192.168.0.0", "192.168.0.255")]
        [TestCase("192.168.0.1/24", "192.168.0.0", "192.168.0.255")]
        [TestCase("192.168.10.254-192.168.0.1", "192.168.0.1", "192.168.10.254")]
        [TestCase("192.168.0.1", "192.168.0.1", "192.168.0.1")]
        public void StartEndTest(string strAcl,string start,string end) {
            var o = new AclV4("test",strAcl);
            Assert.AreEqual(o.Start, new Ip(start));
            Assert.AreEqual(o.End, new Ip(end));

        }

        [TestCase("192.168.1.0/24","192.168.1.0",true)]
        [TestCase("192.168.1.0/24","192.168.1.255",true)]
        [TestCase("192.168.1.0/24","192.168.0.255",false)]
        [TestCase("192.168.1.0/24","192.168.2.0",false)]
        [TestCase("*", "192.168.2.0", true)]
        public void IsHitTest(string strAcl, string ipStr, bool status) {
            var o = new AclV4("test",strAcl);
            Assert.AreEqual(o.IsHit(new Ip(ipStr)),status);
        }

    }
}
*/