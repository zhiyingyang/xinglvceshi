package com.example.administrator.xinglv.base;

import android.app.Application;
import android.util.Log;

import com.example.administrator.xinglv.tools.CrashHandler;


/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class MyApplication extends Application {
    //店铺列表 显示的类型

    public static MyApplication myApplication;
   // public RefWatcher refWatcher = null;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("Application", "onCreate");
        myApplication = this;
        try {
            //全局捕获异常
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext(), this);
        }catch (Exception e){
            e.printStackTrace();
        }

      //  refWatcher = LeakCanary.install(this);
    }


}
