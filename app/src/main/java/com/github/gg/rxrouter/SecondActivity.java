package com.github.gg.rxrouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView tv = findViewById(R.id.text);
        String str = getIntent().getStringExtra("key_aaa");
        if (!TextUtils.isEmpty(str)) {
            tv.append(str);
        }
    }
}
