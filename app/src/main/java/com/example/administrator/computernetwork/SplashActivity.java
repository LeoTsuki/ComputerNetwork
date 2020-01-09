package com.example.administrator.computernetwork;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.computernetwork.About.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private final int Splash_Delay_Time=2500;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler=new Handler();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                finish();

            }
        }, Splash_Delay_Time);
    }

        
    }

