package org.quackery.junit;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static org.junit.runner.Description.createSuiteDescription;
import static org.junit.runner.Description.createTestDescription;
import static org.quackery.Suite.suite;
import static org.quackery.help.Helpers.failingCase;
import static org.quackery.junit.FixBugs.fixBugs;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.QuackeryException;
import org.quackery.Suite;
import org.quackery.Test;
import org.quackery.help.TraversingDecorator;
import org.quackery.report.AssertException;
import org.quackery.report.AssumeException;
import org.quackery.run.Runners;

public class QuackeryRunner extends Runner {
  private final Class<?> annotatedClass;
  private Description description;
  private Runner junitRunner;
  private Test quackeryTest;

  /** This constructor is required by Runner contract and is invoked by junit. */
  public QuackeryRunner(Class<?> annotatedClass) {
    this.annotatedClass = annotatedClass;
    try {
      junitRunner = new BlockJUnit4ClassRunner(annotatedClass);
      description = junitRunner.getDescription();
      quackeryTest = fixBugs(instantiateQuackerySuite(annotatedClass));
      for (Description childDescription : describe(quackeryTest).getChildren()) {
        description.addChild(childDescription);
      }
    } catch (InitializationError error) {
      quackeryTest = fixBugs(instantiateQuackerySuite(annotatedClass)
          .addAll(instantiateFailingTestsExplainingCausesOf(error)));
      description = describe(quackeryTest);
    }
  }

  public Description getDescription() {
    return description;
  }

  public void run(final RunNotifier notifier) {
    Runners.run(new TraversingDecorator() {
      protected Case decorateCase(Case cas) {
        return notifying(notifier, cas);
      }
    }.decorate(quackeryTest));

    if (junitRunner != null) {
      junitRunner.run(notifier);
    }
  }

  private Case notifying(final RunNotifier notifier, final Case cas) {
    return new Case(cas.name) {
      public void run() throws Throwable {
        Description described = describe(cas);
        notifier.fireTestStarted(described);
        try {
          cas.run();
        } catch (AssertException e) {
          Throwable wrapper = new AssertionError(e.getMessage(), e);
          notifier.fireTestFailure(new Failure(described, wrapper));
        } catch (AssumeException e) {
          Throwable wrapper = new AssumptionViolatedException(e.getMessage(), e);
          notifier.fireTestAssumptionFailed(new Failure(described, wrapper));
        } catch (Throwable throwable) {
          notifier.fireTestFailure(new Failure(described, throwable));
        } finally {
          notifier.fireTestFinished(described);
        }
      }
    };
  }

  private static Suite instantiateQuackerySuite(Class<?> testClass) {
    List<Test> tests = new ArrayList<>();
    for (Method method : testClass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Quackery.class)) {
        tests.add(instantiateQuackeryTestReturnedBy(method));
      }
    }
    return suite(testClass.getName())
        .addAll(tests);
  }

  private static Test instantiateQuackeryTestReturnedBy(Method method) {
    if (!isPublic(method.getModifiers())) {
      return fail(method, "method must be public");
    } else if (!isStatic(method.getModifiers())) {
      return fail(method, "method must be static");
    } else if (!Test.class.isAssignableFrom(method.getReturnType())) {
      return fail(method, "method return type must be assignable to " + Test.class.getName());
    } else if (method.getParameterTypes().length > 0) {
      return fail(method, "method cannot have parameters");
    }
    try {
      return (Test) method.invoke(null);
    } catch (InvocationTargetException e) {
      return failingCase(method.getName(), e.getCause());
    } catch (IllegalAccessException e) {
      throw new Error(e);
    }
  }

  private static Case fail(Method method, String message) {
    return failingCase(method.getName(), new QuackeryException(message));
  }

  private static List<Test> instantiateFailingTestsExplainingCausesOf(InitializationError error) {
    boolean hasJunitTestMethods = hasJunitTestMethods(error);

    List<Test> testsExplainingErrors = new ArrayList<>();
    for (Throwable cause : error.getCauses()) {
      if (noRunnableMethods(cause)) {
        continue;
      } else if (noPublicDefaultConstructor(cause) && !hasJunitTestMethods) {
        continue;
      } else if (incorrectJunitTestMethod(cause)) {
        testsExplainingErrors.add(failingCase(junitTestMethodName(cause), cause));
      } else {
        testsExplainingErrors.add(failingCase(cause.getMessage(), cause));
      }
    }
    return testsExplainingErrors;
  }

  private static boolean hasJunitTestMethods(InitializationError error) {
    for (Throwable cause : error.getCauses()) {
      if (noRunnableMethods(cause)) {
        return false;
      }
    }
    return true;
  }

  private static boolean noRunnableMethods(Throwable cause) {
    return cause.getMessage().equals("No runnable methods");
  }

  private static boolean noPublicDefaultConstructor(Throwable cause) {
    String message = cause.getMessage();
    return message.equals("Test class should have exactly one public constructor")
        || message.equals("Test class should have exactly one public zero-argument constructor");
  }

  private static boolean incorrectJunitTestMethod(Throwable cause) {
    String message = cause.getMessage();
    return message.startsWith("Method")
        && message.contains(" should ");
  }

  private static String junitTestMethodName(Throwable cause) {
    String message = cause.getMessage();
    int begin = "Method ".length();
    int end = message.contains("() should")
        ? message.indexOf("() should")
        : message.indexOf(" should");
    return message.substring(begin, end);
  }

  private Description describe(Test test) {
    if (test instanceof Suite) {
      return describe((Suite) test);
    } else if (test instanceof Case) {
      return describe((Case) test);
    } else {
      throw new QuackeryException();
    }
  }

  private Description describe(Suite suite) {
    Description parent = createSuiteDescription(suite.name, id(suite));
    for (Test child : suite.tests) {
      parent.addChild(describe(child));
    }
    return parent;
  }

  private Description describe(Case cas) {
    return createTestDescription(annotatedClass.getName(), cas.name, id(cas));
  }

  private static Serializable id(Test test) {
    final int id = System.identityHashCode(test);
    return new Serializable() {
      public boolean equals(Object obj) {
        return getClass() == obj.getClass() && hashCode() == obj.hashCode();
      }

      public int hashCode() {
        return id;
      }

      public String toString() {
        return Integer.toString(id);
      }
    };
  }
}
