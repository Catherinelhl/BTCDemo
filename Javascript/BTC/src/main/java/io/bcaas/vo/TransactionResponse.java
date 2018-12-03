package io.bcaas.vo;

import java.util.List;

public class TransactionResponse {
    private String hash160;
    private String address;
    private long n_tx;
    private long total_received;
    private long total_sent;
    private long final_balance;
    private List<Transaction> txs;


}
