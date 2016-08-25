package com.nightfarmer.coder;

import android.os.SystemClock;

import org.junit.Test;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.print("hehe");
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testRx() {
        Observable.range(1, 5)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .toBlocking()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }
}