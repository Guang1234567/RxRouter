package com.github.router;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.Pair;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * @author Guang1234567
 * @date 2018/5/22 14:01
 */

public final class RxRouters {

    private final Application mApplication;

    private final IRouter<Intent> mIntentRouter;

    private final IRouter<Pair<String, Bundle>> mAliasRouter;

    public RxRouters(Application application) {
        mApplication = application;

        mIntentRouter = createRouter();
        mIntentRouter.asObservable()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        mApplication.startActivity(intent);
                    }
                });

        mAliasRouter = createRouter();
    }

    private static <T> IRouter<T> createRouter() {
        return new CoreRouter<>();
    }

    public Consumer<? super Intent> naviByIntent() {
        return mIntentRouter.asConsumer();
    }

    public Consumer<? super Pair<Uri, Bundle>> naviByUri() {
        return new Consumer<Pair<Uri, Bundle>>() {
            @Override
            public void accept(Pair<Uri, Bundle> pair) throws Exception {
                Intent intent = new Intent(Intent.ACTION_VIEW, pair.first);
                if (pair.second != null) {
                    intent.putExtras(pair.second);
                }
                naviByIntent().accept(intent);
            }
        };
    }

    public void registerAlias(final Class<? extends Activity> clazz, final String alias) {
        mAliasRouter.asObservable()
                .filter(new Predicate<Pair<String, Bundle>>() {
                    @Override
                    public boolean test(Pair<String, Bundle> pair) throws Exception {
                        return alias.equals(pair.first);
                    }
                })
                .subscribe(new Consumer<Pair<String, Bundle>>() {
                    @Override
                    public void accept(Pair<String, Bundle> alias) throws Exception {
                        Intent intent = new Intent(mApplication, clazz);
                        if (alias.second != null) {
                            intent.putExtras(alias.second);
                        }
                        mApplication.startActivity(intent);
                    }
                });
    }

    public Consumer<? super Pair<String, Bundle>> naviByAlias() {
        return mAliasRouter.asConsumer();
    }
}
