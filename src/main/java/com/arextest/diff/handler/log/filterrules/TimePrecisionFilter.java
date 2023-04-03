package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.log.LogEntity;
import com.fasterxml.jackson.databind.node.TextNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Predicate;

/**
 * Created by rchen9 on 2023/3/30.
 */
public class TimePrecisionFilter implements Predicate<LogEntity> {

    private static AbstractDataProcessor dataProcessor;

    private static SimpleDateFormat parseFormat1
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private static SimpleDateFormat parseFormat2
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static SimpleDateFormat parseFormat3
            = new SimpleDateFormat("HH:mm:ss.SSS");

    private long ignoredTimePrecision;


    static {
        parseFormat1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        parseFormat2.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        parseFormat3.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        parseFormat1.setLenient(false);
        parseFormat2.setLenient(false);
        parseFormat3.setLenient(false);

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

        if (baseValue instanceof TextNode && testValue instanceof TextNode) {
            String baseStr = ((TextNode) baseValue).asText();
            String testStr = ((TextNode) testValue).asText();

            int baseStrLen = baseStr.length();
            int testStrLen = testStr.length();
            if (baseStrLen < 12 || baseStrLen > 29
                    || testStrLen < 12 || testStrLen > 29) {
                return true;
            }

            if ((baseStr.startsWith("0") || baseStr.startsWith("1") || baseStr.startsWith("2")) &&
                    (testStr.startsWith("0") || testStr.startsWith("1") || testStr.startsWith("2"))) {
                Date baseTime = dataProcessor.process(baseStr);
                Date testTime = dataProcessor.process(testStr);
                if (baseTime == null || testTime == null) {
                    return true;
                }

                long durationMillis = baseTime.getTime() - testTime.getTime();
                if (Math.abs(durationMillis) <= ignoredTimePrecision) {
                    return false;
                }
            }
        }
        return true;
    }


    public static abstract class AbstractDataProcessor {
        private AbstractDataProcessor nextProcessor;

        public void setNextProcessor(AbstractDataProcessor nextProcessor) {
            this.nextProcessor = nextProcessor;
        }

        public Date process(String data) {
            Date date = processData(data);
            if (date != null) {
                return date;
            }
            if (this.nextProcessor == null) {
                return null;
            }
            return this.nextProcessor.process(data);
        }

        protected abstract Date processData(String data);
    }

    public static class FirstDataProcessor extends AbstractDataProcessor {

        @Override
        protected Date processData(String data) {

            Date time = null;
            try {
                time = parseFormat1.parse(data);
            } catch (ParseException e) {
            }
            return time;
        }
    }

    public static class SecondDataProcessor extends AbstractDataProcessor {

        @Override
        protected Date processData(String data) {
            Date time = null;
            try {
                time = parseFormat2.parse(data);
            } catch (ParseException e) {
            }
            return time;
        }
    }

    public static class ThirdDataProcessor extends AbstractDataProcessor {

        @Override
        protected Date processData(String data) {
            Date time = null;
            try {
                time = parseFormat3.parse(data);
            } catch (ParseException e) {
            }
            return time;

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