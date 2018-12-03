package io.bcaas.constants;

public class APIURLConstants {

    //获取余额
    public static String API_BALANCE_URL = "https://blockchain.info/balance";

    //获取当前钱包的未交易区块
    public static String API_UNSPENT_URL = "https://blockchain.info/unspent";

    //发送交易
    public static String API_PUSHTX_URL = "https://blockchain.info/pushtx";

    //获取交易记录 : API_GETRECORD_URL/{address}
    public static String API_GETRECORD_URL = "https://blockchain.info/rawaddr";

}
