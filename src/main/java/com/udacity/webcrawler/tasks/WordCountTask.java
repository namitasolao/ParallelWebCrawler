package com.udacity.webcrawler.tasks;

import com.udacity.webcrawler.crawler.Crawl;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public class WordCountTask extends RecursiveAction {

    private final ConcurrentHashMap<String , Integer> counts;
    private final Set<String> visitedUrls;
    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final Instant deadline;
    private final int maxDepth;
    private final List<Pattern> ignoredUrls;
    private final String url;
    private final WordCountTaskFactory wordCountTaskFactory;

    WordCountTask(
            String url,
            ConcurrentHashMap<String, Integer> counts,
            Set<String> visitedUrls,
            Clock clock,
            PageParserFactory parserFactory,
            Instant deadline,
            int maxDepth,
            List<Pattern> ignoredUrls,
            WordCountTaskFactory wordCountTaskFactory
    ) {
        this.url = url;
        this.counts = counts;
        this.visitedUrls = visitedUrls;
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.deadline = deadline;
        this.maxDepth = maxDepth;
        this.ignoredUrls = ignoredUrls;
        this.wordCountTaskFactory = wordCountTaskFactory;
    }

    @Override
    protected void compute() {
        if (!Crawl.validate(
                maxDepth,
                clock,
                deadline,
                url,
                ignoredUrls,
                visitedUrls
        )) {
            return;
        }

        // Visited URLs
        visitedUrls.add(url);

        // Web page crawling
        PageParser.Result result = parserFactory.get(url).parse();

        for (Map.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
            counts.computeIfPresent(
                    e.getKey(),
                    new BiFunction<String, Integer, Integer>() {
                        @Override
                        public Integer apply(String s, Integer integer) {
                            return integer + e.getValue();
                        }
                    }
            );
            counts.computeIfAbsent(
                    e.getKey(),
                    new Function<String, Integer>() {
                        @Override
                        public Integer apply(String s) {
                            return e.getValue();
                        }
                    }
            );
        }

        // Invoke other operations
        for(String link : result.getLinks()) {
            invokeAll(
                    wordCountTaskFactory.get(
                            link,
                            counts,
                            visitedUrls,
                            maxDepth - 1
                    )
            );
        }
    }
}
