package com.hotfix.patchdispatcher.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chan on 2018/1/12.
 * 封装了Toast的工具类
 */

public class ToastUtil {

    private static Toast toast;

    public static void showTextForShort(Context context, String msg){
        if (toast == null){
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }else {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        toast.show();
    }

    public static void showTextForLong(Context context, String msg){
        if (toast == null){
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        }else {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_LONG);
        }

        toast.show();
    }

}
