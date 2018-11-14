package bcaasc.io.btcdemo;

import bcaasc.io.btcdemo.constants.BTCParamsConstants;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;

import java.math.BigDecimal;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/14
 */
public class test {

    public static void main(String[] args) {
        System.out.println("this is a  test");
//        BigDecimal oldFee = new BigDecimal("").multiply(new BigDecimal(BTCParamsConstants.BtcUnit));
//        System.out.println(oldFee);
        System.out.println(Utils.parseAsHexOrBase58("4d1ae5265cdf6fbfd47f29b2e1d46172cd361e6775db1ec8b39d63f7e3c1beff"));
        System.out.println(new Sha256Hash(Utils.parseAsHexOrBase58("4d1ae5265cdf6fbfd47f29b2e1d46172cd361e6775db1ec8b39d63f7e3c1beff")));

    }
}
