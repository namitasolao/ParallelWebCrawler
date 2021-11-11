package com.udacity.webcrawler.crawler;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Crawl {
    public static boolean validate(
            int maxDepth,
            Clock clock,
            Instant deadline,
            String url,
            List<Pattern> ignoredUrls,
            Set<String> visitedUrls
    ) {
        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return false;
        }
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return false;
            }
        }
        if (visitedUrls.contains(url)) {
            return false;
        }

        return true;
    }
}
