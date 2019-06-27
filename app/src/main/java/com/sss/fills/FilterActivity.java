package com.sss.fills;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;

public class FilterActivity extends AppCompatActivity {
    FButton btnComplete;
    EditText editText;
    SeekBar seekBar;

    ArrayList<Integer> mUserItems = new ArrayList<>();

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

        final Button btnHeritage = (Button)findViewById(R.id.heritage);
        btnHeritage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnHeritage.setSelected(true);
            }
        });

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
