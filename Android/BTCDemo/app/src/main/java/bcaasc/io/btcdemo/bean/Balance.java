package bcaasc.io.btcdemo.bean;

import com.google.gson.JsonObject;

import java.io.Serializable;


/**
 * 數據類：獲取當前錢包的餘額
 */
public class Balance implements Serializable {

    private long finalBalance;
    private long txCount;
    private long totalReceived;

    public Balance(long finalBalance, long txCount, long totalReceived) {
        this.finalBalance = finalBalance;
        this.txCount = txCount;
        this.totalReceived = totalReceived;
    }

    public Balance(JsonObject json) {

        this(
                json.has("final_balance") ? json.get("final_balance").getAsLong() : 0,
                json.has("n_tx") ? json.get("n_tx").getAsLong() : 0,
                json.has("total_received") ? json.get("total_received").getAsLong() : 0
        );
    }

    public long getFinalBalance() {
        return finalBalance;
    }

    public long getTxCount() {
        return txCount;
    }

    public long getTotalReceived() {
        return totalReceived;
    }

    public void setFinalBalance(long final_balance) {
        this.finalBalance = final_balance;
    }

    public void setTxCount(long n_tx) {
        this.txCount = n_tx;
    }

    public void setTotalReceived(long total_received) {
        this.totalReceived = total_received;
    }
}
