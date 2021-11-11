package com.udacity.webcrawler;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class DeadlineProvider implements Provider<Instant> {
    private final Duration timeout;
    private final Clock clock;

    @Inject
    public DeadlineProvider(
            Clock clock,
            @Timeout Duration timeout
    ) {
        this.clock = clock;
        this.timeout = timeout;
    }

    @Override
    public Instant get() {
        return clock.instant().plus(timeout);
    }
}
