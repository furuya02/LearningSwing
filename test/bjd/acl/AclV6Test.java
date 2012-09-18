package bjd.acl;

import static org.junit.Assert.*;

import org.junit.Test;

public class AclV6Test {

	@Test
	public void test() {
		fail("まだ実装されていません");
	}

}
/*
        [TestCase("1122:3344::/32", "1122:3344::", "1122:3344:ffff:ffff:ffff:ffff:ffff:FFFF")]
        [TestCase("1122:3344::/64", "1122:3344::", "1122:3344::ffff:ffff:ffff:FFFF")]
        [TestCase("1122:3344::-1122:3355::", "1122:3344::", "1122:3355::")]
        [TestCase("1122:3355::-1122:3344::", "1122:3344::", "1122:3355::")]
        [TestCase("1122:3344::2", "1122:3344::2", "1122:3344::2")]
        [TestCase("*", "::0", "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")]
        [TestCase("*:*:*:*:*:*:*:*", "::0", "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")]
        public void StartEndTest(string strAcl, string start, string end) {
            var o = new AclV6("test", strAcl);
            Assert.AreEqual(o.Start, new Ip(start));
            Assert.AreEqual(o.End, new Ip(end));
        }

        [TestCase("1122:3344::/64", "1122:3343::", false)]
        [TestCase("1122:3344::/64", "1122:3344::1", true)]
        [TestCase("1122:3344::/64", "1122:3345::", false)]
        public void IsHitTest(string strAcl, string ipStr, bool status) {
            var o = new AclV6("test", strAcl);
            Assert.AreEqual(o.IsHit(new Ip(ipStr)), status);
        }
    }

}
*/