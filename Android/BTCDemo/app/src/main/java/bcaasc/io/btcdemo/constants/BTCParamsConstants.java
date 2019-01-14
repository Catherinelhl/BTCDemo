package bcaasc.io.btcdemo.constants;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/13
 * <p>
 * 定义BTC相关参数的常量
 */
public class BTCParamsConstants {

    //当前是否测试环境
    public static final boolean isTest = true;

    //根据是否是测试环境，返回网络参数
    public static final NetworkParameters NetworkParameter = isTest ? TestNet3Params.get() : MainNetParams.get();

    public static final String BtcUnit = "100000000";
    public static final String BtcUnitRevert = "0.00000001";
}
