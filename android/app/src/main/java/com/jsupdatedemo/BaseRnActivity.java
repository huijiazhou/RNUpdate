package com.jsupdatedemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.update.jsbundle.JsBundleUpdate;
import com.update.jsbundle.callback.JsPathCallBack;
import com.update.jsbundle.utils.CheckPermission;

import java.util.Arrays;

public class BaseRnActivity extends Activity implements DefaultHardwareBackBtnHandler {
    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReactRootView = new ReactRootView(this);


        JsBundleUpdate.update(BaseRnActivity.this, getPackageName(), BuildConfig.VERSION_NAME, new JsPathCallBack() {
            @Override
            public void callback(String jspath) {
                mReactInstanceManager = ReactInstanceManager.builder()
                        .setApplication(BaseRnActivity.this.getApplication())
                        .setBundleAssetName("index.android.jsbundle")
                        .setJSMainModulePath("index")
//                .addPackage(new NewMainReactPackage(MainApplication.getDefaultConfigBuilder(getApplicationContext()).build()))
                        .addPackages( Arrays.<ReactPackage>asList(
                                new MainReactPackage()
                        ))
                        .setJSBundleFile(jspath)
                        .setUseDeveloperSupport(BuildConfig.DEBUG)
                        .setInitialLifecycleState(LifecycleState.RESUMED)
                        .build();
                //这里的AndroidRnDemoApp必须对应“index.js”中的“AppRegistry.registerComponent()”的第一个参数
                mReactRootView.startReactApplication(mReactInstanceManager, "JsUpdateDemo", null);
                //加载ReactRootView到布局中
                setContentView(mReactRootView);
                mReactInstanceManager.onHostResume(BaseRnActivity.this, BaseRnActivity.this);
            }
        });

    }
    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }
    /**
     * ReactInstanceManager生命周期同activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }
    }
    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CheckPermission.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

}
