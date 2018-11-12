package bcaasc.io.btcdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import bcaasc.io.btcdemo.R;
import bcaasc.io.btcdemo.contact.MainContact;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/12
 */
public class MainActivity extends Activity implements MainContact.View {

    private MainContact.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
