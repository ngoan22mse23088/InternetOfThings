package com.screens.activity.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.screens.R;
import com.screens.utils.Tools;

public class SplashFacebook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_fb);
        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);
        Tools.fullScreen(this);
    }
}