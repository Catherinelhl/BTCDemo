package bcaasc.io.btcdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import bcaasc.io.btcdemo.R;
import bcaasc.io.btcdemo.constants.Constants;
import bcaasc.io.btcdemo.contact.MainContact;
import bcaasc.io.btcdemo.presenter.MainPresenterImp;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/12
 */
public class MainActivity extends Activity implements MainContact.View {

    @BindView(R.id.btn_get_balance)
    Button btnGetBalance;
    @BindView(R.id.btn_get_tx_list)
    Button btnGetTxList;
    @BindView(R.id.btn_push)
    Button btnPush;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.btn_get_tx_status)
    Button btnGetTxStatus;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.btn_push_xiaodong)
    Button btnPushXiaodong;
    private MainContact.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initListener();
    }


    private void initView() {
        presenter = new MainPresenterImp(this);
        tvAddress.setText("Address:" + Constants.address);
        etAmount.setText("0.0020");
        presenter.getBalance();
    }

    private void initListener() {
        btnGetBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getBalance();
            }
        });
        btnGetTxList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getTransactionList();
            }
        });
        btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = etAmount.getText().toString();
                if (TextUtils.isEmpty(amount)) {
                    amount = Constants.amountString;
                }
                presenter.getUnspent(amount, Constants.toAddress);
            }
        });
        btnPushXiaodong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = etAmount.getText().toString();
                if (TextUtils.isEmpty(amount)) {
                    amount = Constants.amountString;
                }
                presenter.getUnspent(amount, "13WVtuUFwoEp3wsBmocRA1KDQ8C95P5wS3");
            }
        });
        btnGetTxStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getTXInfoByHash("");
            }
        });


    }

    @Override
    public void getBalanceSuccess(String balance) {
        btnGetBalance.setText("Current Balance:" + balance);
    }

    @Override
    public void getBalanceFailure(String info) {

    }

    @Override
    public void success(String info) {
        etContent.setText(info);

    }

    @Override
    public void failure(String info) {
        etContent.setText(info);

    }

    @Override
    public void hashStatus(String info) {
        btnGetTxStatus.setText("blockHeight:" + info);
    }
}
