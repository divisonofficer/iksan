package com.sss.fills;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class LauncyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(LauncyActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundColor(Color.parseColor("#074E72"))
                .withBackgroundResource(R.drawable.ic_launch_background)
//                .withLogo(R.drawable.ic_logo)
                .withHeaderText("Welcom Fills!!!")
                .withFooterText("Copyright 2019 OpenHack")
                .withBeforeLogoText("지도 여행을 떠나자")
                .withAfterLogoText("This is east LauncyActivity Screen");

        //Set Test Color
        config.getHeaderTextView().setTextColor(Color.WHITE);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);

        //Set to vew
        View view = config.create();

        //Set view to content view
        setContentView(view);
    }
}
