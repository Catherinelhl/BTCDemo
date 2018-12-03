package io.bcaas.vo;

import java.util.ArrayList;
import java.util.List;

public class BtcUnspentOutputsResponse {

    private ArrayList<BtcUtxo> unspent_outputs;

    public ArrayList<BtcUtxo> getUnspent_outputs() {
        return unspent_outputs;
    }

    public void setUnspent_outputs(ArrayList<BtcUtxo> unspent_outputs) {
        this.unspent_outputs = unspent_outputs;
    }

    @Override
    public String toString() {
        return "BtcUnspentOutputsResponse{" +
                "unspent_outputs=" + unspent_outputs +
                '}';
    }

}
