package bcaasc.io.btcdemo.presenter;

import bcaasc.io.btcdemo.bean.BtcUnspentOutputsResponse;
import bcaasc.io.btcdemo.bean.BtcUtxo;
import bcaasc.io.btcdemo.contact.MainContact;
import bcaasc.io.btcdemo.http.MainInteractor;
import bcaasc.io.btcdemo.tool.LogTool;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.spongycastle.util.encoders.Hex;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/12
 */
public class MainPresenterImp implements MainContact.Presenter {

    private String TAG = MainPresenterImp.class.getSimpleName();

    private MainInteractor interactor = new MainInteractor();
    private String address = "mkkjcX4s4zoJJaE5E1NnfvsbuMvmzgBWTo";
    private List<BtcUtxo> btcUtxoList;
    private String privateWIFKey = "93AaWXJMutsyX5KPCXzGjK9uPm18ezP5jiFjcCtvZwELYX9LAkk";


    private BigDecimal mFeePerKb, mDelivery, mOldFee;
    private String mTransactionRaw, mTransactionHash;
    private List<BtcUtxo> mFromUtxo = new ArrayList<>();

    private MainContact.View view;

    public MainPresenterImp(MainContact.View view) {
        this.view = view;
    }

    @Override
    public void getBalance() {
        interactor.getBalance(address, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogTool.d(TAG, response.body());
                view.success(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LogTool.e(TAG, t.getMessage());
                view.success(t.getMessage());


            }
        });

    }

    /**
     * 获取BTC交易记录数据
     *
     * @return
     */
    @Override
    public void getTransactionList() {
        interactor.getTXList(address, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogTool.d(TAG, response.body());
                view.success(response.body());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LogTool.e(TAG, t.getMessage());
                view.success(t.getMessage());
            }
        });
    }

    /**
     * 获取未消费的输出
     *
     * @return
     */
    @Override
    public void getUnspent() {
        interactor.getUnspent(address, new Callback<BtcUnspentOutputsResponse>() {
            @Override
            public void onResponse(Call<BtcUnspentOutputsResponse> call, Response<BtcUnspentOutputsResponse> response) {
                BtcUnspentOutputsResponse unspentOutputsResponse = response.body();
                LogTool.d(TAG, response);

                if (unspentOutputsResponse != null) {
                    btcUtxoList = unspentOutputsResponse.getUnspent_outputs();
                    //排序UTXO  从大到小
                    Collections.sort(btcUtxoList, (unspentOutput, t1) -> Long.compare(t1.getValue(), unspentOutput.getValue()));
                    view.success(btcUtxoList.toString());
                    String feeString = "0.001";
                    String address = "mu41Bfy1RiGkW3KDDpj2ndr36VFad9ydau";
                    String amountString = "0.005";
                    pushTX(feeString, address, amountString);
                }

            }

            @Override
            public void onFailure(Call<BtcUnspentOutputsResponse> call, Throwable t) {
                LogTool.e(TAG, t.getMessage());
                view.success(t.getMessage());

            }
        });
    }

    /**
     * 转账BTC方法
     *
     * @param feeString    手续费
     * @param toAddress    收款地址
     * @param amountString 转账数量
     * @return
     */
    @Override
    public void pushTX(String feeString, String toAddress, String amountString) {
        if (btcUtxoList == null) {
            return;
        }
        Context.getOrCreate(TestNet3Params.get());
        Transaction transaction = new Transaction(TestNet3Params.get());
        Address addressToSend = null;
        try {
            addressToSend = Address.fromBase58(TestNet3Params.get(), toAddress);
        } catch (AddressFormatException a) {
            LogTool.e(TAG, a.getMessage());
        }
        BigDecimal amount = new BigDecimal(amountString).multiply(new BigDecimal("100000000"));
        mOldFee = new BigDecimal(feeString).multiply(new BigDecimal("100000000"));
        if (mOldFee.doubleValue() <= 0) {
            mOldFee = new BigDecimal("0.005").multiply(new BigDecimal("100000000"));
        }
        BigDecimal overFlow = new BigDecimal("0.0");
        transaction.addOutput(Coin.valueOf((amount.longValue())), addressToSend);

        amount = amount.add(mOldFee);
        List<BtcUtxo> btcUnspentOutputList = new ArrayList<>();
        for (int i = 0; i < btcUtxoList.size(); i++) {
            BtcUtxo unspentOutput = btcUtxoList.get(i);
            overFlow = overFlow.add(new BigDecimal(unspentOutput.getValue()));
            btcUnspentOutputList.add(unspentOutput);
            if (overFlow.doubleValue() >= amount.doubleValue()) {
                if (i < btcUtxoList.size() - 1) {
                    overFlow = overFlow.add(new BigDecimal(btcUtxoList.get(i + 1).getValue()));
                    btcUnspentOutputList.add(btcUtxoList.get(i + 1));
                }
                break;
            }
        }
        LogTool.d(TAG, "overFlow:" + overFlow);
        LogTool.d(TAG, "amount:" + amount);
        if (overFlow.doubleValue() < amount.doubleValue()) {
            LogTool.e(TAG, "insufficient_transaction");
        }
        mDelivery = overFlow.subtract(amount);
        // 私鑰WIF字串轉ECKey
        ECKey privateKey = DumpedPrivateKey.fromBase58(TestNet3Params.get(), privateWIFKey).getKey();

//        ECKey currentKey = KeyStorage.getInstance().getBtcDeterministicKeyBySeedAndAddress(mSeed);
        if (mDelivery.doubleValue() != 0.0) {
            transaction.addOutput(Coin.valueOf((mDelivery.longValue())), privateKey.toAddress(TestNet3Params.get()));
        }
        LogTool.d("delivery = " + mDelivery);
        LogTool.d(TAG, "unspentOutputs.size = " + btcUtxoList.size());
        mFromUtxo.clear();
        for (BtcUtxo unspentOutput : btcUnspentOutputList) {
            if (unspentOutput.getValue() != 0.0) {
                Sha256Hash sha256Hash = new Sha256Hash(Utils.parseAsHexOrBase58(unspentOutput.getTx_hash_big_endian()));
                TransactionOutPoint outPoint = new TransactionOutPoint(TestNet3Params.get(), unspentOutput.getTx_output_n(), sha256Hash);
                Script script = new Script(Utils.parseAsHexOrBase58(unspentOutput.getScript()));
                LogTool.d(TAG, "addSignedInput getTxid>>" + unspentOutput.getTx_hash_big_endian());
                LogTool.d(TAG, "addSignedInput getSatoshis>>" + unspentOutput.getValue());
//                DeterministicKey deterministicKey = KeyStorage.getInstance().getBtcDeterministicKeyBySeedAndAddress(mSeed);
                transaction.addSignedInput(outPoint, script, privateKey, Transaction.SigHash.ALL, true);
                mFromUtxo.add(unspentOutput);
            }
        }
        transaction.getConfidence().setSource(TransactionConfidence.Source.SELF);
        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);

        byte[] bytes = transaction.unsafeBitcoinSerialize();
        LogTool.d(TAG, "Transaction size = " + bytes.length);
        int txSizeInkB = (int) Math.ceil(bytes.length / 1024.);
