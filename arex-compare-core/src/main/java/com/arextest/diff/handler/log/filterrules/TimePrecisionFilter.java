package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.log.LogEntity;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.function.Predicate;

/**
 * Created by rchen9 on 2023/3/30.
 */
public class TimePrecisionFilter implements Predicate<LogEntity> {

    private static AbstractDataProcessor dataProcessor;
    private static DateTimeFormatter parseFormat1 = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .optionalStart().appendLiteral(' ').optionalEnd()
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
            .toFormatter();
    private static DateTimeFormatter parseFormat2 = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
            .toFormatter();
    private static DateTimeFormatter parseFormat3 = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .optionalStart().appendLiteral('T').optionalEnd()
            .optionalStart().appendLiteral(' ').optionalEnd()
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSSXXX"))
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss.SSSZ"))
            .toFormatter();

    private long ignoredTimePrecision;


    static {
        parseFormat1 = parseFormat1.withZone(ZoneId.of("UTC"));

        dataProcessor = new ProcessorChainBuilder()
                .addProcessor(new FirstDataProcessor())
                .addProcessor(new SecondDataProcessor())
                .addProcessor(new ThirdDataProcessor())
                .build();
    }

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
        if (baseStrLen < 12 || baseStrLen > 29
                || testStrLen < 12 || testStrLen > 29) {
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


    public static abstract class AbstractDataProcessor {
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

    public static class FirstDataProcessor extends AbstractDataProcessor {

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

    public static class SecondDataProcessor extends AbstractDataProcessor {

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

    public static class ThirdDataProcessor extends AbstractDataProcessor {

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

    public static class ProcessorChainBuilder {
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