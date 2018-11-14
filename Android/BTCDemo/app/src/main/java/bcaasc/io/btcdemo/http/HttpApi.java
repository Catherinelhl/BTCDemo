package bcaasc.io.btcdemo.http;

import bcaasc.io.btcdemo.bean.BtcUnspentOutputsResponse;
import bcaasc.io.btcdemo.bean.Transaction;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.*;

public interface HttpApi {


    /*获取当前钱包的余额*/
    @GET("/balance")
    Call<String> getBalance(@Query("active") String address);

    /*获取当前钱包的未交易区块*/
    @GET("/unspent")
    Call<BtcUnspentOutputsResponse> getUnspentTransactionOutputs(@Query("active") String address);

    /*发送交易*/
    @FormUrlEncoded
    @POST("/pushtx")
    Call<String> pushTX(@Field("tx") String tx);


    /*发送交易*/
    @FormUrlEncoded
    @POST("rawtx/{tx_hash}")
    Call<String> getTransactionOfBinary(@Field("tx") String tx);

    /*获取交易记录*/
    @GET("/rawaddr/{address}")
    Call<String> getTransactionList(@Path("address") String address);

    /*获取未确认的区块*/
    @GET("/unconfirmed-transactions?format=json")
    Observable<String> getUnconfirmedTransaction();

    /*You can also request the transaction to return in binary form (Hex encoded) using ?format=hex*/
    @GET("/rawtx/{tx_hash}")
    Call<Transaction> getTXInfoByHash(@Path("tx_hash") String hash);
}
