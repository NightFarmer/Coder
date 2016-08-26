package com.nightfarmer.coder

import rx.Observable
import rx.Subscriber
import rx.functions.Action1
import rx.functions.Func1

/**
 * Created by zhangfan on 16-8-26.
 */
class TestA {

    fun hehe() {
        Observable.just(1, 2).flatMap(Func1<kotlin.Int, rx.Observable<kotlin.String>> { Observable.create { subscriber -> subscriber.onNext("sss") } }).subscribe { }
    }



}
