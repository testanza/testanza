package org.testanza;

import static org.testanza.TestMembers.hasModifier;
import static org.testanza.describe_testanza.verify;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import junit.framework.Test;
import junit.framework.TestCase;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class describe_TestMembers_has_modifier {
  private static Member member, otherMember;
  private static Test test, otherTest;
  private static Result result;
  private static String name, message;

  public static void succeeds_if_method_has_modifier() throws Throwable {
    @SuppressWarnings("unused")
    class TestClass {
      final void testMethod() {}
    }
    member = TestClass.class.getDeclaredMethod("testMethod");
    test = hasModifier(Modifier.FINAL).test(member);
    result = new JUnitCore().run(test);
    verify(0 == result.getFailureCount());
  }

  public static void fails_if_method_has_no_modifier() throws Throwable {
    @SuppressWarnings("unused")
    class TestClass {
      void testMethod() {}
    }
    member = TestClass.class.getDeclaredMethod("testMethod");
    test = hasModifier(Modifier.FINAL).test(member);
    result = new JUnitCore().run(test);
    verify(1 == result.getFailureCount());
  }

  public static void failure_prints_message() throws Throwable {
    @SuppressWarnings("unused")
    class TestClass {
      void foo() {}
    }
    member = TestClass.class.getDeclaredMethod("foo");
    test = hasModifier(Modifier.FINAL).test(member);
    result = new JUnitCore().run(test);

    message = result.getFailures().get(0).getMessage();
    verify(message.contains("" //
        + "\n" //
        + "  expected that\n" //
        + "    method " + member.toString() + "\n" //
        + "  has modifier\n" //
        + "    final\n" //
    ));
  }

  public static void test_name_contains_modifier() throws Throwable {
    @SuppressWarnings("unused")
    class TestClass {
      void testMethod() {}
    }
    member = TestClass.class.getDeclaredMethod("testMethod");
    test = hasModifier(Modifier.FINAL).test(member);
    name = ((TestCase) test).getName();
    verify(name.contains("final"));
  }

  public static void test_name_contains_member_type_and_siple_name() throws Throwable {
    @SuppressWarnings("unused")
    class TestClass {
      TestClass() {}

      Object foo;

      void foo() {}
    }
    member = TestClass.class.getDeclaredMethod("foo");
    test = hasModifier(Modifier.FINAL).test(member);
    name = ((TestCase) test).getName();
    verify(name.contains("method foo"));

    member = TestClass.class.getDeclaredField("foo");
    test = hasModifier(Modifier.FINAL).test(member);
    name = ((TestCase) test).getName();
    verify(name.contains("field foo"));

    member = TestClass.class.getDeclaredConstructor();
    test = hasModifier(Modifier.FINAL).test(member);
    name = ((TestCase) test).getName();
    verify(name.contains("constructor TestClass"));
  }

  public static void test_name_differs_even_if_members_have_same_simple_name() throws Throwable {
    @SuppressWarnings("unused")
    class TestClass {
      void foo() {}

      void foo(Object o) {}
    }
    member = TestClass.class.getDeclaredMethod("foo");
    otherMember = TestClass.class.getDeclaredMethod("foo", Object.class);
    test = hasModifier(Modifier.FINAL).test(member);
    otherTest = hasModifier(Modifier.FINAL).test(otherMember);
    verify(!((TestCase) test).getName().equals(((TestCase) otherTest).getName()));
  }

  public static void test_name_is_same_for_equal_member() throws Throwable {
    // TODO ignored
    if (true) {
      return;
    }
    @SuppressWarnings("unused")
    class TestClass {
      void foo() {}

      void foo(Object o) {}
    }
    member = TestClass.class.getDeclaredMethod("foo");
    otherMember = TestClass.class.getDeclaredMethod("foo");
    test = hasModifier(Modifier.FINAL).test(member);
    otherTest = hasModifier(Modifier.FINAL).test(otherMember);
    verify(((TestCase) test).getName().equals(((TestCase) otherTest).getName()));
  }
}
