package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.log.LogEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.function.Predicate;

/**
 * Created by rchen9 on 2023/3/30.
 */
public class TimePrecisionFilter implements Predicate<LogEntity> {


  private static final int MIN_TIME_LENGTH = 8;

  private static final int MAX_TIME_LENGTH = 29;

  private static AbstractDataProcessor dataProcessor;
  private static DateTimeFormatter parseFormat1 = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd")
      .optionalStart().appendLiteral(' ').optionalEnd()
      .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"))
      .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
      .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss"))
      .toFormatter();
  private static DateTimeFormatter parseFormat2 = new DateTimeFormatterBuilder()
      .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"))
      .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
      .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss"))
      .toFormatter();
  private static DateTimeFormatter parseFormat3 = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd")
      .optionalStart().appendLiteral('T').optionalEnd()
      .optionalStart().appendLiteral(' ').optionalEnd()
      .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSSXXX"))
      .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSSZ"))
      .toFormatter();

  static {
    parseFormat1 = parseFormat1.withZone(ZoneId.of("UTC"));

    dataProcessor = new ProcessorChainBuilder()
        .addProcessor(new FirstDataProcessor())
        .addProcessor(new SecondDataProcessor())
        .addProcessor(new ThirdDataProcessor())
        .build();
  }

  private long ignoredTimePrecision;

  public TimePrecisionFilter(long ignoredTimePrecision) {
    this.ignoredTimePrecision = ignoredTimePrecision;
  }

  public void setIgnoredTimePrecision(long ignoredTimePrecision) {
    this.ignoredTimePrecision = ignoredTimePrecision;
  }

  @Override
  public boolean test(LogEntity logEntity) {
    Object baseValue = logEntity.getBaseValue();
    Object testValue = logEntity.getTestValue();
    if (baseValue == null || testValue == null) {
      return true;
    }

    String baseStr = (String) baseValue;
    String testStr = (String) testValue;

    int baseStrLen = baseStr.length();
    int testStrLen = testStr.length();
    if (baseStrLen < MIN_TIME_LENGTH || baseStrLen > MAX_TIME_LENGTH
        || testStrLen < MIN_TIME_LENGTH || testStrLen > MAX_TIME_LENGTH) {
      return true;
    }

    if ((baseStr.startsWith("0") || baseStr.startsWith("1") || baseStr.startsWith("2")) &&
        (testStr.startsWith("0") || testStr.startsWith("1") || testStr.startsWith("2"))) {
      Instant baseTime = dataProcessor.process(baseStr);
      Instant testTime = dataProcessor.process(testStr);
      if (baseTime == null || testTime == null) {
        return true;
      }

      long durationMillis = baseTime.toEpochMilli() - testTime.toEpochMilli();
      if (Math.abs(durationMillis) <= ignoredTimePrecision) {
        return false;
      }
    }
    return true;
  }

  public Instant identifyTime(String data) {
    if (data == null || data.length() < MIN_TIME_LENGTH || data.length() > MAX_TIME_LENGTH) {
      return null;
    }

    if ((data.startsWith("0") || data.startsWith("1") || data.startsWith("2"))) {
      Instant baseTime = dataProcessor.process(data);
      if (baseTime == null) {
        return null;
      }
      return baseTime;
    }
    return null;
  }


  private static abstract class AbstractDataProcessor {

    private AbstractDataProcessor nextProcessor;

    public void setNextProcessor(AbstractDataProcessor nextProcessor) {
      this.nextProcessor = nextProcessor;
    }

    public Instant process(String data) {
      Instant date = processData(data);
      if (date != null) {
        return date;
      }
      if (this.nextProcessor == null) {
        return null;
      }
      return this.nextProcessor.process(data);
    }

    protected abstract Instant processData(String data);
  }

  private static class FirstDataProcessor extends AbstractDataProcessor {

    @Override
    protected Instant processData(String data) {

      Instant instant = null;
      try {
        ZonedDateTime zdt = ZonedDateTime.parse(data, parseFormat1);
        instant = zdt.toInstant();
      } catch (Exception e) {
      }
      return instant;
    }
  }

  private static class SecondDataProcessor extends AbstractDataProcessor {

    @Override
    protected Instant processData(String data) {
      Instant instant = null;
      try {
        LocalTime time = LocalTime.parse(data, parseFormat2);
        LocalDate date = LocalDate.ofEpochDay(0);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        instant = dateTime.toInstant(ZoneOffset.UTC);
      } catch (Exception e) {
      }
      return instant;

    }
  }

  private static class ThirdDataProcessor extends AbstractDataProcessor {

    @Override
    protected Instant processData(String data) {
      Instant instant = null;
      try {
        ZonedDateTime zdt = ZonedDateTime.parse(data, parseFormat3);
        instant = zdt.toInstant();
      } catch (Exception e) {
      }
      return instant;
    }
  }

  private static class ProcessorChainBuilder {

    private AbstractDataProcessor firstProcessor;
    private AbstractDataProcessor lastProcessor;

    public ProcessorChainBuilder addProcessor(AbstractDataProcessor processor) {
      if (firstProcessor == null) {
        firstProcessor = processor;
      } else {
        lastProcessor.setNextProcessor(processor);
      }
      lastProcessor = processor;
      return this;
    }

    public AbstractDataProcessor build() {
      return firstProcessor;
    }
  }

}