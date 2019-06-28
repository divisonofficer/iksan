package com.sss.fills;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProvinceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);

        Button btnIcksan = (Button)findViewById(R.id.icksan);
        btnIcksan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(ProvinceActivity.this, MapActivity.class);
                startActivity(intent1);
            }
        });

        Button btnGunsan = (Button)findViewById(R.id.gunsan);
        btnGunsan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(ProvinceActivity.this, FilterActivity.class);
                startActivity(intent1);
            }
        });
    }


}
