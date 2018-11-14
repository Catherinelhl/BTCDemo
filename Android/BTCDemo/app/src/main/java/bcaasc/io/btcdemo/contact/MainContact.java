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

    }

    interface Presenter {

        void getBalance();

        void getTransactionList();

        void getUnspent();

        void getTXInfoByHash(String rawHash);

        void pushTX(String feeString, String toAddress, String amountString);
    }
}
