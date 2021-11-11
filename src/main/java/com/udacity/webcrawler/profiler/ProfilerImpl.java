package com.udacity.webcrawler.profiler;

import com.udacity.webcrawler.json.CrawlerConfiguration;

import javax.inject.Inject;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler {

  private final Clock clock;
  private final CrawlerConfiguration crawlerConfiguration;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(
          Clock clock,
          CrawlerConfiguration crawlerConfiguration
  ) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
    this.crawlerConfiguration = crawlerConfiguration;
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {
    Objects.requireNonNull(klass);

    Method[] methods = klass.getMethods();
    boolean isProfiled = false;
    for (Method method : methods) {
      isProfiled = method.getAnnotation(Profiled.class) != null;
      if (isProfiled) {
        break;
      }
    }

    if (!isProfiled) {
      throw new IllegalArgumentException("Missing Profiled annotated methods");
    }

    T proxy = (T) Proxy.newProxyInstance(
            delegate.getClass().getClassLoader(),
            delegate.getClass().getInterfaces(),
            new ProfilingMethodInterceptor(
                    clock,
                    state,
                    delegate,
                    this,
                    crawlerConfiguration.getProfileOutputPath()
            )
    );

    return proxy;
  }

  @Override
  public void writeData(Path path) {
    try {
      FileWriter fileWriter = new FileWriter(path.toFile(), true);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

      state.write(bufferedWriter);

      bufferedWriter.close();
      fileWriter.close();
    } catch (IOException e) {
      System.out.println(
              "IOException when writing tp path : " +
                      path + ", Exception : " + e.getMessage()
      );
    }
  }

  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }
}
