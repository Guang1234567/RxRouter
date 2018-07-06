package com.github.router;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author Guang1234567
 * @date 2018/2/27 11:03
 */
public class SimpleRouter {

    private Subject<Pair<String, Bundle>> mTrigger;

    private final Scheduler mscheduler;

    private final ObservableTransformer<Bundle, Bundle> mQueryTransformer;

    protected SimpleRouter(Scheduler scheduler,
                           ObservableTransformer<Bundle, Bundle> queryTransformer) {
        mTrigger = PublishSubject.create();
        mscheduler = scheduler;
        mQueryTransformer = queryTransformer;
    }

    public void route(String alias, Bundle args) {
        mTrigger.onNext(new Pair<>(alias, args));
    }

    public Observable<Bundle> createQuery(final String alias) {
        return mTrigger
                .filter(new Predicate<Pair<String, Bundle>>() {
                    @Override
                    public boolean test(Pair<String, Bundle> pair) throws Exception {
                        return !TextUtils.isEmpty(alias) && alias.equals(pair.first);
                    }
                })
                .map(new Function<Pair<String, Bundle>, Bundle>() {
                    @Override
                    public Bundle apply(Pair<String, Bundle> pair) throws Exception {
                        return pair.second != null ? pair.second : new Bundle();
                    }
                })
                .observeOn(mscheduler)
                .compose(mQueryTransformer);
    }
}
