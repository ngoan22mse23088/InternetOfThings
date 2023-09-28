package com.screens.activity.slider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.screens.R;
import com.screens.utils.Tools;

public class SliderFilterFlight extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_filter_flight);

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }
}