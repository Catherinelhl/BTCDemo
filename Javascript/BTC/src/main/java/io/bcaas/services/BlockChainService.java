package io.bcaas.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.bcaas.api.HttpClient;
import io.bcaas.constants.APIURLConstants;
import io.bcaas.constants.Constants;
import io.bcaas.vo.BtcUnspentOutputsResponse;
import io.bcaas.vo.BtcUtxo;
import io.bcaas.vo.TransactionVo;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockChainService {

    //Json工具类
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    //Http请求工具类
    private HttpClient httpClient = new HttpClient();
    //交易未确认区块list
    private List<BtcUtxo> btcUtxoList = new ArrayList<>();
    //发送交易tx参数
    private String transactionRaw;
    //transactionHash
    private String transactionHash;

    //获取余额
    public String getBalance(String address) {

        //拼接请求Url
        String apiUrl = APIURLConstants.API_BALANCE_URL + "?active=" + address;
        //HTTP发起请求
        String responseStr = httpClient.get(apiUrl);
        try {

            //解析返回的JSON,获取余额
            JSONObject responseJson = new JSONObject(responseStr);
            JSONObject balanceJson = responseJson.getJSONObject(address);

            //获取返回余额
            long final_balance = balanceJson.getLong("final_balance");
            //转换余额
            BigDecimal amount = new BigDecimal(final_balance).divide(new BigDecimal(Constants.BtcUnit));
            //替换显示到页面的余额
            balanceJson.put("final_balance", amount);

            //返回json格式
            return balanceJson.toString();
        } catch (Exception e) {
            return responseErrorJson();
        }
    }

    /**
     * 转账
     */
    public boolean send(TransactionVo transactionVo) {

        String feeStr = transactionVo.getFee().trim();
        String amountStr = transactionVo.getAmount().trim();
        String toAddress = transactionVo.getToAddress().trim();
        String privateKey = transactionVo.getPrivateKey().trim();
        String fromAddress = transactionVo.getFromAddress().trim();

        //获取当前钱包的未交易区块
        String unspentResponse = getUnspent(fromAddress);
        //返回字符去除空格和换行
        unspentResponse = BlockChainService.replaceSpecialStr(unspentResponse);
        //转Json格式
        BtcUnspentOutputsResponse unspentOutputsResponse = gson.fromJson(unspentResponse, BtcUnspentOutputsResponse.class);
        //保存返回未交易区块list
        btcUtxoList = unspentOutputsResponse.getUnspent_outputs();

        if (btcUtxoList == null || btcUtxoList.size()<=0) {
            Constants.LOGGER_INFO.info("当前账户无未交易区块!");
        	return false;
		}
        
        //排序UTXO  从大到小
        Collections.sort(btcUtxoList, (unspentOutput, t1) -> Long.compare(t1.getValue(), unspentOutput.getValue()));

        //清空tx字符串
        transactionRaw = null;
        //获取发送TX，赋值transactionRaw
        pushTX(feeStr, toAddress, amountStr, privateKey);
        //发送tx为空，返回失败
        if (StringUtils.isEmpty(transactionRaw)) {
            return false;
        }

        //拼接请求Url
        String apiUrl = APIURLConstants.API_PUSHTX_URL;
        //发送参数Map
        Map<String, Object> reqParams = new HashMap();
        //tx参数
        reqParams.put("tx", transactionRaw);
        //HTTP发起请求
        String responseStr = httpClient.post(apiUrl, reqParams);

        try {
            //是否返回交易成功字段
            if (StringUtils.contains(responseStr, Constants.SEND_RESPONSE_SUCCESS)) {
                return true;
            }
            //返回交易失败
            return false;
        } catch (Exception e) {
            Constants.LOGGER_INFO.info("发送交易失败,responseStr:{},Exception:{}",responseStr, e);
            return false;
        }

    }

    //获取当前钱包的未交易区块
    public String getUnspent(String address) {

        //拼接请求Url
        String apiUrl = APIURLConstants.API_UNSPENT_URL + "?active=" + address;
        //HTTP发起请求
        String responseStr = httpClient.get(apiUrl);
        try {

            //返回空，则没有未交易区块
            if (StringUtils.isEmpty(responseStr)) {
                return responseErrorJson();
            }
            //返回json格式
            return responseStr;

        } catch (Exception e) {
            return responseErrorJson();
        }
    }

    //获取交易记录
    public String getTransactionList(String address) {

        //拼接请求Url
        String apiUrl = APIURLConstants.API_GETRECORD_URL + "/" + address;
        //HTTP发起请求
        String responseStr = httpClient.get(apiUrl);
        try {

            //去除空格
            responseStr = replaceSpecialStr(responseStr);
            //解析返回的JSON
            JSONObject jsonObject = new JSONObject(responseStr);

            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseErrorJson();
    }

    /**
     * @param feeString    手续费
     * @param toAddress    收款地址
     * @param amountString 转账数量
     * @param privateKey   私钥
     */
    public void pushTX(String feeString, String toAddress, String amountString, String privateKey) {

        //判断当前是否有UTXO事务
        if (btcUtxoList == null) {
            Constants.LOGGER_INFO.info("当前账户无未交易区块");
            return;
        }
        //声明一个地址
        Address addressToSend = null;
        try {
            //将当前传入的地址转化成特定的地址格式
            addressToSend = Address.fromBase58(Constants.NetworkParameter, toAddress);
        } catch (AddressFormatException a) {
            a.printStackTrace();
        }

        //因为BTC内部的单位是「聪」10^8,所以，这里需要对刚传入的金额和利息参数进行换算
        BigDecimal amount = new BigDecimal(amountString).multiply(new BigDecimal(Constants.BtcUnit));
        BigDecimal fee;
        //判断当前用户是否给予手续费，否则采用本APP默认的手续费规则
        if (feeString == "" || feeString == null) {
            fee = new BigDecimal("0.005").multiply(new BigDecimal(Constants.BtcUnit));
        } else {
            fee = new BigDecimal(feeString).multiply(new BigDecimal(Constants.BtcUnit));
        }
        //判断当前单位换算过的fee是否满足要求，否则也按照给定的规则计算手续费
        if (fee.doubleValue() <= 0) {
            fee = new BigDecimal("0.005").multiply(new BigDecimal(Constants.BtcUnit));
        }
        //声明一个变量用于得到待会取出UTXO需要用来支付的的「输入」
        BigDecimal walletBtc = new BigDecimal("0.0");
        //获取当前设定环境下的Transaction实例
        Transaction transaction = new Transaction(Constants.NetworkParameter);
        //添加「输出」的金额以及地址信息
        transaction.addOutput(Coin.valueOf((amount.longValue())), addressToSend);
        //将当前交易的金额+手续费
        amount = amount.add(fee);
        //重新声明一个UTXO数组
        List<BtcUtxo> btcUnspentOutputList = new ArrayList<>();
        //遍历UTXO，得到当前地址的所有UTXO事务
        for (int i = 0; i < btcUtxoList.size(); i++) {
            BtcUtxo unspentOutput = btcUtxoList.get(i);
            //添加当前的UTXO里面的value
            walletBtc = walletBtc.add(new BigDecimal(unspentOutput.getValue()));
            //将当前数据新添加入新定义的UTXO数组里面
            btcUnspentOutputList.add(unspentOutput);
            //比较当前账户的btc是否大于等于这次需要push的金额
            if (walletBtc.doubleValue() >= amount.doubleValue()) {
                //如果是，判断当前是否是最后一条UTXO事务
                if (i < btcUtxoList.size() - 1) {
                    //如果不是，那么就将下一条数据也加入进来
                    walletBtc = walletBtc.add(new BigDecimal(btcUtxoList.get(i + 1).getValue()));
                    btcUnspentOutputList.add(btcUtxoList.get(i + 1));
                }
                break;
            }
        }
        Constants.LOGGER_INFO.info("walletBtc:" + walletBtc);
        Constants.LOGGER_INFO.info("amount:" + amount);

        //比较当前账户的balance的double值是否大于这次需要push的金额double值
        if (walletBtc.doubleValue() < amount.doubleValue()) {
            Constants.LOGGER_INFO.info("当前账户余额不足！");
            return;
        }
        //得到这次传送之后剩下的btc
        BigDecimal goBackBtc = walletBtc.subtract(amount);
        // 根据私鑰WIF字串轉ECKey
        ECKey ecKey = DumpedPrivateKey.fromBase58(Constants.NetworkParameter, privateKey).getKey();
        //判断当前剩下的btc不为0
        if (goBackBtc.doubleValue() != 0.0) {
            //添加「找零」的金额和地址
            transaction.addOutput(Coin.valueOf((goBackBtc.longValue())), ecKey.toAddress(Constants.NetworkParameter));
        }
        Constants.LOGGER_INFO.info("goBackBtc = " + goBackBtc);
        Constants.LOGGER_INFO.info("unspentOutputs.size :" + btcUnspentOutputList.size() + ";unspentOutputs: " + btcUnspentOutputList);

        //必须在使用bitcoinj之前构造一个Context对象
        Context.getOrCreate(MainNetParams.get());

        //对重新组装的UTXO进行遍历,进行交易签章
        for (BtcUtxo unspentOutput : btcUnspentOutputList) {
            if (unspentOutput.getValue() != 0.0) {
                // 获取交易输入TXId对应的交易数据
                Sha256Hash sha256Hash = new Sha256Hash(Utils.parseAsHexOrBase58(unspentOutput.getTx_hash_big_endian()));
                // 获取交易输入所对应的上一笔交易中的交易输出
                TransactionOutPoint outPoint = new TransactionOutPoint(Constants.NetworkParameter,
                        unspentOutput.getTx_output_n(), sha256Hash);
                //获取当前script进行格式解析
                Script script = new Script(Utils.parseAsHexOrBase58(unspentOutput.getScript()));
                Constants.LOGGER_INFO.info("script:" + unspentOutput.getScript());
                Constants.LOGGER_INFO.info("script2:" + script);
                Constants.LOGGER_INFO.info("addSignedInput getTxid:" + unspentOutput.getTx_hash_big_endian());
                Constants.LOGGER_INFO.info("addSignedInput getSatoshis:" + unspentOutput.getValue());
                //添加「交易」信息
                transaction.addSignedInput(outPoint, script, ecKey, Transaction.SigHash.ALL, true);
            }
        }
        transaction.getConfidence().setSource(TransactionConfidence.Source.SELF);
        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);

        Constants.LOGGER_INFO.info("transaction is:" + transaction);
        byte[] bytes = transaction.unsafeBitcoinSerialize();
        Constants.LOGGER_INFO.info("Transaction size = " + bytes.length);
        transactionRaw = Hex.toHexString(bytes);
        Constants.LOGGER_INFO.info("transactionRaw:" + transactionRaw);
        transactionHash = transaction.getHashAsString();
        Constants.LOGGER_INFO.info("transactionHash:" + transactionHash);
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符等
     *
     * @param str
     * @return
     */
    public static String replaceSpecialStr(String str) {
        String repl = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            repl = m.replaceAll("");
        }
        return repl;
    }

    //统一返回json格式
    public String responseErrorJson() {
        return new JSONObject().put("error", "Exception").toString();
    }
}
