package bcaasc.io.btcdemo.base;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

/**
 * @author catherine.brainwilliam
 * @since 2018/11/13
 */
public class BaseApplication extends MultiDexApplication {

    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context context() {
        return instance.getApplicationContext();
    }
}
