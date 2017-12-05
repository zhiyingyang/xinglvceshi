package com.example.administrator.xinglv.tools;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.xinglv.base.MyApplication;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;

public class Tools {

    /**
     * 获取屏幕宽高
     */
    public static int[] getWidthHeight(Resources r) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = r.getDisplayMetrics();
        int width = dm.widthPixels; //
        int height = dm.heightPixels; // 屏幕高度（像素）
        int[] i = new int[2];
        // float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        // int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
        // float xdpi = dm.xdpi;
        // float ydpi = dm.ydpi;
        i[0] = width;
        i[1] = height;

        return i;
    }

    /**
     * 弹出框
     */

    public static void showToast(String msg, Context context) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 对象转json
     */
    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * 编辑框绑定软键盘
     */
    public static void showKey(View view) {

        if (view instanceof EditText) {
            InputMethodManager inputmanger = (InputMethodManager) view
                    .getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            inputmanger.showSoftInput(view, 0);
        }

    }

    //获取权限
    public static void getPermission(Context context) {
        String[] PERMISSION = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (islacksOfPermission(PERMISSION)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSION, 0x12);
        }

    }

    public static boolean islacksOfPermission(String[] permission) {
        boolean flag = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permission.length; i++) {
                flag = ContextCompat.checkSelfPermission(MyApplication.myApplication, permission[i]) ==
                        PackageManager.PERMISSION_DENIED;
            }
        }
        return flag;
    }


//    //添加用户key
//    public static void getaUserKey(RequestParams params) {
//        if (MyApplication.user == null) {
//            params.put("yonghukey", Tools.DeviceInfo(MyApplication.myApplication));
//        } else {
//            params.put("yonghukey", MyApplication.user.getKey());
//        }
//
//    }


    /**
     * json转对象
     */
    public static <T> T toObject(String jsonString, Class<T> cls) {
        T t = null;
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        try {
            t = new Gson().fromJson(jsonObject.toString(), cls);
        } catch (Exception e) {
        }
        return t;
    }

    public boolean isNull(String string) {
        if (string == null && "".equals(string)) {
            return false;
        }
        return true;
    }

    //分享
    public static void onClickShare(String url, Context context) {
        if (url == null) {
            Tools.showToast("分享失败", MyApplication.myApplication);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        // intent.setType("image/*");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "标题"));

    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public static String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = MyApplication.myApplication.getPackageManager();
            PackageInfo info = manager.getPackageInfo(MyApplication.myApplication.getPackageName(), 0);
            String version = info.versionName + "";
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * 为轮播图写的数组处理方法 在首尾加上辅助图片
     */
    public static String[] getStringArray(String url) {
        String[] data = url.split(";");
        String[] newdata = new String[data.length + 2];
        for (int i = 0; i < newdata.length; i++) {
            if (i == 0) {
                newdata[i] = data[data.length - 1];
                continue;
            }
            if (i == newdata.length - 1) {
                newdata[i] = data[0];
                continue;
            }
            newdata[i] = data[i - 1];
        }
        return newdata;
    }

    /**
     * 为轮播图写的数组处理方法 在首尾加上辅助图片
     */
    public static String[] getStringArray2(String url) {
        String[] data = url.split(",");
        String[] newdata = new String[data.length + 2];
        for (int i = 0; i < newdata.length; i++) {
            if (i == 0) {
                newdata[i] = data[data.length - 1];
                continue;
            }
            if (i == newdata.length - 1) {
                newdata[i] = data[0];
                continue;
            }
            newdata[i] = data[i - 1];
        }
        return newdata;
    }
}
