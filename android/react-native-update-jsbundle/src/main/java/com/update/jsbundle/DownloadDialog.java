package com.update.jsbundle;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DownloadDialog {
    private static DownloadDialog inst;

    private  Dialog loadingDialog;
    private  ProgressBar pbPercent;
    private  TextView tvPercent;
    private Context mContext;

    public static DownloadDialog getInstance(Context context){
        if (inst == null){
            inst = new DownloadDialog(context);
        }
        return inst;
    }
   public DownloadDialog(Context context){
        mContext = context;
    }

    public  Dialog createLoadingDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_download, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v
                .findViewById(R.id.dialog_download_view);// 加载布局
        pbPercent = v.findViewById(R.id.pb_download_percent);
        tvPercent = v.findViewById(R.id.tv_download_percent);

        // 创建自定义样式dialog
        loadingDialog = new Dialog(mContext, R.style.MyDialogStyle);
        loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
        loadingDialog.setCanceledOnTouchOutside(false); // 点击加载框以外的区域
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局

        /**
         *将显示Dialog的方法封装在这里面
         */
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        if (!loadingDialog.isShowing()){
            loadingDialog.show();
        }
        return loadingDialog;
    }


    public void setCanceledOnTouchOutside() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.setCanceledOnTouchOutside(false);
        }
    }

    public  void closeDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public  void setPercent(int percent){
        if (pbPercent != null){
            pbPercent.setProgress(percent);
            tvPercent.setText(percent+"%");
        }
    }

}