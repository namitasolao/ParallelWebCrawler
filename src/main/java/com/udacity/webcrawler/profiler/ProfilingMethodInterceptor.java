package com.udacity.webcrawler.profiler;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final ProfilingState profilingState;
  private final Path profileDataPath;
  private final Profiler profiler;
  private final Object target;

  ProfilingMethodInterceptor(
          Clock clock,
          ProfilingState profilingState,
          Object target,
          Profiler profiler,
          @Nullable String profileDataPath
  ) {
    this.clock = Objects.requireNonNull(clock);
    this.profilingState = Objects.requireNonNull(profilingState);
    this.target = Objects.requireNonNull(target);
    this.profiler = profiler;
    if (profileDataPath == null) {
      this.profileDataPath = null;
    } else {
      this.profileDataPath = Path.of(profileDataPath);
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    Object result = null;
    Throwable throwable = null;
    Temporal startTime = clock.instant();
    try {
      result = method.invoke(target, args);
    } catch (InvocationTargetException ex) {
      throwable = ex.getTargetException();
    }
    Temporal endTime = clock.instant();

    profilingState.record(
            target.getClass(),
            method,
            Duration.between(startTime, endTime)
    );

    profiler.writeData(profileDataPath);

    if (throwable == null) {
      return result;
    } else {
      throw throwable;
    }
  }
}
