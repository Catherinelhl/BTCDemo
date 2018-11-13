package bcaasc.io.btcdemo.presenter;

import bcaasc.io.btcdemo.bean.BtcUnspentOutputsResponse;
import bcaasc.io.btcdemo.bean.BtcUtxo;
import bcaasc.io.btcdemo.constants.BTCParamsConstants;
import bcaasc.io.btcdemo.contact.MainContact;
import bcaasc.io.btcdemo.http.MainInteractor;
import bcaasc.io.btcdemo.tool.LogTool;
import org.bitcoinj.core.*;
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
    private MainContact.View view;


    private BigDecimal feePerKb, delivery, oldFee;
    private String transactionRaw, transactionHash;


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
     * 获取UTXO事务
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
        //判断当前是否有UTXO事务
        if (btcUtxoList == null) {
            return;
        }
        //You must construct a Context object before using BitCoin j!
        Context.getOrCreate(BTCParamsConstants.NetworkParameter);
        //获取当前设定环境下的Transaction实例
        Transaction transaction = new Transaction(BTCParamsConstants.NetworkParameter);
        //声明一个地址
        Address addressToSend = null;
        try {
            //将当前传入的地址转化成特定的地址格式
            addressToSend = Address.fromBase58(BTCParamsConstants.NetworkParameter, toAddress);
        } catch (AddressFormatException a) {
            LogTool.e(TAG, a.getMessage());
        }
        //因为BTC内部的单位是「聪」10^8,所以，这里需要对刚传入的金额和利息参数进行换算
        BigDecimal amount = new BigDecimal(amountString).multiply(new BigDecimal("100000000"));
        oldFee = new BigDecimal(feeString).multiply(new BigDecimal("100000000"));
        if (oldFee.doubleValue() <= 0) {
            oldFee = new BigDecimal("0.005").multiply(new BigDecimal("100000000"));
        }

        //声明一个变量用于存储待会UTXO需要用的的账户金额
        BigDecimal walletBalance = new BigDecimal("0.0");
        transaction.addOutput(Coin.valueOf((amount.longValue())), addressToSend);
        //将当前交易的金额+手续费
        amount = amount.add(oldFee);
        //重新声明一个UTXO数组
        List<BtcUtxo> btcUnspentOutputList = new ArrayList<>();
        //遍历UTXO，得到当前地址的所有unspent
        for (int i = 0; i < btcUtxoList.size(); i++) {
            BtcUtxo unspentOutput = btcUtxoList.get(i);
            //添加当前的UTXO里面的value
            walletBalance = walletBalance.add(new BigDecimal(unspentOutput.getValue()));
            //将当前数据新添加入新定义的UTXO数组里面
            btcUnspentOutputList.add(unspentOutput);
            //比较当前账户的balance是否大于等于这次需要push的金额
            if (walletBalance.doubleValue() >= amount.doubleValue()) {
                //如果是，判断当前是否是最后一条UTXO事务
                if (i < btcUtxoList.size() - 1) {
                    //如果不是，那么就将下一条数据也加入进来
                    walletBalance = walletBalance.add(new BigDecimal(btcUtxoList.get(i + 1).getValue()));
                    btcUnspentOutputList.add(btcUtxoList.get(i + 1));
                }
                break;
            }
        }
        LogTool.d(TAG, "walletBalance:" + walletBalance);
        LogTool.d(TAG, "amount:" + amount);
        //比较当前账户的balance是否大于这次需要push的金额
        if (walletBalance.doubleValue() < amount.doubleValue()) {
            LogTool.e(TAG, "insufficient_transaction");
        }
        //得到这次传送之后剩下的balance
        delivery = walletBalance.subtract(amount);
        // 根据私鑰WIF字串轉ECKey
        ECKey privateKey = DumpedPrivateKey.fromBase58(BTCParamsConstants.NetworkParameter, privateWIFKey).getKey();

//        ECKey currentKey = KeyStorage.getInstance().getBtcDeterministicKeyBySeedAndAddress(mSeed);
        //判断当前剩下的balance不为0
        if (delivery.doubleValue() != 0.0) {
            //将这部分再传给自己
            transaction.addOutput(Coin.valueOf((delivery.longValue())), privateKey.toAddress(BTCParamsConstants.NetworkParameter));
        }
        LogTool.d("delivery = " + delivery);
        LogTool.d(TAG, "unspentOutputs.size = " + btcUtxoList.size());
        List<BtcUtxo> fromUtxo = new ArrayList<>();
        //对重新组装的UTXO进行遍历
        for (BtcUtxo unspentOutput : btcUnspentOutputList) {
            if (unspentOutput.getValue() != 0.0) {
                Sha256Hash sha256Hash = new Sha256Hash(Utils.parseAsHexOrBase58(unspentOutput.getTx_hash_big_endian()));
                TransactionOutPoint outPoint = new TransactionOutPoint(BTCParamsConstants.NetworkParameter, unspentOutput.getTx_output_n(), sha256Hash);
                Script script = new Script(Utils.parseAsHexOrBase58(unspentOutput.getScript()));
                LogTool.d(TAG, "addSignedInput getTxid>>" + unspentOutput.getTx_hash_big_endian());
                LogTool.d(TAG, "addSignedInput getSatoshis>>" + unspentOutput.getValue());
//                DeterministicKey deterministicKey = KeyStorage.getInstance().getBtcDeterministicKeyBySeedAndAddress(mSeed);
                transaction.addSignedInput(outPoint, script, privateKey, Transaction.SigHash.ALL, true);
                fromUtxo.add(unspentOutput);
            }
        }
        transaction.getConfidence().setSource(TransactionConfidence.Source.SELF);
        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);

        byte[] bytes = transaction.unsafeBitcoinSerialize();
        LogTool.d(TAG, "Transaction size = " + bytes.length);
        int txSizeInkB = (int) Math.ceil(bytes.length / 1024.);
//        BigDecimal minimumFee = mFeePerKb.multiply(new BigDecimal(txSizeInkB));
//                        if (minimumFee.doubleValue() > oldFee.doubleValue()) {
//                            String error = CommonUtility.formatString(minimumFee.toString(), " QTUM");
//                            String errorValue = String.format(mContext.getString(R.string.insufficient_fee_tips), error, error);
//                            ExceptionHandle.ResponseThrowable responseThrowable = new ExceptionHandle.ResponseThrowable(errorValue, TRANSFER_ERROR_ONE, minimumFee, amountString, toAddress);
//                            throw responseThrowable;
//                        }
        transactionRaw = Hex.toHexString(bytes);
        LogTool.d(TAG, "transactionRaw:" + transactionRaw);
        transactionHash = transaction.getHashAsString();
        LogTool.d(TAG, "transactionHash:" + transactionHash);
        interactor.pushTX(transactionRaw, new Callback<String>() {
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
//                                    BigDecimal newFee = delivery.add(oldFee);
//                                    String error = delivery.toPlainString();
//                                    String errorValue = String.format(mContext.getString(R.string.transfer_error_too_low_value), error);
//                                    LogTool.e(TAG, "转账剩余qtum太低  value = " + delivery + " oldFee = " + oldFee + " 新的fee = " + newFee);
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
