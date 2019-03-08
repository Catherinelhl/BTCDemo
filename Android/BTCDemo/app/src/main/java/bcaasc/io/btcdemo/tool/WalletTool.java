package bcaasc.io.btcdemo.tool;
/*
+--------------+---------------------------------
+ author       +   Catherine Liu
+--------------+---------------------------------
+ since        +   2019/3/8 上午10:24
+--------------+---------------------------------
+ projectName  +   BTCDemo
+--------------+---------------------------------
+ packageName  +   bcaasc.io.btcdemo.tool
+--------------+---------------------------------
+ description  +
+--------------+---------------------------------
+ version      +
+--------------+---------------------------------
*/

import bcaasc.io.btcdemo.constants.BTCParamsConstants;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;

import java.math.BigInteger;

import static org.bitcoinj.core.Utils.HEX;

public class WalletTool {

    public static void main(String[] args) {
//        createWallet("93JyCWhaavABdipL7Pitfp4oXKn3o91G3uUohPzdT8DvUpT926p");
        createWallet();
    }

    public static void createWallet() {

        try {

            // 取得私鑰WIF格式
            String privateKeyAsHex = new ECKey().getPrivateKeyAsHex();
            BigInteger privateKeyInt = new BigInteger(1, HEX.decode(privateKeyAsHex.toLowerCase()));
            // 未壓縮
            ECKey privateKey = ECKey.fromPrivate(privateKeyInt, false);

            System.out.println("privateKey:" + privateKey.getPrivateKeyAsWiF(BTCParamsConstants.getNetworkParameter()));
            // 公鑰(長度130)
            System.out.println("publicKey:" + privateKey.getPublicKeyAsHex());
            // 產生地址
            System.out.println("address:" + privateKey.toAddress(BTCParamsConstants.getNetworkParameter()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createWallet2(){
       BigInteger bigInteger= new ECKey().getPrivKey();
        System.out.println("privateKey:"+bigInteger);
    }

    public static void createWallet(String privateKeyAsWiFStr) {

        try {

            // 私鑰WIF格式字串取得ECKey
            ECKey privateKey = DumpedPrivateKey.fromBase58(BTCParamsConstants.getNetworkParameter(), privateKeyAsWiFStr).getKey();

            System.out.println("privateKey:" + privateKey.getPrivateKeyAsWiF(BTCParamsConstants.getNetworkParameter()));
            // 公鑰(長度130)
            System.out.println("publicKey:" + privateKey.getPublicKeyAsHex());
            // 產生地址
            System.out.println("address:" + privateKey.toAddress(BTCParamsConstants.getNetworkParameter()).toBase58());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
