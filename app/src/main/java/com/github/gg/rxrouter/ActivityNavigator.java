package com.github.gg.rxrouter;

import android.app.Activity;
import android.app.Application;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.widget.Toast;

import com.github.router.ActivityRouter;
import com.github.router.RxRouters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * @author Guang1234567
 * @date 2018/5/21 16:43
 */

public final class ActivityNavigator {

    public final static class NAVI_TO_SECOND {
        public static final String ALIAS_01 = "rxrouter://www.mycompany.com/ui/second_activity_1";
        public static final String ALIAS_02 = "https://www.mycompany.com/ui/second_activity_2";

        public static final String ALIAS_GPS_CURRENT_POS = "https://www.mycompany.com/ui/gps_current_pos";

        public static final String PARAM_AAA = "param_aaa";
    }

    private RxRouters mRouters;

    private List<Activity> mActivitys;

    private final PublishSubject<Object> mKillSwitch = PublishSubject.create();

    public ActivityNavigator(Application application) {
        initLifecycle(application);
        initRouter(application);
    }

    private void initRouter(final Application application) {
        mRouters = new RxRouters(AndroidSchedulers.mainThread(),
                new ObservableTransformer<Bundle, Bundle>() {
                    @Override
                    public ObservableSource<Bundle> apply(Observable<Bundle> upstream) {
                        return upstream.takeUntil(mKillSwitch);
                    }
                },
                new ObservableTransformer<ActivityRouter.Query, ActivityRouter.Query>() {
                    @Override
                    public ObservableSource<ActivityRouter.Query> apply(Observable<ActivityRouter.Query> upstream) {
                        return upstream.takeUntil(mKillSwitch);
                    }
                });


        mRouters.activityRouter()
                .createQuery(NAVI_TO_SECOND.ALIAS_01, SecondActivity.class, application)
                .subscribe(new Consumer<ActivityRouter.Query>() {
                    @Override
                    public void accept(ActivityRouter.Query query) throws Exception {
                        query.run();
                    }
                });


        mRouters.activityRouter()
                .createQuery(NAVI_TO_SECOND.ALIAS_02, SecondActivity.class, application)
                .subscribe(new Consumer<ActivityRouter.Query>() {
                    @Override
                    public void accept(ActivityRouter.Query query) throws Exception {
                        query.run();
                    }
                });


        mRouters.simpleRouter()
                .createQuery(NAVI_TO_SECOND.ALIAS_GPS_CURRENT_POS)
                .compose(new GpsPlugin(application))
                .subscribe(new Consumer<PointF>() {
                    @Override
                    public void accept(PointF currentPos) throws Exception {
                        Toast.makeText(application, "My current position : " + String.valueOf(currentPos), Toast.LENGTH_SHORT).show();
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

        mKillSwitch.onNext("kill");
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

    public void naviTo(String alias, Bundle args) {
        mRouters.route(alias, args);
    }
}
