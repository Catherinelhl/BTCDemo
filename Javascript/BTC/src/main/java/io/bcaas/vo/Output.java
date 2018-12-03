package io.bcaas.vo;

public class Output {
    private int n;
    private long value;
    private String address;
    private long txIndex;
    private String script;
    private boolean spent;
    private boolean spentToAddress;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(long txIndex) {
        this.txIndex = txIndex;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public boolean isSpent() {
        return spent;
    }

    public void setSpent(boolean spent) {
        this.spent = spent;
    }

    public boolean isSpentToAddress() {
        return spentToAddress;
    }

    public void setSpentToAddress(boolean spentToAddress) {
        this.spentToAddress = spentToAddress;
    }
}
