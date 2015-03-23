package org.quackery.testing.bug;

import static java.util.Arrays.asList;
import static org.quackery.testing.Tests.run;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.quackery.AssumptionException;
import org.quackery.Case;
import org.quackery.Contract;
import org.quackery.FailureException;
import org.quackery.Suite;
import org.quackery.Test;

public class Expectations {
  public static void expectSuccess(Contract<Class<?>> contract, Class<?> implementation)
      throws Throwable {
    run(contract.test(implementation));
  }

  public static void expectFailure(Contract<Class<?>> contract, Class<?> implementation)
      throws Throwable {
    Test test = contract.test(implementation);
    List<Result> failures = new ArrayList<>();
    List<Result> errors = new ArrayList<>();
    for (Result result : runAndCatch(test)) {
      if (result.problem instanceof FailureException) {
        failures.add(result);
      } else if (result.problem == null) {

      } else if (result.problem instanceof AssumptionException) {

      } else {
        errors.add(result);
      }
    }

    boolean expected = failures.size() > 0 && errors.size() == 0;
    if (!expected) {
      StringBuilder builder = new StringBuilder();
      builder.append("\n<message>\n");
      builder.append("expectedFailure of " + implementation.getName());
      builder.append("\nfound ").append(failures.size()).append(" failures:");
      for (Result result : failures) {
        builder.append("\n").append(result.test.name).append("\n");
      }
      builder.append("\nfound ").append(errors.size()).append(" errors:");
      for (Result result : errors) {
        builder.append("\n").append(result.test.name).append("\n");
        builder.append(printStackTrace(result.problem));
      }
      builder.append("\n<end of message>");
      throw new AssertionError(builder.toString());
    }
  }

  private static String printStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  private static List<Result> runAndCatch(Test test) {
    if (test instanceof Case) {
      Case cas = (Case) test;
      try {
        cas.run();
        return asList(new Result(cas, null));
      } catch (Throwable t) {
        return asList(new Result(cas, t));
      }
    } else if (test instanceof Suite) {
      List<Result> results = new ArrayList<>();
      for (Test subtest : ((Suite) test).tests) {
        results.addAll(runAndCatch(subtest));
      }
      return results;
    } else {
      throw new RuntimeException("");
    }
  }

  private static class Result {
    public final Case test;
    public final Throwable problem;

    public Result(Case test, Throwable problem) {
      this.test = test;
      this.problem = problem;
    }
  }
}
