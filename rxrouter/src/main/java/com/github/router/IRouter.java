package com.github.router;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * @author Guang1234567
 * @date 2018/5/21 17:48
 */

public interface IRouter<T> {

    Observable<T> asObservable();

    Consumer<? super T> asConsumer();
}
