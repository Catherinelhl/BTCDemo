package bcaasc.io.btcdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a transaction.
 */
public class Transaction implements Serializable {
    private String hash;
    private long ver;
    private long vin_sz;
    private long vout_sz;
    private long lock_time;
    private long size;
    private String relayed_by;
    private long block_height;
    private long tx_index;
    private List<Input> inputs;
    private List<Output> outputs;
    private boolean doubleSpend;
    private long time;
    private int version;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getVer() {
        return ver;
    }

    public void setVer(long ver) {
        this.ver = ver;
    }

    public long getVin_sz() {
        return vin_sz;
    }

    public void setVin_sz(long vin_sz) {
        this.vin_sz = vin_sz;
    }

    public long getVout_sz() {
        return vout_sz;
    }

    public void setVout_sz(long vout_sz) {
        this.vout_sz = vout_sz;
    }

    public long getLock_time() {
        return lock_time;
    }

    public void setLock_time(long lock_time) {
        this.lock_time = lock_time;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getRelayed_by() {
        return relayed_by;
    }

    public void setRelayed_by(String relayed_by) {
        this.relayed_by = relayed_by;
    }

    public long getBlock_height() {
        return block_height;
    }

    public void setBlock_height(long block_height) {
        this.block_height = block_height;
    }

    public long getTx_index() {
        return tx_index;
    }

    public void setTx_index(long tx_index) {
        this.tx_index = tx_index;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    public boolean isDoubleSpend() {
        return doubleSpend;
    }

    public void setDoubleSpend(boolean doubleSpend) {
        this.doubleSpend = doubleSpend;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "hash='" + hash + '\'' +
                ", ver=" + ver +
                ", vin_sz=" + vin_sz +
                ", vout_sz=" + vout_sz +
                ", lock_time=" + lock_time +
                ", size=" + size +
                ", relayed_by='" + relayed_by + '\'' +
                ", block_height=" + block_height +
                ", tx_index=" + tx_index +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                ", doubleSpend=" + doubleSpend +
                ", time=" + time +
                ", version=" + version +
                '}';
    }
}
