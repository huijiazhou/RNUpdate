package com.update.jsbundle;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.widget.Toast;

import com.update.jsbundle.bean.CheckBack;
import com.update.jsbundle.bean.RNMessage;
import com.update.jsbundle.callback.CheckPermissionCallBack;
import com.update.jsbundle.callback.HttpCallBack;
import com.update.jsbundle.callback.JsPathCallBack;
import com.update.jsbundle.callback.UnzipCallBack;
import com.update.jsbundle.net.HttpClient;
import com.update.jsbundle.service.CheckService;
import com.update.jsbundle.service.DownloadService;
import com.update.jsbundle.utils.CheckPermission;
import com.update.jsbundle.utils.FileUtils;
import com.update.jsbundle.utils.GsonTools;
import com.update.jsbundle.utils.JsonFile;
import com.update.jsbundle.utils.ZipUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by zhj on 2018/7/20.
 */

/**
 * {
 * "appInfo": {
 * "rn-remote": {
 * "rnVersion": "1.0",
 * "rnIndex": "index.android.jsbundle",
 * "moduleName": "qiuyoule"
 * },
 * "CFBundleIdentifier": "com.moxi.footballmatch",
 * "CFBundleShortVersionString": "1.3.0"
 * },
 * "platform": "android"
 * }
 */

public class JsBundleUpdate {

    private static JsPathCallBack jsPathCallBack;
    private static String bundlePath;
    private static Dialog dialog;


    /**
     *
     * @param context  Activity
     * @param packageName 包名 "com.jsupdatedemo"
     * @param packageVersion app 版本号 "1.0.1"
     */
    public static void update(final Activity context, final String packageName, final String packageVersion, final JsPathCallBack callBack){
        jsPathCallBack = callBack;
        CheckPermission.verifyStoragePermissions(context, new CheckPermissionCallBack() {
            @Override
            public void checkPermission(boolean hasPermission) {
                if (hasPermission){
                    //sd

                    getConf(context,packageName,packageVersion);

                }else{
                    //没有读取SD卡权限

                    getCacheConf(context,packageName,packageVersion);

                }
            }
        });
    }

    private static void getCacheConf(final Activity context, final String packageName, final String packageVersion) {

        String cacheDir =  FileUtils.getDiskCacheDir(context);
        File confFile = new File(cacheDir+"/RN_qyl/"+Constant.confName);
        if (confFile.exists()){
            String json = JsonFile.getSdJson(context,cacheDir+"/RN_qyl/"+Constant.confName);
            RNMessage rnMessage = GsonTools.changeGsonToBean(json, RNMessage.class);
            Log.i("------cache-----",json);
            checkVersion(context,rnMessage,packageName,packageVersion,false);
            bundlePath = cacheDir+"/RN_qyl/"+Constant.bundleName;
        }else{
            String zippath = cacheDir+"/"+Constant.zipName;
            String jsbundleDir = cacheDir+"/RN_qyl/";
            File zipfile = new File(zippath);
            if (zipfile.exists()){
                ZipUtils.unZip(zippath, jsbundleDir, new UnzipCallBack() {
                    @Override
                    public void unZipsuccess() {
                        getCacheConf(context,packageName,packageVersion);
                        Log.i("------cache-----","解压缩成功");
                    }

                    @Override
                    public void unZipFail(Exception error) {
                        Log.i("------cache-----","解压缩失败"+error);

                    }
                });
            }else{
                //读取assets里的conf.json
                String json = JsonFile.getAssetsJson(context,"conf.json");
                RNMessage rnMessage = GsonTools.changeGsonToBean(json, RNMessage.class);
                Log.i("------assets-----",json);
                checkVersion(context,rnMessage,packageName,packageVersion,false);
//               bundlePath =  FileUtils.getAssetsCacheFile(context,"index.android.jsbundle");
                bundlePath = "assets";
            }
        }
    }


    public static void getConf(final Activity context, final String packageName, final String packageVersion){



            File confFile = new File(Constant.filepath);
            File zipFile = new File(Constant.zippath);
            if (confFile.exists()){
                //读取sd下的conf.json
                String json = JsonFile.getSdJson(context,Constant.filepath);
                RNMessage rnMessage = GsonTools.changeGsonToBean(json, RNMessage.class);
                Log.i("------sd-----",json);
                checkVersion(context,rnMessage,packageName,packageVersion,true);
                bundlePath = Constant.jsbundlepath;

            }else{
                if (zipFile.exists()){
                    //解压缩zip
                    ZipUtils.unZip(Constant.zippath,Constant.jsbundleDir,new UnzipCallBack() {
                        @Override
                        public void unZipsuccess() {
                            getConf(context,packageName,packageVersion);
                            Log.i("-----------","解压缩成功");
                        }

                        @Override
                        public void unZipFail(Exception e) {
                            Log.i("-----------","解压缩失败"+e);
                        }
                    });


                }else{
                    //读取assets里的conf.json
                    String json = JsonFile.getAssetsJson(context,"conf.json");
                    RNMessage rnMessage = GsonTools.changeGsonToBean(json, RNMessage.class);
                    Log.i("------assets-----",json);
                    checkVersion(context,rnMessage,packageName,packageVersion,true);
//                    bundlePath = context.getAssets()+"index.android.jsbundle";
//                    bundlePath =  FileUtils.getAssetsCacheFile(context,"index.android.jsbundle");
                    bundlePath = "assets";
                }
            }


    }


