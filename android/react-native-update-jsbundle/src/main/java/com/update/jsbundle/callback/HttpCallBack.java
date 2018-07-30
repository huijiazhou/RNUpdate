package com.update.jsbundle.callback;

/**
 * Created by zhj on 2018/7/20.
 */
public interface HttpCallBack {
    void onLoading(long current, long total);

    void onComplete();
}
