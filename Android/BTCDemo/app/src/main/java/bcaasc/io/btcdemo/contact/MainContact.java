package bcaasc.io.btcdemo.contact;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/12
 */
public interface MainContact {
    interface View {
        void getBalanceSuccess(String balance);

        void getBalanceFailure(String info);

        void success(String info);

        void failure(String info);

        void hashStatus(String info);

        void setHashRaw(String hashRaw);
    }

    interface Presenter {

        void getBalance(String address);

        void getTransactionList(String address);

        void getUnspent(String address, String amount, String fee,String addressTo,String addressPrivateKey);

        void getTXInfoByHash(String rawHash);

        void pushTX(String feeString, String toAddress, String amountString,String addressPrivateKey);
    }
}
