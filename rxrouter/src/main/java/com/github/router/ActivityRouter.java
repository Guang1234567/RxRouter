package com.github.router;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
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

public class ActivityRouter {

    private Subject<Pair<String, Bundle>> mTrigger;

    private final Scheduler mscheduler;

    private final ObservableTransformer<Query, Query> mQueryTransformer;

    protected ActivityRouter(Scheduler scheduler,
                             ObservableTransformer<Query, Query> queryTransformer) {
        mTrigger = PublishSubject.create();
        mscheduler = scheduler;
        mQueryTransformer = queryTransformer;
    }

    public void route(String alias, Bundle args) {
        mTrigger.onNext(new Pair<>(alias, args));
    }

    public Observable<Query> createQuery(String alias, Class<? extends Activity> clazz, Application application) {
        return createQuery(new Query1(alias, clazz, application));
    }

    public Observable<Query> createQuery(String alias, Class<? extends Activity> clazz, Activity activity) {
        return createQuery(new Query2(alias, clazz, activity));
    }

    private Observable<Query> createQuery(final Query query) {
        return mTrigger
                .filter(query)
                .map(query)
                .observeOn(mscheduler)
                .compose(mQueryTransformer);
    }

    public static abstract class Query
            implements Predicate<Pair<String, Bundle>>,
            Function<Pair<String, Bundle>, Query> {

        protected String mAlias;

        protected Class<? extends Activity> mClazz;

        protected Bundle mArgs;

        protected Query(final String alias, final Class<? extends Activity> clazz) {
            mAlias = alias;
            mClazz = clazz;
        }

        @Override
        public boolean test(Pair<String, Bundle> pair) throws Exception {
            return !TextUtils.isEmpty(mAlias) && mAlias.equals(pair.first);
        }

        @Override
        public Query apply(Pair<String, Bundle> pair) throws Exception {
            mArgs = pair.second;
            return this;
        }

        public abstract void run() throws Exception;

        public String getAlias() {
            return mAlias;
        }

        public Class<? extends Activity> getClazz() {
            return mClazz;
        }

        public Bundle getArgs() {
            return mArgs;
        }

        public void setArgs(Bundle args) {
            mArgs = args;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Query{");
            sb.append("mAlias='").append(mAlias).append('\'');
            sb.append(", mClazz=").append(mClazz);
            sb.append(", mArgs=").append(mArgs);
            sb.append('}');
            return sb.toString();
        }
    }

    private static class Query1 extends Query {
        private Application mApplication;

        private Query1(String alias, Class<? extends Activity> clazz, Application application) {
            super(alias, clazz);
            mApplication = application;
        }

        @Override
        public void run() throws Exception {
            Intent intent = new Intent(mApplication, mClazz);
            if (mArgs != null) {
                intent.putExtras(mArgs);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(intent);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Query1{");
            sb.append("mAlias='").append(mAlias).append('\'');
            sb.append(", mClazz=").append(mClazz);
            sb.append(", mArgs=").append(mArgs);
            sb.append(", mApplication=").append(mApplication);
            sb.append('}');
            return sb.toString();
        }
    }

    private static class Query2 extends Query {
        private Activity mActivity;

        private Query2(String alias, Class<? extends Activity> clazz, Activity activity) {
            super(alias, clazz);
            mActivity = activity;
        }

        @Override
        public void run() throws Exception {
            Intent intent = new Intent(mActivity, mClazz);
            if (mArgs != null) {
                intent.putExtras(mArgs);
            }
            mActivity.startActivity(intent);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Query2{");
            sb.append("mAlias='").append(mAlias).append('\'');
            sb.append(", mClazz=").append(mClazz);
            sb.append(", mArgs=").append(mArgs);
            sb.append(", mActivity=").append(mActivity);
            sb.append('}');
            return sb.toString();
        }
    }
}
