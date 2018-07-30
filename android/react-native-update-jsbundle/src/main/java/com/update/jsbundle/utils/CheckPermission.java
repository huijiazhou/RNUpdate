package com.update.jsbundle.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.update.jsbundle.callback.CheckPermissionCallBack;

/**
 * Created by zz on 2018/7/24.
 */

public class CheckPermission{
    private static CheckPermissionCallBack permissionCallBack;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private  static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static void verifyStoragePermissions(Activity activity, CheckPermissionCallBack callBack) {
        permissionCallBack = callBack;
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);


            }else {
                permissionCallBack.checkPermission(true);



            }
        } catch (Exception e) {
            e.printStackTrace();
            permissionCallBack.checkPermission(false);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isSuccess = true;
        for (int i=0;i< grantResults.length;i++){
            if (grantResults[i] != 0){
                isSuccess =  false;
                Log.i("--------Perm--"+i,grantResults[i]+"");
            }

        }
        Log.i("-------hasPermission--",isSuccess+"");
        if (isSuccess){
            permissionCallBack.checkPermission(true);

        }else{
            permissionCallBack.checkPermission(false);
        }

    }

}
