package com.example.administrator.computernetwork;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;


public class MyApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"stgI2DAgp39RjO4ijEErtbWv-gzGzoHsz","X64aUVijVOUH2gELAa2q4LjI");
    }
}
