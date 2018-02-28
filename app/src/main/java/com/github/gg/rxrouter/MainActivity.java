package com.github.gg.rxrouter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.router.Router;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Router router = new Router();

        final Uri uri001 = new Uri.Builder().scheme("router").authority("www.mycompany.com").appendPath("second_activity").build();
        router.to(uri001, SecondActivity.class).subscribe();

        final Object uri002 = new Uri.Builder().scheme("router").authority("www.mycompany.com").appendPath("call_some_method").build();
        router.to(uri002).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object request) throws Exception {
                showToast(request);
            }
        });

        TextView tv = findViewById(R.id.text);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Usage 1:  router to activity
                Intent args = new Intent();
                args.putExtra("key_aaa", "value_bbb");
                router.route(v.getContext(), uri001, args);

                //Usage 2:  router to do some thing, exclude to activity.
                router.route(uri002);
            }
        });
    }

    private void showToast(Object request) {
        Toast.makeText(this, "hello!!!\n" + String.valueOf(request), Toast.LENGTH_SHORT).show();
    }
}
