package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class to write a {@link CrawlResult} to file.
 */
public final class CrawlResultWriter {
  private final CrawlResult result;

  /**
   * Creates a new {@link CrawlResultWriter} that will write the given {@link CrawlResult}.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Path}.
   *
   * <p>If a file already exists at the path, the existing file should not be deleted; new data
   * should be appended to it.
   *
   * @param path the file path where the crawl result data should be written.
   */
  public void write(Path path) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(path);
    try {
      FileWriter writer = new FileWriter(path.toFile());
      BufferedWriter bufferedWriter = new BufferedWriter(writer);
      write(bufferedWriter);
    } catch (IOException e) {
      System.out.println(
              "IOException when writing tp path : " +
                      path + ", Exception : " + e.getMessage()
      );
    }
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Writer}.
   *
   * @param writer the destination where the crawl result data should be written.
   */
  public void write(Writer writer) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(writer);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
      objectMapper.writeValue(writer , result);
    } catch (Exception e){
      System.out.println("Exception when writing using writer : " + e.getMessage());
    }
  }
}
