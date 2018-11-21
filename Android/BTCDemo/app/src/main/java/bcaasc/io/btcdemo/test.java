package bcaasc.io.btcdemo;

import bcaasc.io.btcdemo.constants.BTCParamsConstants;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;

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
//        System.out.println("30440220750f78bbed3aed4ad7427fe3aab279353d66a8da03e54e977437d32fd70c2b6e0220775c9b68be56130c97fa113c5027e85849ed870b201bee0a01dd100d6d6f9ee0"));
        System.out.println(new Script(Utils.parseAsHexOrBase58("3045022100f55c99ed6349338dc07680f13217a15a9f51d1d91eadadc10ffc94c50e38083402200790c47c91c5c24e964c5072eb0dde6af11f3f237ca1365b73c19337e6e46115")));
        System.out.println(new Sha256Hash(Utils.parseAsHexOrBase58("4d1ae5265cdf6fbfd47f29b2e1d46172cd361e6775db1ec8b39d63f7e3c1beff")));

    }
}
