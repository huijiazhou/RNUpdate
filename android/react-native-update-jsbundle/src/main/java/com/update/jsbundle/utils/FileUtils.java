package com.update.jsbundle.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.update.jsbundle.Constant;
import com.update.jsbundle.callback.HttpCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 *
 */
public class FileUtils {


    public static File createFile(Context context, boolean hasSd){


        File file=null;
        String state = Environment.getExternalStorageState();

        if(state.equals(Environment.MEDIA_MOUNTED) && hasSd){

            file = new File(Constant.zippath);
            File dir = new File(Constant.SDPath);
            if (!dir.exists()){
                dir.mkdirs();
            }
        }else {
            file = new File(getDiskCacheDir(context)+"/"+Constant.zipName);
        }

        Log.d("-----createFile----",file.getAbsolutePath());

        return file;

    }




    public static void writeFile2Disk(Response<ResponseBody> response, File file, HttpCallBack httpCallBack){


        long currentLength = 0;
        OutputStream os =null;

        InputStream is = response.body().byteStream();
        long totalLength =response.body().contentLength();

        try {

            os = new FileOutputStream(file);


            int len ;

            byte [] buff = new byte[1024];

            while((len=is.read(buff))!=-1){

                os.write(buff,0,len);
                currentLength+=len;
                Log.d("vivi","当前进度:"+currentLength);
                httpCallBack.onLoading(currentLength,totalLength);
            }
          httpCallBack.onComplete();


        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(os!=null){
                try {
                    os.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * 缓存
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            cachePath = context.getExternalCacheDir().getPath();
//        } else {
            cachePath = context.getCacheDir().getPath();
//        }
        return cachePath;
    }


    public static String getAssetsCacheFile(Context context,String fileName)   {
        File cacheFile = new File(context.getCacheDir(), fileName);
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }

}
