package io.bcaas.vo;

public class BalanceVo {

    private long final_balance;

    private long n_tx;

    private long total_received;

    public long getFinal_balance() {
        return final_balance;
    }

    public void setFinal_balance(long final_balance) {
        this.final_balance = final_balance;
    }

    public long getN_tx() {
        return n_tx;
    }

    public void setN_tx(long n_tx) {
        this.n_tx = n_tx;
    }

    public long getTotal_received() {
        return total_received;
    }

    public void setTotal_received(long total_received) {
        this.total_received = total_received;
    }
}

