package bcaasc.io.btcdemo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import bcaasc.io.btcdemo.R;
import bcaasc.io.btcdemo.constants.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.obt.qrcode.encoding.EncodingUtils;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/23
 * 显示当前地址的二维码样式
 */
public class QrCodeActivity extends AppCompatActivity {
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    //二維碼渲染的前景色
    private int foregroundColorOfQRCode = 0xff000000;
    //二維碼渲染的背景色
    private int backgroundColorOfQRCode = 0x00000000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        makeQRCodeByAddress();
    }

    private void makeQRCodeByAddress() {
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Bitmap qrCode = EncodingUtils.createQRCode(Constants.address, 200,
                200, null, foregroundColorOfQRCode, backgroundColorOfQRCode);
        ivQrCode.setImageBitmap(qrCode);
    }
}
