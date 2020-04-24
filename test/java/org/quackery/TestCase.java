package org.quackery;

import static org.junit.Assert.fail;
import static org.quackery.Case.newCase;
import static org.quackery.testing.Testing.assertEquals;
import static org.quackery.testing.Testing.assertTrue;
import static org.quackery.testing.Testing.nameOf;
import static org.quackery.testing.Testing.runAndThrow;

import java.util.concurrent.atomic.AtomicInteger;

public class TestCase {
  public static void test_case() throws Throwable {
    implements_test_interface();
    constructor_assigns_name();
    factory_assigns_name();
    factory_body_is_run_once();
    factory_body_is_run_each_time();
    factory_body_can_throw_exception();
    validates_arguments();
  }

  private static void implements_test_interface() {
    assertTrue(Test.class.isAssignableFrom(Case.class));
  }

  private static void constructor_assigns_name() {
    String name = "name";

    Case test = new Case(name) {
      public void run() {}
    };

    assertEquals(nameOf(test), name);
  }

  private static void factory_assigns_name() {
    String name = "name";

    Test test = newCase(name, () -> {});

    assertEquals(nameOf(test), name);
  }

  private static void factory_body_is_run_once() throws Throwable {
    AtomicInteger invoked = new AtomicInteger();
    Test test = newCase("name", () -> {
      invoked.incrementAndGet();
    });

    runAndThrow(test);

    assertEquals(invoked.get(), 1);
  }

  private static void factory_body_is_run_each_time() throws Throwable {
    AtomicInteger invoked = new AtomicInteger();
    Test test = newCase("name", () -> {
      invoked.incrementAndGet();
    });

    runAndThrow(test);
    runAndThrow(test);
    runAndThrow(test);

    assertEquals(invoked.get(), 3);
  }

  private static void factory_body_can_throw_exception() {
    Throwable throwable = new Throwable();
    Test test = newCase("name", () -> {
      throw throwable;
    });

    try {
      runAndThrow(test);
      fail();
    } catch (Throwable e) {
      assertEquals(e, throwable);
    }
  }

  private static void validates_arguments() {
    try {
      new Case(null) {
        public void run() {}
      };
      fail();
    } catch (QuackeryException e) {}
    try {
      newCase(null, () -> {});
      fail();
    } catch (QuackeryException e) {}
    try {
      newCase("name", null);
      fail();
    } catch (QuackeryException e) {}
  }
}
