package com.nightfarmer.coder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangfan on 16-8-26.
 */
public class Person {

    String name;
    List<Person> friends = new ArrayList<>();

    public Person(String name) {
        this.name = name;
    }

    public Person() {
    }

    @Override
    public String toString() {
        return "" + name;
    }
}
