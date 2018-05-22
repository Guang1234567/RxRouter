package com.github.gg.rxrouter;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.Pair;

import com.github.router.RxRouters;

import io.reactivex.functions.Consumer;

/**
 * @author Guang1234567
 * @date 2018/5/21 16:43
 */

public class ActivityNavigator {

    public static class NAVI_TO_SECOND {
        public static final String ALIAS_01 = "/ui/second_activity";
        public static final String ALIAS_02 = "/ui/second_activity";

        public static final String PARAM_AAA = "param_aaa";
    }

    private final RxRouters mRouters;

    public ActivityNavigator(Application application) {
        mRouters = new RxRouters(application);
        mRouters.registerAlias(SecondActivity.class, NAVI_TO_SECOND.ALIAS_01);
        mRouters.registerAlias(SecondActivity.class, NAVI_TO_SECOND.ALIAS_02);
    }

    public Consumer<? super Intent> naviByIntent() {
        return mRouters.naviByIntent();
    }

    public Consumer<? super Pair<Uri, Bundle>> naviByUri() {
        return mRouters.naviByUri();
    }

    public Consumer<? super Pair<String, Bundle>> naviByAlias() {
        return mRouters.naviByAlias();
    }
}
