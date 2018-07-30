package com.update.jsbundle;

import android.os.Environment;

/**
 * Created by zhj on 2018/7/20.
 */

public class Constant {
    public static  String zipName = "RN_qyl.zip";
    public static  String confName = "conf.json";
    public static  String bundleName = "index.android.jsbundle";


    public static String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/qiuyoule/";
    public static String zippath = SDPath+zipName;
    public static String jsbundleDir = SDPath+"RN_qyl/";
    public static String filepath = jsbundleDir+confName;
    public static String jsbundlepath = jsbundleDir+bundleName;

}
