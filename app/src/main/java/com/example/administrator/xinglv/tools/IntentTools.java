package com.example.administrator.xinglv.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;


import com.example.administrator.xinglv.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/29. 跳转activity工具
 */
public class IntentTools {

    //公共对象
    public static Object object;

    /**
     * 主页面
     */
    public static void toMainActivity(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }



}
