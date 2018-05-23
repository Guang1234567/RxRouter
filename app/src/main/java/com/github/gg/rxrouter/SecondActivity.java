package com.github.gg.rxrouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    public static final String EXTRA_VALUE_FOR_RESULT_CODE = "extra_value_for_result_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView tv = findViewById(R.id.text);
        String str = getIntent().getStringExtra(ActivityNavigator.NAVI_TO_SECOND.PARAM_AAA);
        if (!TextUtils.isEmpty(str)) {
            tv.append(str);
        }

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_VALUE_FOR_RESULT_CODE, "我是SecondActivity返回的数据"); //将计算的值回传回去
                setResult(RESULT_OK, intent);

                finish(); //结束当前的activity的生命周期
            }
        });
    }
}