    public static void checkVersion(final Activity context, RNMessage message, String packageName, String packageVersion, final boolean hasSd){
        Log.i("JsBundleUpdate","checkVersion");

        Map<String,Object> map0 = new HashMap<>();
        map0.put("rnVersion",message.rnVersion);
        map0.put("rnIndex",message.rnIndex);
        map0.put("moduleName",message.moduleName);
        Map<String,Object> map1= new HashMap<>();
        map1.put("rn-remote",map0);
        map1.put("CFBundleIdentifier",packageName);
        map1.put("CFBundleShortVersionString",packageVersion);
        Map<String,Object> map = new HashMap<>();
        map.put("appInfo",map1);
        map.put("platform","android");

        Retrofit retrofit = new Retrofit.Builder()
                .client(HttpClient.getHttpClient())
                .baseUrl("https://mx.vincent78.com:8083/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CheckService request = retrofit.create(CheckService.class);
        Call<CheckBack> call = request.checkVersion(map);
        call.enqueue(new Callback<CheckBack>() {
            @Override
            public void onResponse(Call<CheckBack> call, Response<CheckBack> response) {
                CheckBack checkBack = response.body();
                Log.i("------JsBundleUpdate",checkBack.url);
                checkBack.url =  "https://cprn.oss-cn-hongkong.aliyuncs.com/test/qiuyoule-android-0.0.1.zip";
                if (checkBack.url != null && !checkBack.url.isEmpty()){

                    String url = checkBack.url;
                    downlodZip(url,context,hasSd);

                }else{
                    Log.e("-----bundlePath",bundlePath);
                    jsPathCallBack.callback(bundlePath);
                }
            }

            @Override
            public void onFailure(Call<CheckBack> call, Throwable t) {
                Log.e("-----JsBundleUpdate",t.toString());
                Log.e("-----bundlePath",bundlePath);

                jsPathCallBack.callback(bundlePath);
            }
        });
        
    }

//    {
//        "bkey": "13e4994836",
//            "jkey": "84f37b42b524a0b5cfc950f7",
//            "moduleName": "TC168",
//            "packageType": "lastest",
//            "showNav": "N",
//            "showOrigin": "N",
//            "showRN": "Y",
//            "tkey": "11",
//            "ukey": "11",
//            "upgrade": "Y",
//            "url": "https://cprn.oss-cn-hongkong.aliyuncs.com/118/RN0.37-nocodepush-118-release-1.1.0.zip",
//            "webURL": "http://www.4399.com"
//    }
    private static void downlodZip(String url, final Activity context, final boolean hasSd) {

    String baseUrl = url.substring(0,url.lastIndexOf("/")+1);
    String downloadUrl = url.substring(url.lastIndexOf("/")+1,url.length());
//
        Retrofit retrofit = new Retrofit.Builder()
                .client(HttpClient.getHttpClient())
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        DownloadService downloadService = retrofit.create(DownloadService.class);

        Call<ResponseBody> responseBodyCall = downloadService.downloadFile(downloadUrl);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                //建立一个文件
                final File file          = FileUtils.createFile(context,hasSd);

                //下载文件放在子线程
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        //保存到本地
                        FileUtils.writeFile2Disk(response, file, new HttpCallBack() {
                            @Override
                            public void onLoading(final long current, final long total) {
                                /**
                                 * 更新进度条
                                 */
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(dialog == null){
                                            dialog = DownloadDialog.getInstance(context).createLoadingDialog();
                                        }


                                        if ((int) Math.abs(current*100/total) == 100){

                                           DownloadDialog.getInstance(context).closeDialog();
                                        }else{
                                            DownloadDialog.getInstance(context).setPercent((int) Math.abs(current*100/total));
                                        }

                                    }
                                });
                            }

                            @Override
                            public void onComplete() {

                                String zippath;
                                String jsbundleDir;
                                if (hasSd){
                                    zippath = Constant.zippath;
                                    jsbundleDir = Constant.jsbundleDir;
                                }else{
                                     zippath = FileUtils.getDiskCacheDir(context)+"/"+Constant.zipName;
                                     jsbundleDir = FileUtils.getDiskCacheDir(context)+"/RN_qyl/";
                                }

                                        ZipUtils.unZip(zippath,jsbundleDir,new UnzipCallBack() {

                                            @Override
                                            public void unZipsuccess() {
                                                context.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String path;
                                                        if (hasSd){
                                                            path = Constant.jsbundlepath;
                                                        }else{
                                                            path = FileUtils.getDiskCacheDir(context)+"/RN_qyl/"+Constant.bundleName;
                                                        }
                                                        Log.i("-------下载解压后--",path+"--"+jsPathCallBack);

                                                        jsPathCallBack.callback(path);
                                                    }
                                                });


                                            }

                                            @Override
                                            public void unZipFail(Exception e) {
                                                Toast.makeText(context,"解压缩失败",Toast.LENGTH_SHORT).show();
                                                Log.i("-----------","解压缩失败"+e);
                                                context.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        jsPathCallBack.callback(bundlePath);
                                                        Log.e("-----bundlePath",bundlePath);

                                                    }
                                                });

                                            }
                                        });
                                    }
                                });


                    }
                }.start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("-----bundlePath",bundlePath+"----"+t.toString());

                jsPathCallBack.callback(bundlePath);
            }
        });

    }
}
