package com.example.administrator.xinglv.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by 杨志颖 on 2016/7/21 0021.
 */
public class BaseAcitivity extends AppCompatActivity  {
    //private static final int REQUEST_CODE = 1;
    //public PopuwindowLoading dialogLoading;
    //public PopuwindowNoData dialogNoData;
    public int page = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //启动时调用
    protected void onStart() {
        super.onStart();
        //hideNavigationBar();
        // dialogLoading = new PopuwindowLoading(this);
    }

    //恢复到前台时调用
    @Override
    protected void onResume() {
        super.onResume();
        //JPushInterface.onResume(this);
    }

    //暂停时调用
    @Override
    protected void onPause() {
        super.onPause();
        //JPushInterface.onPause(this);
    }

    //退出时调用
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //隐藏底部虚拟按键
    public void hideNavigationBar() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.setAttributes(params);
    }


  /*  public void changeColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.main_header_bg);//通知栏所需颜色
    }*/

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void getData(boolean flag) {
    }

}
