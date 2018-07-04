package com.github.router;

import android.os.Bundle;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;

/**
 * @author Guang1234567
 * @date 2018/5/22 14:01
 */

public final class RxRouters {

    private final SimpleRouter mSimpleRouter;

    private final ActivityRouter mActivityRouter;

    public RxRouters(Scheduler scheduler,
                     ObservableTransformer<Bundle, Bundle> simpleRouterTf,
                     ObservableTransformer<ActivityRouter.Query, ActivityRouter.Query> activityRouterTf) {
        mSimpleRouter = new SimpleRouter(scheduler, simpleRouterTf == null ? RxRouters.<Bundle>createDefaultObservableTransformer() : simpleRouterTf);
        mActivityRouter = new ActivityRouter(scheduler, activityRouterTf == null ? RxRouters.<ActivityRouter.Query>createDefaultObservableTransformer() : activityRouterTf);
    }

    public void route(String alias, Bundle args) {
        mSimpleRouter.route(alias, args);
        mActivityRouter.route(alias, args);
    }

    public SimpleRouter simpleRouter() {
        return mSimpleRouter;
    }

    public ActivityRouter activityRouter() {
        return mActivityRouter;
    }

    static final <T> ObservableTransformer<T, T> createDefaultObservableTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public Observable<T> apply(Observable<T> upstream) {
                return upstream;
            }
        };
    }
}
