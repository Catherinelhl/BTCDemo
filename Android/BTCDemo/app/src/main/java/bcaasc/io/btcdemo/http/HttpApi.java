package bcaasc.io.btcdemo.http;

import io.reactivex.Observable;
import retrofit2.http.*;

public interface HttpApi {

    /*获取当前钱包的余额*/
    @GET("/balance")
    Observable<String> getBalance(@Query("active") String address);

    /*获取当前钱包的未交易区块*/
    @GET("/unspent")
    Observable<String> getUnspentTransactionOutputs(@Query("active") String address);

    /*发送交易*/
    @FormUrlEncoded
    @POST("/pushtx")
    Observable<String> publishTX(@Field("tx") String tx);

    /*获取交易记录*/
    @GET("/rawaddr/{address}")
    Observable<String> getTransactionList(@Path("address") String address);
}
