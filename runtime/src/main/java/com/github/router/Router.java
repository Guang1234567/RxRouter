package com.github.router;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author Guang1234567
 * @date 2018/2/27 11:03
 */

public class Router {

    public static final String TAG = "Router";

    private final Subject mTriggers;

    public Router() {
        mTriggers = PublishSubject.create();
    }

    void sendRequestTrigger(Object request) {
        mTriggers.onNext(request);
    }

    public <T> void route(T to) {
        sendRequestTrigger(to);
    }

    public void route(Context from, Uri to, Intent args) {
        route(new ActivityRequest(from, to, args));
    }

    public <T> Observable<T> to(final T to) {
        return mTriggers
                .filter(new Predicate<T>() {
                    @Override
                    public boolean test(T o) throws Exception {
                        //Log.d(TAG, String.valueOf(o) + "\n" + String.valueOf(to));
                        return to != null
                                && to.equals(o);
                    }
                });
    }

    public <T> Observable<ActivityRequest<T>> to(final T to, final Class<? extends Activity> clazz) {
        Request<T> r = new Request(to);
        return to(r)
                .filter(new Predicate<Request<T>>() {
                    @Override
                    public boolean test(Request<T> uriRequest) throws Exception {
                        return clazz != null;
                    }
                })
                .ofType(ActivityRequest.class)
                .map(new Function<ActivityRequest, ActivityRequest<T>>() {
                    @Override
                    public ActivityRequest<T> apply(ActivityRequest activityRequest) throws Exception {
                        return (ActivityRequest<T>) activityRequest;
                    }
                })
                .doOnNext(new Consumer<ActivityRequest<T>>() {
                    @Override
                    public void accept(ActivityRequest<T> r) throws Exception {
                        Context from = r.getFrom();
                        T to = r.getTo();
                        Intent args = r.getArgs();
                        Intent intent;
                        if (args == null) {
                            intent = new Intent(from, clazz);
                        } else {
                            intent = new Intent(args);
                            intent.setComponent(new ComponentName(from, clazz));
                        }
                        from.startActivity(intent);
                    }
                });
    }
}
