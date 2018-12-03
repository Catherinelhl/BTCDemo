package io.bcaas.constants;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

    public static final String CHARSET_UTF8 = "utf-8";

    public static final String SEND_RESPONSE_SUCCESS = "Submitted";

    //当前是否测试环境
    public static final boolean isTest = false;

    //根据是否是测试环境，返回网络参数
    public static final NetworkParameters NetworkParameter = isTest ? TestNet3Params.get() : MainNetParams.get();

    public static final String BtcUnit = "100000000";
    public static final String BtcUnitRevert = "0.00000001";

    // Log Name for appender
    public static final String LOG_INFO = "log.info";
    public static final String LOG_DEBUG = "log.debug";

    public static final Logger LOGGER_INFO = LoggerFactory.getLogger(LOG_INFO);
    public static final Logger LOGGER_DEBUG = LoggerFactory.getLogger(LOG_DEBUG);

    public static final String WEBSITE_START = " --- [Start]";
    public static final String WEBSITE_END = " --- [End]";
}
