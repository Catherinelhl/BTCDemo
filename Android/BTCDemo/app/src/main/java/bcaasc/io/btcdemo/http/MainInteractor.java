package bcaasc.io.btcdemo.http;

import bcaasc.io.btcdemo.bean.BtcUnspentOutputsResponse;
import bcaasc.io.btcdemo.http.retrofit.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * @author catherine.brainwilliam
 * @since 2018/11/13
 */
public class MainInteractor {


    public void getBalance(String address, Callback<String> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getTestInstance().create(HttpApi.class);
        Call<String> call = httpApi.getBalance(address);
        call.enqueue(callBackListener);
    }
    public void getTXList(String address, Callback<String> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getTestInstance().create(HttpApi.class);
        Call<String> call = httpApi.getTransactionList(address);
        call.enqueue(callBackListener);
    }
    public void getUnspent(String address, Callback<BtcUnspentOutputsResponse> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getTestInstance().create(HttpApi.class);
        Call<BtcUnspentOutputsResponse> call = httpApi.getUnspentTransactionOutputs(address);
        call.enqueue(callBackListener);
    }
    public void pushTX(String rawHash, Callback<String> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getTestInstance().create(HttpApi.class);
        Call<String> call = httpApi.pushTX(rawHash);
        call.enqueue(callBackListener);
    }
}
