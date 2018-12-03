package io.bcaas.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.bcaas.constants.Constants;
import io.bcaas.services.BlockChainService;
import io.bcaas.vo.BtcUnspentOutputsResponse;
import io.bcaas.vo.BtcUtxo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockChainTest {

    private static List<BtcUtxo> btcUtxoList = new ArrayList<>();

    public static void main(String[] args) {
        BigDecimal fee = new BigDecimal("0.0001").multiply(new BigDecimal(Constants.BtcUnit));
        System.out.println(fee);

        String address = "1PmR1EUzWdygApeuNX5WU9KqdwfEYjzzqp";

        BlockChainService blockChainService = new BlockChainService();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//
//        //获取当前钱包的未交易区块
//        String unspentResponse = blockChainService.getUnspent(address);
//
//        //去除空格和换行
//        unspentResponse = BlockChainService.replaceSpecialStr(unspentResponse);
//
//        BtcUnspentOutputsResponse unspentOutputsResponse = gson.fromJson(unspentResponse, BtcUnspentOutputsResponse.class);
//
//        btcUtxoList = unspentOutputsResponse.getUnspent_outputs();
//
//        //排序UTXO  从大到小
//        Collections.sort(btcUtxoList, (unspentOutput, t1) -> Long.compare(t1.getValue(), unspentOutput.getValue()));
//
//        //组装发送数据
//        blockChainService.pushTX("0.00001", "1Ag8vD6axmhXAFEPZkBFxcwUoW1Wsm25qR",
//                "0.0001", "5JR6FD5sFVxmbLFWUGEbQCybz4dxEmxNYXppwxQDjqhUyccSW1Z");
//
//       //发送
//        blockChainService.pushtx(transactionRaw);

//        Client client = ClientBuilder.newClient();
//
//        //获取
//        Response response = client.target("https://blockchain.info/unconfirmed-transactions?format=json")
//                                  .request(MediaType.TEXT_PLAIN_TYPE)
//                                  .get();

        //获取代签收区块
//        Response response = client.target("https://blockchain.info/unspent?active=1PmR1EUzWdygApeuNX5WU9KqdwfEYjzzqp")
//                                  .request(MediaType.TEXT_PLAIN_TYPE)
//                                  .get();

//        //输出响应数据
//        System.out.println("status: " + response.getStatus());
//        System.out.println("headers: " + response.getHeaders());
//        System.out.println("body:" + response.readEntity(String.class));

//        //查询余额
        blockChainService.getBalance(address);
//

//        //获取交易记录
//        blockChainService.getTransactionList("1PmR1EUzWdygApeuNX5WU9KqdwfEYjzzqp");
    }

}
