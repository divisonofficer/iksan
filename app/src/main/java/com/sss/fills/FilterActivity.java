package com.sss.fills;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import info.hoang8f.widget.FButton;

public class FilterActivity extends AppCompatActivity {
    FButton btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        btnComplete = (FButton)findViewById(R.id.btnComplete);

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(FilterActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }
}
