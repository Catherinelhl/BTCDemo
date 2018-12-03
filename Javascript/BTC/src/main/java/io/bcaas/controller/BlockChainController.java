package io.bcaas.controller;

import io.bcaas.constants.Constants;
import io.bcaas.services.BlockChainService;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.vo.TransactionVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class BlockChainController {

    //BlockChainService
    private BlockChainService blockChainService = new BlockChainService();

    /**
     * 获取余额
     *
     * @param address 比特币地址
     * @return
     */
    @PostMapping(value = "/getBalance")
    public String getBalance(@RequestParam("address") String address) {
        Constants.LOGGER_INFO.info("getBalance address:" + address);

        //返回余额jsonStr
        String response = blockChainService.getBalance(address);

        return response;
    }


    /**
     * 发送交易
     *
     * @param transactionVo
     * @return
     */
    @PostMapping(value = "/pushTX")
    public boolean pushTX(TransactionVo transactionVo) {

        //获取参数，进行参数验证
        String feeStr = transactionVo.getFee().trim();
        String amountStr = transactionVo.getAmount().trim();
        String toAddress = transactionVo.getToAddress().trim();

        //验证金额格式是否正确
        try {
            new BigDecimal(feeStr).multiply(new BigDecimal(Constants.BtcUnit));
            new BigDecimal(amountStr).multiply(new BigDecimal(Constants.BtcUnit));
        } catch (Exception e) {
            Constants.LOGGER_INFO.info("发送金额或手续费金额格式错误！feeStr:{},amountStr:{}" ,feeStr,amountStr );
            return false;
        }

        //验证交易地址格式
        try {
            if (!KeyTool.validateBitcoinAddress(toAddress)) {
                Constants.LOGGER_INFO.info("交易地址格式错误！");
                return false;
            }
        } catch (Exception e) {
            Constants.LOGGER_INFO.info("验证交易地址格式异常！");
            return false;
        }

        //返回发送是否成功
        boolean isSendSuccess = blockChainService.send(transactionVo);

        return isSendSuccess;
    }

    /**
     * 获取交易列表信息
     *
     * @param address 比特币地址
     * @return
     */
    @PostMapping(value = "/getTransactionList")
    public String getTransactionList(@RequestParam("address") String address) {

        //获取交易记录
        String transactionList = blockChainService.getTransactionList(address);

        return transactionList;
    }


}
