package com.github.router;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * @author Guang1234567
 * @date 2018/5/22 14:01
 */

public final class RxRouters {

    private final Application mApplication;

    private final IRouter<Intent> mIntentRouter;

    private final IRouter<Pair<Uri, Bundle>> mUriRouter;

    private final IRouter<Pair<String, Bundle>> mAliasRouter;

    public RxRouters(Application application) {
        mApplication = application;

        mIntentRouter = createRouter();
        mIntentRouter.asObservable()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mApplication.startActivity(intent);
                    }
                });

        mUriRouter = createRouter();

        mAliasRouter = createRouter();
    }

    private static <T> IRouter<T> createRouter() {
        return new CoreRouter<>();
    }

    public Consumer<? super Intent> asIntentConsumer() {
        return mIntentRouter.asConsumer();
    }

    public Consumer<? super Pair<Uri, Bundle>> asUriConsumer() {
        return mUriRouter.asConsumer();
    }

    public Observable<Pair<Uri, Bundle>> asUriObservable(final Uri uri) {
        return mUriRouter.asObservable()
                .filter(new Predicate<Pair<Uri, Bundle>>() {
                    @Override
                    public boolean test(Pair<Uri, Bundle> pair) throws Exception {
                        if (uri == null || pair.first == null) {
                            return false;
                        }

                        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
                        matcher.addURI(uri.getEncodedAuthority(), uri.getEncodedPath(), UriMatcher.NO_MATCH + 1);
                        matcher.addURI(uri.getEncodedAuthority(), uri.getEncodedPath() + "/*", UriMatcher.NO_MATCH + 2);
                        return matcher.match(pair.first) != UriMatcher.NO_MATCH;
                    }
                });
    }

    public void registerUri(final Class<? extends Activity> clazz, final Uri uri) {
        asUriObservable(uri)
                .subscribe(new Consumer<Pair<Uri, Bundle>>() {
                    @Override
                    public void accept(Pair<Uri, Bundle> pair) throws Exception {
                        Intent intent = new Intent(mApplication, clazz);

                        // add some param that appended in uri
                        Set<String> keys = pair.first.getQueryParameterNames();
                        Iterator<String> it = keys.iterator();
                        while (it.hasNext()) {
                            String key = it.next();
                            if (!TextUtils.isEmpty(key)) {
                                String value = pair.first.getQueryParameter(key);
                                intent.putExtra(key, value);
                            }
                        }

                        // override the param if have same key
                        if (pair.second != null) {
                            intent.putExtras(pair.second);
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mApplication.startActivity(intent);
                    }
                });
    }

    /**
     * @param action eg: {@link android.content.Intent#ACTION_VIEW}, {@link android.content.Intent#ACTION_ALL_APPS}, ...
     */
    public void registerUri(final String action, final Uri uri) {
        asUriObservable(uri)
                .filter(new Predicate<Pair<Uri, Bundle>>() {
                    @Override
                    public boolean test(Pair<Uri, Bundle> pair) throws Exception {
                        return !TextUtils.isEmpty(action);
                    }
                })
                .subscribe(new Consumer<Pair<Uri, Bundle>>() {
                    @Override
                    public void accept(Pair<Uri, Bundle> pair) throws Exception {
                        Intent intent = new Intent(action, pair.first);

                        // add some param that appended in uri
                        if (pair.first != null) {
                            Set<String> keys = pair.first.getQueryParameterNames();
                            Iterator<String> it = keys.iterator();
                            while (it.hasNext()) {
                                String key = it.next();
                                if (!TextUtils.isEmpty(key)) {
                                    String value = pair.first.getQueryParameter(key);
                                    intent.putExtra(key, value);
                                }
                            }
                        }

                        // override the param if have same key
                        if (pair.second != null) {
                            intent.putExtras(pair.second);
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mApplication.startActivity(intent);
                    }
                });
    }


    public Consumer<? super Pair<String, Bundle>> asAliasConsumer() {
        return mAliasRouter.asConsumer();
    }

    public Observable<Pair<String, Bundle>> asAliasObservable(final String alias) {
        return mAliasRouter.asObservable()
                .filter(new Predicate<Pair<String, Bundle>>() {
                    @Override
                    public boolean test(Pair<String, Bundle> pair) throws Exception {
                        return !TextUtils.isEmpty(alias) && alias.equals(pair.first);
                    }
                });
    }

    public void registerAlias(final Class<? extends Activity> clazz, final String alias) {
        asAliasObservable(alias)
                .subscribe(new Consumer<Pair<String, Bundle>>() {
                    @Override
                    public void accept(Pair<String, Bundle> pair) throws Exception {
                        Intent intent = new Intent(mApplication, clazz);
                        if (pair.second != null) {
                            intent.putExtras(pair.second);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mApplication.startActivity(intent);
                    }
                });
    }
}
