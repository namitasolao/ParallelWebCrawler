package com.udacity.webcrawler.tasks;

import com.udacity.webcrawler.Deadline;
import com.udacity.webcrawler.IgnoredUrls;
import com.udacity.webcrawler.MaxDepth;
import com.udacity.webcrawler.parser.PageParserFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class WordCountTaskFactory {

    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final Instant deadline;
    private final List<Pattern> ignoredUrls;

    @Inject
    public WordCountTaskFactory(
            Clock clock,
            PageParserFactory parserFactory,
            @Deadline Instant deadline,
            @IgnoredUrls List<Pattern> ignoredUrls
    ) {
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.deadline = deadline;
        this.ignoredUrls = ignoredUrls;
    }

    public WordCountTask get(
            String url,
            ConcurrentHashMap<String, Integer> count,
            Set<String> visitedUrls,
            int maxDepth
    ) {
        return new WordCountTask(
                url,
                count,
                visitedUrls,
                clock,
                parserFactory,
                deadline,
                maxDepth,
                ignoredUrls,
                this
        );
    }
}
