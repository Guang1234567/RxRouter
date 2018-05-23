package com.github.gg.rxrouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.functions.Function;

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
                .map(new Function<Object, Pair<String, Bundle>>() {
                    @Override
                    public Pair<String, Bundle> apply(Object o) throws Exception {
                        Bundle args = new Bundle();
                        args.putString(ActivityNavigator.NAVI_TO_SECOND.PARAM_AAA, "value_bbbbb8");
                        return new Pair<>(ActivityNavigator.NAVI_TO_SECOND.ALIAS_02, args);
                    }
                })
                .subscribe(mNavigator.naviByAlias());
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
