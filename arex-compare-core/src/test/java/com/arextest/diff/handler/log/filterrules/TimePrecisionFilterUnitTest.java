package com.arextest.diff.handler.log.filterrules;

import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TimePrecisionFilterUnitTest {


  private TimePrecisionFilter timePrecisionFilter = new TimePrecisionFilter(1000);


  @Test
  public void testIdentifyTime1() {

    Instant instant = null;

    instant = timePrecisionFilter.identifyTime("2021-03-30 12:12:12.123456");
    Assertions.assertNotEquals(instant, null);

    instant = timePrecisionFilter.identifyTime("2021-03-30 12:12:12.123");
    Assertions.assertNotEquals(instant, null);

    instant= timePrecisionFilter.identifyTime("2023-03-30 15:30:45");
    Assertions.assertNotEquals(instant, null);

    instant = timePrecisionFilter.identifyTime("12:12:12.123456");
    Assertions.assertNotEquals(instant, null);

    instant = timePrecisionFilter.identifyTime("12:12:12.123");
    Assertions.assertNotEquals(instant, null);

    instant = timePrecisionFilter.identifyTime("12:12:12");
    Assertions.assertNotEquals(instant, null);

    instant = timePrecisionFilter.identifyTime("2021-03-30T12:12:12.123Z");
    Assertions.assertNotEquals(instant, null);

    instant = timePrecisionFilter.identifyTime("2021-03-30 12:12:12.123Z");
    Assertions.assertNotEquals(instant, null);

    instant= timePrecisionFilter.identifyTime("2021-03-30T12:12:12.123Z");
    Assertions.assertNotEquals(instant, null);

    instant= timePrecisionFilter.identifyTime("2023-03-30T15:30:45.123+08:00");
    Assertions.assertNotEquals(instant, null);

    instant = timePrecisionFilter.identifyTime("2023-03-30 15:30:45.123+08:00");
    Assertions.assertNotEquals(instant, null);

  }

}
