package com.nightfarmer.coder;

import android.os.SystemClock;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by zhangfan on 16-8-26.
 */
public class TestJavaCode {

    @Test
    public void test1() {
        Observable.range(1, 5)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer % 2 == 1;
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "hehe" + integer;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String str) {
                        System.out.println(str);
                    }
                });
    }


    @Test
    public void test2() throws InterruptedException {
        Person person = new Person("zhangsan");
        person.friends.add(new Person("1"));
        person.friends.add(new Person("2"));
        person.friends.add(new Person("3"));
        person.friends.add(new Person("4"));
        Person person2 = new Person("lisi");
        person2.friends.add(new Person("3"));
        person2.friends.add(new Person("4"));
        person2.friends.add(new Person("5"));
        Person person3 = new Person("wangwu");
        person3.friends.add(new Person("2"));
        person3.friends.add(new Person("4"));
        person3.friends.add(new Person("6"));

        Observable.just(person, person2, person3)
                .flatMap(new Func1<Person, Observable<Person>>() {
                    @Override
                    public Observable<Person> call(Person person) {
                        return Observable.from(person.friends);
                    }
                })
                .filter(new Func1<Person, Boolean>() {
                    @Override
                    public Boolean call(Person person) {
                        return !"1".equals(person.name);
                    }
                })
                .observeOn(Schedulers.newThread())
                .startWith(new Person("9999"))
                .sorted(new Func2<Person, Person, Integer>() {
                    @Override
                    public Integer call(Person person, Person person2) {
                        try {
                            Thread.sleep(1000);
                            System.out.println("calc once" + Thread.currentThread().getName());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Integer integer = Integer.valueOf(person.name);
                        Integer integer1 = Integer.valueOf(person2.name);
                        return integer - integer1;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Person>() {
                    @Override
                    public void call(Person person) {
                        System.out.println(person + Thread.currentThread().getName());
                    }
                });
        System.out.print("end");

        Thread.sleep(10099900);
    }


}
