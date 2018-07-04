package com.github.gg.rxrouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_001 = 0x333;

    private ActivityNavigator mNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigator = new ActivityNavigator(getApplication()); // init

        final TextView tv = findViewById(R.id.text);
        RxView.clicks(tv)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Bundle args = new Bundle();
                        args.putString(ActivityNavigator.NAVI_TO_SECOND.PARAM_AAA, "value_bbbbb8");
                        mNavigator.naviTo(ActivityNavigator.NAVI_TO_SECOND.ALIAS_02, args);
                    }
                });
    }

    private void showToast(Object request) {
        Toast.makeText(this, "hello!!!\n" + String.valueOf(request), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_001) {
            if (resultCode == RESULT_OK) {
                String response = data.getStringExtra(SecondActivity.EXTRA_VALUE_FOR_RESULT_CODE);

                TextView tv = findViewById(R.id.text);
                tv.append(response);
            }
        }
    }
}
