package com.example.administrator.computernetwork.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;//
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVUser;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity{
    protected FragmentManager fragmentManager;
    protected Activity activity;
    protected AVUser current_user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
        ButterKnife.bind(this);
        if (AVUser.getCurrentUser() != null){
            current_user = AVUser.getCurrentUser();
        }
        fragmentManager = getSupportFragmentManager();
        activity = this;
        logicActivity(savedInstanceState);
        
    }
    protected abstract void logicActivity(Bundle savedInstanceState);
    protected abstract int getLayoutView();
}
