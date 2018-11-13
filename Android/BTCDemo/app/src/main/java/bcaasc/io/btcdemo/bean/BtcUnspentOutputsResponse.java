package bcaasc.io.btcdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/13
 *
 * 數據類：獲取當前所有的UTXO事務
 */
public class BtcUnspentOutputsResponse implements Serializable {

    private List<BtcUtxo> unspent_outputs;

    public List<BtcUtxo> getUnspent_outputs() {
        return unspent_outputs;
    }

    public void setUnspent_outputs(List<BtcUtxo> unspent_outputs) {
        this.unspent_outputs = unspent_outputs;
    }

    @Override
    public String toString() {
        return "BtcUnspentOutputsResponse{" +
                "unspent_outputs=" + unspent_outputs +
                '}';
    }
}
