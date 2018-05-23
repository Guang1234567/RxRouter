package com.github.gg.rxrouter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.github.router.RxRouters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @author Guang1234567
 * @date 2018/5/21 16:43
 */

public final class ActivityNavigator {

    public final static class NAVI_TO_SECOND {
        public static final String ALIAS_01 = "rxrouter://www.mycompany.com/ui/second_activity_1";
        public static final String ALIAS_02 = "https://www.mycompany.com/ui/second_activity_2";

        public static final String ALIAS_DO_STH_03 = "https://www.mycompany.com/ui/doSth";

        public static final String PARAM_AAA = "param_aaa";
    }

    private RxRouters mRouters;

    private List<Activity> mActivitys;

    public ActivityNavigator(Application application) {
        initLifecycle(application);
        initRouter(application);
    }

    private void initRouter(final Application application) {
        mRouters = new RxRouters(application);
        mRouters.registerAlias(SecondActivity.class, NAVI_TO_SECOND.ALIAS_01);
        mRouters.registerAlias(SecondActivity.class, NAVI_TO_SECOND.ALIAS_02);
        mRouters.asAliasObservable(NAVI_TO_SECOND.ALIAS_DO_STH_03)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Pair<String, Bundle>>() {
                    @Override
                    public void accept(Pair<String, Bundle> pair) throws Exception {
                        Toast.makeText(application, String.valueOf(pair), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initLifecycle(Application application) {
        mActivitys = new LinkedList<>();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivitys.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (!mActivitys.isEmpty()) {
                    mActivitys.remove(activity);
                }
            }
        });
    }

    @MainThread
    private void doFinishAllActivity() {
        if (mActivitys.isEmpty()) {
            return;
        }
        Iterator<Activity> iterator = mActivitys.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            activity.finish();
            iterator.remove();
        }
    }

    public void finishAllActivity() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            doFinishAllActivity();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    doFinishAllActivity();
                }
            });
        }
    }

    public Consumer<? super Intent> naviByIntent() {
        return mRouters.asIntentConsumer();
    }

    public Consumer<? super Pair<Uri, Bundle>> naviByUri() {
        return mRouters.asUriConsumer();
    }

    public Consumer<? super Pair<String, Bundle>> naviByAlias() {
        return mRouters.asAliasConsumer();
    }
}
