package org.quackery;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.quackery.QuackeryException.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Suite implements Test {
  public final String name;
  public final List<Test> tests;

  private Suite(String name, List<Test> tests) {
    this.name = name;
    this.tests = tests;
  }

  private static Suite newSuite(String name, List<Test> tests) {
    check(name != null);
    check(tests != null);
    Suite suite = new Suite(name, unmodifiableList(new ArrayList<Test>(tests)));
    check(!suite.tests.contains(null));
    return suite;
  }

  public static Suite newSuite(String name) {
    check(name != null);
    return newSuite(name, Arrays.<Test> asList());
  }

  public Suite test(Test extraTest) {
    check(extraTest != null);
    ArrayList<Test> allTests = new ArrayList<>();
    allTests.addAll(tests);
    allTests.add(extraTest);
    return newSuite(name, allTests);
  }

  public Suite testAll(Iterable<? extends Test> extraTests) {
    check(extraTests != null);
    ArrayList<Test> allTests = new ArrayList<>();
    allTests.addAll(tests);
    for (Test extraTest : extraTests) {
      check(extraTest != null);
      allTests.add(extraTest);
    }
    return newSuite(name, allTests);
  }

  public Suite testAll(Test[] extraTests) {
    check(extraTests != null);
    return testAll(asList(extraTests));
  }

  public <T> Suite testThat(T item, Tester<T> tester) {
    check(tester != null);
    return test(tester.test(item));
  }

  public <T> Suite testThatAll(Iterable<? extends T> items, Tester<T> tester) {
    check(items != null);
    check(tester != null);
    List<Test> extraTests = new ArrayList<>();
    for (T item : items) {
      extraTests.add(tester.test(item));
    }
    return testAll(extraTests);
  }

  public <T> Suite testThatAll(T[] items, Tester<T> tester) {
    check(items != null);
    return testThatAll(asList(items), tester);
  }

  public String toString() {
    return name;
  }
}