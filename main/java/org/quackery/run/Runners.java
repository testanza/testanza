package org.quackery.run;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.quackery.QuackeryException.check;
import static org.quackery.help.Helpers.traverseBodies;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.quackery.Body;
import org.quackery.Test;
import org.quackery.report.AssertException;

public class Runners {
  public static Test run(Test root) {
    check(root != null);
    return traverseBodies(root, body -> run(body));
  }

  private static Body run(Body body) {
    try {
      body.run();
    } catch (Throwable throwable) {
      return () -> {
        throw throwable;
      };
    }
    return () -> {};
  }

  public static Test in(Executor executor, Test root) {
    check(root != null);
    check(executor != null);
    return traverseBodies(root, body -> futureBody(executor, body));
  }

  private static Body futureBody(Executor executor, Body body) {
    FutureTask<Body> future = new FutureTask<Body>(() -> run(body));
    executor.execute(future);
    return () -> future.get().run();
  }

  public static Test concurrent(Test test) {
    return in(concurrentExecutor, test);
  }

  private static final ExecutorService concurrentExecutor = concurrentExecutor();

  private static ThreadPoolExecutor concurrentExecutor() {
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    int corePoolSize = availableProcessors;
    int maximumPoolSize = availableProcessors;
    int keepAliveTime = 1;
    TimeUnit keepAliveTimeUnit = NANOSECONDS;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        corePoolSize, maximumPoolSize,
        keepAliveTime, keepAliveTimeUnit,
        new LinkedBlockingQueue<Runnable>());
    executor.allowCoreThreadTimeOut(true);
    return executor;
  }

  public static Test expect(Class<? extends Throwable> throwable, Test test) {
    check(test != null);
    return traverseBodies(test, body -> expect(throwable, body));
  }

  private static Body expect(Class<? extends Throwable> expected, Body body) {
    return () -> {
      Throwable thrown = null;
      try {
        body.run();
      } catch (Throwable throwable) {
        thrown = throwable;
      }
      if (thrown == null) {
        throw new AssertException("nothing thrown");
      }
      if (!expected.isAssignableFrom(thrown.getClass())) {
        throw new AssertException(thrown);
      }
    };
  }

  public static Test timeout(double time, Test test) {
    check(time >= 0);
    check(test != null);
    return traverseBodies(test, body -> timeout(time, body));
  }

  private static Body timeout(double time, Body body) {
    return () -> {
      Thread caller = Thread.currentThread();
      ScheduledFuture<?> alarm = timeoutScheduler.schedule(
          () -> caller.interrupt(),
          (long) (time * 1e9),
          NANOSECONDS);
      try {
        body.run();
      } finally {
        alarm.cancel(true);
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
    };
  }

  private static final ScheduledExecutorService timeoutScheduler = new ScheduledThreadPoolExecutor(0);

  public static Test threadScoped(Test root) {
    check(root != null);
    return traverseBodies(root, body -> threadScoped(body));
  }

  private static Body threadScoped(Body body) {
    return () -> {
      AtomicReference<Throwable> throwable = new AtomicReference<>(null);
      Thread thread = new Thread(new Runnable() {
        public void run() {
          try {
            body.run();
          } catch (Throwable t) {
            throwable.set(t);
          }
        }
      });
      thread.start();
      try {
        thread.join();
      } catch (InterruptedException e) {
        thread.interrupt();
        thread.join();
        throw e;
      }
      if (throwable.get() != null) {
        throw throwable.get();
      }
    };
  }

  public static Test classLoaderScoped(Test root) {
    check(root != null);
    return traverseBodies(root, body -> classLoaderScoped(body));
  }

  private static Body classLoaderScoped(Body body) {
    return () -> {
      Thread thread = Thread.currentThread();
      ClassLoader original = thread.getContextClassLoader();
      thread.setContextClassLoader(new ClassLoader(original) {});
      try {
        body.run();
      } finally {
        thread.setContextClassLoader(original);
      }
    };
  }
}
