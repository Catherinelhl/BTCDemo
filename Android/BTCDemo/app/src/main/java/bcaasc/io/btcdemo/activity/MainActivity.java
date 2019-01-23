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
import bcaasc.io.btcdemo.constants.BTCParamsConstants;
import bcaasc.io.btcdemo.constants.Constants;
import bcaasc.io.btcdemo.constants.MessageConstants;
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


    @BindView(R.id.et_address)
    EditText etAddress;
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
    @BindView(R.id.et_to_address)
    EditText etToAddress;
    @BindView(R.id.ib_scan)
    ImageButton ibScan;
    @BindView(R.id.ib_scan_address)
    ImageButton ibScanAddress;
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
    @BindView(R.id.et_fee)
    EditText etFee;
    @BindView(R.id.switch_net)
    Switch switchNet;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.ib_scan_private_key)
    ImageButton ibScanPrivateKey;
    @BindView(R.id.ll_my_private_key)
    LinearLayout llMyPrivateKey;
    @BindView(R.id.ll_send_amount)
    LinearLayout llSendAmount;
    private MainContact.Presenter presenter;

    //得到当前的txhash
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
//        tvAddress.setText("Address:" + Constants.address);
//        tvToAddress.setText(Constants.toAddress);
        etAmount.setText("0.0020");
        etFee.setText(Constants.feeString);
    }

    private void initListener() {
        switchNet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //刷新当前界面
                etAddress.setText(MessageConstants.EMPTY);
                etToAddress.setText(MessageConstants.EMPTY);
                etPrivateKey.setText(MessageConstants.EMPTY);
                tvTxHash.setText(MessageConstants.EMPTY);
                tvContent.setText(MessageConstants.EMPTY);
                //切换网络
                BTCParamsConstants.isTest = !isChecked;
            }
        });
        tvGetBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etAddress.getText().toString();
                if (address == null || TextUtils.isEmpty(address)) {
                    Toast.makeText(MainActivity.this, "请先输入地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                presenter.getBalance(address);
            }
        });
        btnGetTxList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etAddress.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(MainActivity.this, "请先输入地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                presenter.getTransactionList(address);
            }
        });
        btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etAddress.getText().toString();
                String amount = etAmount.getText().toString();
                String addressTo = etToAddress.getText().toString();
                String privateKey = etPrivateKey.getText().toString();
                if (TextUtils.isEmpty(amount)) {
                    Toast.makeText(MainActivity.this, "请先输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(MainActivity.this, "请先输入地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(addressTo)) {
                    Toast.makeText(MainActivity.this, "请先输入接收方地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(privateKey)) {
                    Toast.makeText(MainActivity.this, "请先输入私钥", Toast.LENGTH_SHORT).show();
                    return;
                }
                String fee = etFee.getText().toString();
                if (TextUtils.isEmpty(fee)) {
                    Toast.makeText(MainActivity.this, "请先输入手续费", Toast.LENGTH_SHORT).show();
                    return;
                }
                presenter.getUnspent(address, amount, fee, addressTo, privateKey);
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
                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_CODE_SCAN_RECEIVE_ADDRESS_OK);

            }
        });
        ibScanAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_CODE_SCAN_ADDRESS_OK);

            }
        });
        ibScanPrivateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_CODE_SCAN_PRIVATE_KEY_OK);

            }
        });

        ibScanAddress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String address = etAddress.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(MainActivity.this, "请先输入地址", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Intent intent = new Intent();
                intent.putExtra("address", address);
                intent.setClass(MainActivity.this, QrCodeActivity.class);
                startActivity(intent);
                return false;
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

    public static final int REQUEST_CODE_SCAN_RECEIVE_ADDRESS_OK = 0x001;
    public static final int REQUEST_CODE_SCAN_ADDRESS_OK = 0x004;
    public static final int REQUEST_CODE_SCAN_PRIVATE_KEY_OK = 0x005;
    public static final int REQUEST_CODE_SCAN_HASH_OK = 0x003;
    public static final int REQUEST_CODE_CAMERA_Permission_OK = 0x002;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            String result = bundle.getString("result");
            switch (requestCode) {
                case REQUEST_CODE_SCAN_ADDRESS_OK:
                    if (etAddress != null) {
                        etAddress.setText(result);
                    }
                    break;
                case REQUEST_CODE_SCAN_RECEIVE_ADDRESS_OK:
                    if (etToAddress != null) {
                        etToAddress.setText(result);
                    }
                    break;
                case REQUEST_CODE_SCAN_HASH_OK:
                    if (tvTxHash != null) {
                        tvTxHash.setText(result);
                    }
                    break;
                case REQUEST_CODE_SCAN_PRIVATE_KEY_OK:
                    if (etPrivateKey != null) {
                        etPrivateKey.setText(result);
                    }
                    break;
            }
        }

    }

    @Override
    public void setHashRaw(String hashRaw) {
        this.hashRaw = hashRaw;
        tvTxHash.setText(hashRaw);
    }
}
