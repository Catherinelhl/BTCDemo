package bcaasc.io.btcdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import bcaasc.io.btcdemo.R;
import bcaasc.io.btcdemo.constants.Constants;
import bcaasc.io.btcdemo.contact.MainContact;
import bcaasc.io.btcdemo.presenter.MainPresenterImp;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.obt.qrcode.activity.CaptureActivity;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/12
 */
public class MainActivity extends AppCompatActivity implements MainContact.View {


    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.ib_receive)
    ImageButton ibReceive;
    @BindView(R.id.ll_my_address)
    LinearLayout llMyAddress;
    @BindView(R.id.tv_get_balance)
    TextView tvGetBalance;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.ll_my_balance)
    LinearLayout llMyBalance;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.btn_to_address)
    TextView btnToAddress;
    @BindView(R.id.tv_to_address)
    TextView tvToAddress;
    @BindView(R.id.ib_scan)
    ImageButton ibScan;
    @BindView(R.id.ll_to_send_address)
    LinearLayout llToSendAddress;
    @BindView(R.id.btn_push)
    Button btnPush;
    @BindView(R.id.btn_get_tx_list)
    Button btnGetTxList;
    @BindView(R.id.ll_send_action)
    LinearLayout llSendAction;
    @BindView(R.id.tv_get_tx_status)
    TextView tvGetTxStatus;
    @BindView(R.id.tv_tx_hash)
    TextView tvTxHash;
    @BindView(R.id.ib_scan_hash)
    ImageButton ibScanHash;
    @BindView(R.id.ll_query)
    LinearLayout llQuery;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    private MainContact.Presenter presenter;
    //得到当前交易的hashRaw
    private String hashRaw;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getCameraPermission();
        initView();
        initListener();
    }


    private void initView() {
        presenter = new MainPresenterImp(this);
        tvAddress.setText("Address:" + Constants.address);
        tvToAddress.setText(Constants.toAddress);
        etAmount.setText("0.0020");
        tvFee.setText("Fee:" + Constants.feeString + " BTC");
        presenter.getBalance();
    }

    private void initListener() {
        ibReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, QrCodeActivity.class);
                startActivity(intent);
            }
        });
        tvGetBalance.setOnClickListener(new View.OnClickListener() {
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
                String address = tvToAddress.getText().toString();
                String amount = etAmount.getText().toString();
                if (TextUtils.isEmpty(amount)) {
                    amount = Constants.amountString;
                }

                presenter.getUnspent(amount, address);
            }
        });
        tvGetTxStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txHash = tvTxHash.getText().toString();
                if (TextUtils.isEmpty(txHash)) {
                    Toast.makeText(MainActivity.this, "txHash is Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    presenter.getTXInfoByHash(txHash);
                }
            }
        });
        ibScanHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_CODE_SCAN_HASH_OK);

            }
        });
        ibScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_CODE_CAMERA_OK);

            }
        });


    }


    /*獲得照相機權限*/
    private void getCameraPermission() {
        if (Build.VERSION.SDK_INT > 22) { //这个说明系统版本在6.0之下，不需要动态获取权限。
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请,否则说明已经获取到摄像头权限了 想干嘛干嘛
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_Permission_OK);

            }
        }
    }

    @Override
    public void getBalanceSuccess(String balance) {
        tvBalance.setText(balance + "  BTC");
    }

    @Override
    public void getBalanceFailure(String info) {

    }

    @Override
    public void success(String info) {
        tvContent.setText(info);

    }

    @Override
    public void failure(String info) {
        tvContent.setText(info);

    }

    @Override
    public void hashStatus(String info) {
        tvContent.setText("blockHeight:" + info);
    }

    public static final int REQUEST_CODE_CAMERA_OK = 0x001;
    public static final int REQUEST_CODE_SCAN_HASH_OK = 0x003;
    public static final int REQUEST_CODE_CAMERA_Permission_OK = 0x002;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            if (requestCode == REQUEST_CODE_CAMERA_OK) {
                // 如果当前是照相机扫描回来
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String result = bundle.getString("result");
                    tvToAddress.setText(result);
                }
            } else if (requestCode == REQUEST_CODE_SCAN_HASH_OK) {
                // 如果当前是照相机扫描回来
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String result = bundle.getString("result");
                    tvTxHash.setText(result);
                }
            }

        }
    }

    @Override
    public void setHashRaw(String hashRaw) {
        this.hashRaw = hashRaw;
        tvTxHash.setText(hashRaw);
    }
}
