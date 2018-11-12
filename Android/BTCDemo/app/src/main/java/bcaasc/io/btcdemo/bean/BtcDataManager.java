package bcaasc.io.btcdemo.bean;

import android.annotation.SuppressLint;
import bcaasc.io.btcdemo.tool.LogTool;
import io.reactivex.Observable;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

public class BtcDataManager {


    private static String TAG;



//    private static class BTCWDataHolder {
//        @SuppressLint("StaticFieldLeak")
//        private static final BtcDataManager INSTANCE = new BtcDataManager();
//    }
//
//    /**
//     * 获取BTC交易记录数据
//     *
//     * @return
//     */
//    public Observable<List<BtcTransactionHistory>> getBtcTransactionHistory() {
//        return Observable.create(e -> BtcService.newInstance().getBtcTransactionHistoryList(BtcKeyStorage.getInstance().getBtcAddress())
//                .subscribe(btcTransactionResponse -> {
//                    System.out.println("btc history = " + btcTransactionResponse.getHash160());
//                    e.onNext(btcTransactionResponse.getTxs());
//                    e.onComplete();
//                }, throwable -> {
//                    throwable.printStackTrace();
//                    e.onNext(new ArrayList<>());
//                    e.onComplete();
//                }));
//
//    }
//
//    private BigDecimal mFeePerKb, mDelivery, mOldFee;
//    private String mTransactionRaw, mTransactionHash, mSeed;
//    private List<BtcUtxo> mFromUtxo = new ArrayList<>();
//    public static final int TRANSFER_ERROR_ONE = -1, TRANSFER_ERROR_TWO = -2, TRANSFER_ERROR_THREE = -3, TRANSFER_ERROR_FOUR = -4, TRANSFER_ERROR_FIVE = -5;
//
//    public String getFromUtxo() {
//        return CommonUtility.JSONObjectUtility.GSON.toJson(mFromUtxo);
//    }
//
//    /**
//     * 转账BTC方法
//     *
//     * @param feeString    手续费
//     * @param toAddress    收款地址
//     * @param amountString 转账数量
//     * @param pin          用户密码
//     * @return
//     */
//    public Observable<String> transactionBtc(String feeString, String toAddress, String amountString, String pin) {
//        return Observable.create(e -> {
//            QbaoEncryptHelper.getBrainCode(BCAASApplication.context(), pin)
//                    .flatMap(seed -> {
//                        mSeed = seed;
//                        return QbaoService.newInstance().getEstimateFeePerKb(2);
//                    })
//                    .flatMap(feePerKb -> {
////                        mFeePerKb = feePerKb.getFeePerKb();
//                        mFeePerKb = new BigDecimal("0.0001");
//                        return getUnSpentOutputs();
//                    })
//                    .flatMap(unspentOutputs -> {
//                        Transaction transaction = new Transaction(TestNet3Params.get());
//                        Address addressToSend = null;
//                        try {
//                            addressToSend = Address.fromBase58(TestNet3Params.get(), toAddress);
//                        } catch (AddressFormatException a) {
//                            LogTool.e(TAG, "incorrect_address:" + a.getMessage());
////                            throw new ExceptionHandle.ResponseThrowable(BCAASApplication.context().getString(R.string.incorrect_address), TRANSFER_ERROR_TWO);
//                        }
//                        BigDecimal amount = new BigDecimal(amountString);
////                        mOldFee = new BigDecimal(feeString).multiply(com.qbao.library.utility.Constants.BIT_COIN);
////                        if (mOldFee.doubleValue() <= 0) {
////                            mOldFee = new BigDecimal("0.005").multiply(com.qbao.library.utility.Constants.BIT_COIN);
////                        }
//                        BigDecimal overFlow = new BigDecimal("0.0");
//                        transaction.addOutput(Coin.valueOf((amount.longValue())), addressToSend);
//
//                        amount = amount.add(mOldFee);
//                        List<BtcUtxo> btcUnspentOutputList = new ArrayList<>();
//                        for (int i = 0; i < unspentOutputs.size(); i++) {
//                            BtcUtxo unspentOutput = unspentOutputs.get(i);
//                            overFlow = overFlow.add(new BigDecimal(unspentOutput.getValue()));
//                            btcUnspentOutputList.add(unspentOutput);
//                            if (overFlow.doubleValue() >= amount.doubleValue()) {
//                                if (i < unspentOutputs.size() - 1) {
//                                    overFlow = overFlow.add(new BigDecimal(unspentOutputs.get(i + 1).getValue()));
//                                    btcUnspentOutputList.add(unspentOutputs.get(i + 1));
//                                }
//                                break;
//                            }
//                        }
////                        if (overFlow.doubleValue() < amount.doubleValue()) {
////                            throw new ExceptionHandle.ResponseThrowable(mContext.getString(R.string.insufficient_transaction), TRANSFER_ERROR_TWO);
////                        }
//                        mDelivery = overFlow.subtract(amount);
//                        ECKey currentKey = KeyStorage.getInstance().getBtcDeterministicKeyBySeedAndAddress(mSeed);
//                        if (mDelivery.doubleValue() != 0.0) {
//                            transaction.addOutput(Coin.valueOf((mDelivery.longValue())), currentKey.toAddress(Constants.NETWORK_PARAMETERS));
//                        }
//                        LogTool.e("delivery = " + mDelivery);
//                        LogTool.e(TAG, "unspentOutputs.size = " + unspentOutputs.size());
//                        mFromUtxo.clear();
//                        for (BtcUtxo unspentOutput : btcUnspentOutputList) {
//                            if (unspentOutput.getValue() != 0.0) {
//                                Sha256Hash sha256Hash = new Sha256Hash(Utils.parseAsHexOrBase58(unspentOutput.getTx_hash_big_endian()));
//                                TransactionOutPoint outPoint = new TransactionOutPoint(Constants.NETWORK_PARAMETERS, unspentOutput.getTx_output_n(), sha256Hash);
//                                Script script = new Script(Utils.parseAsHexOrBase58(unspentOutput.getScript()));
//                                System.out.println( "addSignedInput getTxid>>" + unspentOutput.getTx_hash_big_endian());
//                                System.out.println("addSignedInput getSatoshis>>" + unspentOutput.getValue());
//                                DeterministicKey deterministicKey = KeyStorage.getInstance().getBtcDeterministicKeyBySeedAndAddress(mSeed);
//                                transaction.addSignedInput(outPoint, script, deterministicKey, Transaction.SigHash.ALL, true);
//                                mFromUtxo.add(unspentOutput);
//                            }
//                        }
//                        transaction.getConfidence().setSource(TransactionConfidence.Source.SELF);
//                        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);
//
//                        byte[] bytes = transaction.unsafeBitcoinSerialize();
//                        System.out.println( "Transaction size = " + bytes.length);
//                        int txSizeInkB = (int) Math.ceil(bytes.length / 1024.);
//                        BigDecimal minimumFee = mFeePerKb.multiply(new BigDecimal(txSizeInkB));
////                        if (minimumFee.doubleValue() > mOldFee.doubleValue()) {
////                            String error = CommonUtility.formatString(minimumFee.toString(), " QTUM");
////                            String errorValue = String.format(mContext.getString(R.string.insufficient_fee_tips), error, error);
////                            ExceptionHandle.ResponseThrowable responseThrowable = new ExceptionHandle.ResponseThrowable(errorValue, TRANSFER_ERROR_ONE, minimumFee, amountString, toAddress);
////                            throw responseThrowable;
////                        }
//                        mTransactionRaw = Hex.toHexString(bytes);
//                        mTransactionHash = transaction.getHashAsString();
//                        return BtcService.newInstance().sendRawTransaction(mTransactionRaw);
//                    })
//                    .subscribe(new Rx2Subscriber<String>(mContext, TAG, Rx2Subscriber.CHECK_NET) {
//
//                        @Override
//                        public void onNext(String sendRawTransactionResponse) {
//                            e.onNext(sendRawTransactionResponse);
//                            e.onComplete();
//                        }
//
//                        @Override
//                        public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
//                            String message = responseThrowable.message;
//                            int errorCode = responseThrowable.code;
//                            LogTool.e(TAG, "Error Message = " + message);
//                            LogTool.e(TAG, "Error Code = " + errorCode);
//                            if (!StringTool.isEmpty(message)) {
//                                if (errorCode == TRANSFER_ERROR_ONE) {
//                                    e.onError(responseThrowable);
//                                } else {
//                                    try {
//                                        JSONObject jsonObject = new JSONObject(message);
//                                        JSONObject errorObject = jsonObject.optJSONObject("error");
//                                        if (errorObject != null) {
//                                            int code = errorObject.optInt("code");
//                                            String errorMessage = errorObject.optString("message");
//                                            if (code == -26) {
//                                                BigDecimal newFee = mDelivery.add(mOldFee);
//                                                String error = mDelivery.toPlainString();
//                                                String errorValue = String.format(mContext.getString(R.string.transfer_error_too_low_value), error);
//                                                System.out.println("转账剩余qtum太低  value = " + mDelivery + " oldFee = " + mOldFee + " 新的fee = " + newFee);
//                                                ExceptionHandle.ResponseThrowable responseThrowable1 = new ExceptionHandle.ResponseThrowable(errorValue, TRANSFER_ERROR_TWO, newFee, amountString, toAddress);
//                                                e.onError(responseThrowable1);
//                                            } else {
//                                                e.onError(new ExceptionHandle.ResponseThrowable(errorMessage, TRANSFER_ERROR_TWO));
//                                            }
//                                            TransactionTokenViewModel.sendTransferError(BCAASApplication.context(), TAG, CommonUtility.formatString(code), errorMessage, feeString, mFeePerKb.toString(),
//                                                    getFromUtxo(), "0", "0", mTransactionHash, mTransactionRaw);
//                                        }
//                                    } catch (JSONException e1) {
//                                        e1.printStackTrace();
////                                        e.onError(new ExceptionHandle.ResponseThrowable(message, TRANSFER_ERROR_THREE));
//                                    }
//                                }
//                            } else {
////                                e.onError(new ExceptionHandle.ResponseThrowable(mContext.getString(R.string.failed), TRANSFER_ERROR_FOUR));
//                            }
//
//                        }
//                    });
//        });
//
//    }
//
//    private Observable<List<BtcUtxo>> getUnSpentOutputs() {
//
//        String address= "mkkjcX4s4zoJJaE5E1NnfvsbuMvmzgBWTo";
//        String utxo="{\"block_height\":-1,\"block_index\":-1,\"hash\":\"db403f16fe9786fbf478d1e77b7b6c3ec56223cc81e0cf66561525510cba7e48\",\"addresses\":[\"myTDfZjgcnnZN6mj6irxBxVujWNTQ2mptL\",\"mu41Bfy1RiGkW3KDDpj2ndr36VFad9ydau\"],\"total\":21991900,\"fees\":8100,\"size\":119,\"preference\":\"high\",\"relayed_by\":\"47.74.0.254\",\"received\":\"2018-11-12T03:48:13.407196904Z\",\"ver\":1,\"double_spend\":false,\"vin_sz\":1,\"vout_sz\":2,\"confirmations\":0,\"inputs\":[{\"prev_hash\":\"23c588e1740ebf43134e5668099674537faf0d4e85d475af9d075c608633d900\",\"output_index\":0,\"output_value\":22000000,\"sequence\":4294967295,\"addresses\":[\"myTDfZjgcnnZN6mj6irxBxVujWNTQ2mptL\"],\"script_type\":\"pay-to-pubkey-hash\",\"age\":1443040}],\"outputs\":[{\"value\":11000000,\"script\":\"76a9149478f1436ea04b3093f24bc93d83f00f7025f7c988ac\",\"addresses\":[\"mu41Bfy1RiGkW3KDDpj2ndr36VFad9ydau\"],\"script_type\":\"pay-to-pubkey-hash\"},{\"value\":10991900,\"script\":\"76a914c4bd6e64f09401db45bad7ab91f07151888eb84a88ac\",\"addresses\":[\"myTDfZjgcnnZN6mj6irxBxVujWNTQ2mptL\"],\"script_type\":\"pay-to-pubkey-hash\"}]}";
//        return Observable.create(e -> BtcService.newInstance().getBTCUTXO(BtcKeyStorage.getInstance().getBtcAddress())
//                .flatMap(btcUnspentOutputsResponse -> {
//                    List<BtcUtxo> BTCUTXOList = btcUnspentOutputsResponse.getUnspent_outputs();
//                    //排序UTXO  从大到小
//                    Collections.sort(BTCUTXOList, (unspentOutput, t1) -> Long.compare(t1.getValue(), unspentOutput.getValue()));
//                    return Observable.just(BTCUTXOList);
//                })
//                .subscribe(new Rx2Subscriber<List<BtcUtxo>>(BcaasAddressDAO, TAG) {
//                    @Override
//                    public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
//                        e.onError(responseThrowable);
//                    }
//
//                    @Override
//                    public void onNext(List<BtcUtxo> unspentOutputs) {
//                        e.onNext(unspentOutputs);
//                        e.onComplete();
//                    }
//                }));
//
//
//    }
}
