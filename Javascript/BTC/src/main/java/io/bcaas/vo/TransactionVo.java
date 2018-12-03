package io.bcaas.vo;

/**
 * * @param toAddress   发送地址
 * * @param fromAddress 首款地址
 * * @param amount      交易金额
 * * @param fee         手续费
 * * @param privateKey  私钥
 */
public class TransactionVo {

    String toAddress;
    String fromAddress;
    String amount;
    String fee;
    String privateKey;

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
