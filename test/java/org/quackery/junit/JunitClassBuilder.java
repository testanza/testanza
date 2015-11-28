package org.quackery.junit;

import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;

import java.lang.annotation.Annotation;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.quackery.Quackery;
import org.quackery.Test;

class JunitClassBuilder {
  public final Builder<?> builder;

  private JunitClassBuilder(Builder<?> builder) {
    this.builder = builder;
  }

  public JunitClassBuilder() {
    this(new ByteBuddy()
        .subclass(Object.class)
        .name("JunitClass")
        .annotateType(annotationRunWith(QuackeryRunner.class)));
  }

  public JunitClassBuilder annotate(Annotation annotation) {
    return new JunitClassBuilder(builder.annotateType(annotation));
  }

  public JunitClassBuilder define(MethodDefinition def) {
    return new JunitClassBuilder(builder
        .defineMethod(def.name, def.returnType, def.parameters, def.modifiers)
        .intercept(implementationOf(def))
        .annotateMethod(def.annotations));
  }

  private static Implementation implementationOf(MethodDefinition def) {
    return def.throwing != null
        ? ExceptionMethod.throwing(def.throwing)
        : FixedValue.reference(def.returning);
  }

  public Class<?> load() {
    return builder
        .make()
        .load(Thread.currentThread().getContextClassLoader(), WRAPPER)
        .getLoaded();
  }

  public static MethodDefinition defaultQuackeryMethod() {
    return new MethodDefinition()
        .annotations(annotationQuackery())
        .modifiers(PUBLIC | STATIC)
        .returnType(Test.class)
        .name("test")
        .parameters();
  }

  public static MethodDefinition defaultJunitMethod() {
    return new MethodDefinition()
        .annotations(annotationJunitTest())
        .modifiers(PUBLIC)
        .returnType(void.class)
        .name("test")
        .parameters()
        .returning(null);
  }

  public static Annotation annotationRunWith(final Class<? extends Runner> type) {
    return new RunWith() {
      public Class<? extends Annotation> annotationType() {
        return RunWith.class;
      }

      public Class<? extends Runner> value() {
        return type;
      }
    };
  }

  private static Annotation annotationQuackery() {
    return new Quackery() {
      public Class<? extends Annotation> annotationType() {
        return Quackery.class;
      }
    };
  }

  public static Annotation annotationJunitTest() {
    return new org.junit.Test() {
      public Class<? extends Annotation> annotationType() {
        return org.junit.Test.class;
      }

      public Class<? extends Throwable> expected() {
        return None.class;
      }

      public long timeout() {
        return 0L;
      }
    };
  }

  public static Annotation annotationIgnore(final String reason) {
    return new Ignore() {
      public Class<? extends Annotation> annotationType() {
        return Ignore.class;
      }

      public String value() {
        return reason;
      }
    };
  }
}
