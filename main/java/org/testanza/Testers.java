package org.testanza;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class Testers {
  public static <T> Tester<T> asTester(final Matcher<T> matcher) {
    return new CaseTester<T>() {
      protected String name(T item) {
        return item + " is " + matcher.toString();
      }

      protected void body(T item) throws Throwable {
        if (!matcher.matches(item)) {
          throw new TestanzaAssertionError("" //
              + "  expected that\n" //
              + "    " + item + "\n" //
              + "  matches\n" //
              + "    " + matcher + "\n" //
              + "  but\n" //
              + "    " + diagnose(item, matcher) + "\n" //
          );
        }
      }
    };
  }

  private static <T> String diagnose(T item, Matcher<T> matcher) {
    StringDescription description = new StringDescription();
    matcher.describeMismatch(item, description);
    return description.toString();
  }
}