//        BigDecimal minimumFee = mFeePerKb.multiply(new BigDecimal(txSizeInkB));
//                        if (minimumFee.doubleValue() > mOldFee.doubleValue()) {
//                            String error = CommonUtility.formatString(minimumFee.toString(), " QTUM");
//                            String errorValue = String.format(mContext.getString(R.string.insufficient_fee_tips), error, error);
//                            ExceptionHandle.ResponseThrowable responseThrowable = new ExceptionHandle.ResponseThrowable(errorValue, TRANSFER_ERROR_ONE, minimumFee, amountString, toAddress);
//                            throw responseThrowable;
//                        }
        mTransactionRaw = Hex.toHexString(bytes);
        mTransactionHash = transaction.getHashAsString();
        interactor.pushTX(mTransactionRaw, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Transaction Submitted
                LogTool.d(TAG, response.body());
                view.success(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable responseThrowable) {
                LogTool.e(TAG, responseThrowable.getMessage());
                String message = responseThrowable.getMessage();
                view.failure(responseThrowable.getMessage());

//                int errorCode = responseThrowable.;
//                LogTool.e(TAG, "Error Message = " + message);
//                LogTool.e(TAG, "Error Code = " + errorCode);
//                if (message != null) {
//                    if (errorCode == TRANSFER_ERROR_ONE) {
//                        e.onError(responseThrowable);
//                    } else {
//                        try {
//                            JSONObject jsonObject = new JSONObject(message);
//                            JSONObject errorObject = jsonObject.optJSONObject("error");
//                            if (errorObject != null) {
//                                int code = errorObject.optInt("code");
//                                String errorMessage = errorObject.optString("message");
//                                if (code == -26) {
//                                    BigDecimal newFee = mDelivery.add(mOldFee);
//                                    String error = mDelivery.toPlainString();
//                                    String errorValue = String.format(mContext.getString(R.string.transfer_error_too_low_value), error);
//                                    LogTool.e(TAG, "转账剩余qtum太低  value = " + mDelivery + " oldFee = " + mOldFee + " 新的fee = " + newFee);
//                                    ExceptionHandle.ResponseThrowable responseThrowable1 = new ExceptionHandle.ResponseThrowable(errorValue, TRANSFER_ERROR_TWO, newFee, amountString, toAddress);
//                                    e.onError(responseThrowable1);
//                                } else {
//                                    e.onError(new ExceptionHandle.ResponseThrowable(errorMessage, TRANSFER_ERROR_TWO));
//                                }
//                                TransactionTokenViewModel.sendTransferError(mContext, TAG, CommonUtility.formatString(code), errorMessage, feeString, mFeePerKb.toString(),
//                                        getFromUtxo(), "0", "0", mTransactionHash, mTransactionRaw);
//                            }
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                            e.onError(new ExceptionHandle.ResponseThrowable(message, TRANSFER_ERROR_THREE));
//                        }
//                    }
//                } else {
//                    e.onError(new ExceptionHandle.ResponseThrowable(mContext.getString(R.string.failed), TRANSFER_ERROR_FOUR));
//                }
            }
        });
    }
}
