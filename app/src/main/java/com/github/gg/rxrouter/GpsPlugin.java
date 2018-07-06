package com.github.gg.rxrouter;

import android.app.Application;
import android.graphics.PointF;
import android.os.Bundle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

public class GpsPlugin implements ObservableTransformer<Bundle, PointF> {

    private final Application mApplication;

    //private final GpsGoogleApi mGpsGoogleApi;

    public GpsPlugin(Application application/*, GpsGoogleApi api*/) {
        mApplication = application;
        //mGpsGoogleApi = api;
    }

    @Override
    public ObservableSource<PointF> apply(Observable<Bundle> upstream) {
        return upstream.map(new Function<Bundle, PointF>() {
            @Override
            public PointF apply(Bundle bundle) throws Exception {
                // simulate the position.
                // maybe you will use google map api here in dev.
                // eg: mGpsGoogleApi.getCurrentPos();
                return new PointF(0.0f, 1.1f);
            }
        });
    }
}
