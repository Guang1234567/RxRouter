package com.github.router;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author Guang1234567
 * @date 2018/2/27 11:03
 */

public class CoreRouter<T> implements IRouter<T> {

    public static final String TAG = "Router";

    private final Subject mTriggers;

    protected CoreRouter() {
        mTriggers = PublishSubject.create();
    }

    @Override
    public Observable<T> asObservable() {
        return mTriggers;
    }

    @Override
    public Consumer<? super T> asConsumer() {
        return new Consumer<T>() {
            @Override
            public void accept(T o) throws Exception {
                mTriggers.onNext(o);
            }
        };
    }
}
