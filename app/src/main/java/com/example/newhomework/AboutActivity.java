package com.example.newhomework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//מסך האודות של האפליקציה

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button btnBack = findViewById(R.id.btnBack);

        //כפתור חזור
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
