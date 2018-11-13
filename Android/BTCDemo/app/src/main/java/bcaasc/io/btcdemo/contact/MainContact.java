package bcaasc.io.btcdemo.contact;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/12
 */
public interface MainContact {
    interface View {

    }

    interface Presenter {

        void getBalance();
        void getTransactionList();
        void getUnspent();
        void pushTX();
    }
}
