package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;
import com.udacity.webcrawler.tasks.WordCountTask;
import com.udacity.webcrawler.tasks.WordCountTaskFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a
 * {@link ForkJoinPool} to fetch and process multiple web pages in parallel.
 */
public final class ParallelWebCrawler implements WebCrawler {
  private final int popularWordCount;
  private final int maxDepth;
  private final ForkJoinPool pool;
  private final WordCountTaskFactory wordCountTaskFactory;

  private final ConcurrentHashMap<String , Integer> counts = new ConcurrentHashMap<>();
  private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

  @Inject
  ParallelWebCrawler(
      @PopularWordCount int popularWordCount,
      @TargetParallelism int threadCount,
      @MaxDepth int maxDepth,
      WordCountTaskFactory wordCountTaskFactory
  ) {
    this.maxDepth = maxDepth;
    this.popularWordCount = popularWordCount;
    this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));
    this.wordCountTaskFactory = wordCountTaskFactory;
  }

  @Override
  public CrawlResult crawl(List<String> startingUrls) {
    for (String url: startingUrls) {
      pool.invoke(
              wordCountTaskFactory.get(
                      url,
                      counts,
                      visitedUrls,
                      maxDepth
              )
      );
    }

    if (counts.isEmpty()) {
      return new CrawlResult.Builder()
              .setWordCounts(counts)
              .setUrlsVisited(visitedUrls.size())
              .build();
    }

    return new CrawlResult.Builder()
            .setWordCounts(WordCounts.sort(counts, popularWordCount))
            .setUrlsVisited(visitedUrls.size())
            .build();
  }

  @Override
  public int getMaxParallelism() {
    return Runtime.getRuntime().availableProcessors();
  }
}
