package edu.northeastern.cs5010.calendar;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EventTest {

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2025-01-01 -> 2025-01-01 -> Meeting -> 2025-01-01 -> 2025-01-01
          Lunch -> 2025-03-15 -> 2025-03-15 -> Lunch -> 2025-03-15 -> 2025-03-15
          Conference -> 2025-06-01 -> 2025-06-05 -> Conference -> 2025-06-01 -> 2025-06-05
          Team's Meeting -> 2025-01-10 -> 2025-01-10 -> Team's Meeting -> 2025-01-10 -> 2025-01-10
          Annual Company Meeting 2025 -> 2025-12-01 -> 2025-12-01 -> Annual Company Meeting 2025 -> 2025-12-01 -> 2025-12-01
          """
  )
  void testBuilder(String expectedSubject, String expectedStartDate, String expectedEndDate,
                                           String subject, String startDate, String endDate) {
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(endDate))
        .build();
    assertAll(
        () -> assertEquals(expectedSubject, event.getSubject()),
        () -> assertEquals(LocalDate.parse(expectedStartDate), event.getStartDate()),
        () -> assertEquals(LocalDate.parse(expectedEndDate), event.getEndDate())
    );
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          New Meeting -> Meeting -> New Meeting
          Updated Event -> Event -> Updated Event
          Changed -> Original -> Changed
          """
  )
  void testSetSubject(String expected, String originalSubject, String newSubject) {
    Event event = Event.builder(originalSubject, LocalDate.of(2025, 1, 1))
        .build();
    event.setSubject(newSubject);
    assertEquals(expected, event.getSubject());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          2025-02-01 -> 2025-01-01 -> 2025-02-01
          2025-04-20 -> 2025-03-15 -> 2025-04-20
          2025-07-15 -> 2025-06-01 -> 2025-07-15
          """
  )
  void testSetStartDate(String expected, String originalDate, String newDate) {
    Event event = Event.builder("Event", LocalDate.parse(originalDate))
        .endDate(LocalDate.of(2025, 12, 31))
        .build();
    event.setStartDate(LocalDate.parse(newDate));
    assertEquals(LocalDate.parse(expected), event.getStartDate());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          2025-03-01 -> 2025-01-01 -> 2025-03-01
          2025-05-20 -> 2025-03-15 -> 2025-05-20
          2025-08-15 -> 2025-06-01 -> 2025-08-15
          """
  )
  void testSetEndDate(String expected, String originalDate, String newDate) {
    Event event = Event.builder("Event", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.parse(originalDate))
        .build();
    event.setEndDate(LocalDate.parse(newDate));
    assertEquals(LocalDate.parse(expected), event.getEndDate());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Event[subject=Meeting, startDate=2025-01-01, endDate=2025-01-01 (All-Day), visibility=PUBLIC] -> Meeting -> 2025-01-01 -> 2025-01-01 -> NULL -> NULL -> NULL
          Event[subject=Conference, startDate=2025-06-01, endDate=2025-06-05 (All-Day), visibility=PUBLIC] -> Conference -> 2025-06-01 -> 2025-06-05 -> NULL -> NULL -> NULL
          Event[subject=Team's Meeting, startDate=2025-01-10, endDate=2025-01-10 (All-Day), visibility=PUBLIC] -> Team's Meeting -> 2025-01-10 -> 2025-01-10 -> NULL -> NULL -> NULL
          Event[subject=Meeting, startDate=2025-01-01, startTime=09:00, endDate=2025-01-01, endTime=17:00, visibility=PUBLIC] -> Meeting -> 2025-01-01 -> 2025-01-01 -> 09:00 -> 17:00 -> NULL
          Event[subject=Meeting, startDate=2025-01-01, endDate=2025-01-01 (All-Day), visibility=PUBLIC, location=Room 101] -> Meeting -> 2025-01-01 -> 2025-01-01 -> NULL -> NULL -> Room 101
          """
  )
  void testToString(String expected, String subject, String startDate, String endDate, 
                   String startTime, String endTime, String location) {
    Event.Builder builder = Event.builder(subject, LocalDate.parse(startDate));
    if (endDate != null && !endDate.equals("NULL")) {
      builder.endDate(LocalDate.parse(endDate));
    }
    if (startTime != null && !startTime.equals("NULL")) {
      builder.startTime(LocalTime.parse(startTime));
    }
    if (endTime != null && !endTime.equals("NULL")) {
      builder.endTime(LocalTime.parse(endTime));
    }
    if (location != null && !location.equals("NULL")) {
      builder.location(location);
    }
    Event event = builder.build();
    assertEquals(expected, event.toString());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> Meeting -> 2025-01-01 -> 2025-01-01
          Conference -> Conference -> 2025-06-01 -> 2025-06-05
          Team's Meeting -> Team's Meeting -> 2025-01-10 -> 2025-01-10
          """
  )
  void testGetSubject(String expected, String subject, String startDate, String endDate) {
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(endDate))
        .build();
    assertEquals(expected, event.getSubject());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          2025-01-01 -> Meeting -> 2025-01-01 -> 2025-01-01
          2025-06-01 -> Conference -> 2025-06-01 -> 2025-06-05
          2025-12-25 -> Event -> 2025-12-25 -> 2025-12-31
          """
  )
  void testGetStartDate(String expected, String subject, String startDate, String endDate) {
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(endDate))
        .build();
    assertEquals(LocalDate.parse(expected), event.getStartDate());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          2025-01-01 -> Meeting -> 2025-01-01 -> 2025-01-01
          2025-06-05 -> Conference -> 2025-06-01 -> 2025-06-05
          2025-12-31 -> Event -> 2025-12-25 -> 2025-12-31
          """
  )
  void testGetEndDate(String expected, String subject, String startDate, String endDate) {
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(endDate))
        .build();
    assertEquals(LocalDate.parse(expected), event.getEndDate());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          09:00 -> 09:00 -> 17:00
          10:30 -> 10:30 -> 11:30
          14:00 -> 14:00 -> 16:00
          """
  )
  void testGetStartTime(String expected, String startTime, String endTime) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.parse(startTime))
        .endTime(LocalTime.parse(endTime))
        .build();
    assertEquals(LocalTime.parse(expected), event.getStartTime());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          17:00 -> 09:00 -> 17:00
          11:30 -> 10:30 -> 11:30
          16:00 -> 14:00 -> 16:00
          """
  )
  void testGetEndTime(String expected, String startTime, String endTime) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.parse(startTime))
        .endTime(LocalTime.parse(endTime))
        .build();
    assertEquals(LocalTime.parse(expected), event.getEndTime());
  }

  @Test
  void testGetStartTimeNull() {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    assertNull(event.getStartTime());
  }

  @Test
  void testGetEndTimeNull() {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .build();
    assertNull(event.getEndTime());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          PUBLIC -> PUBLIC
          PRIVATE -> PRIVATE
          """
  )
  void testGetVisibility(String expected, String visibility) {
    Visibility vis = Visibility.valueOf(visibility);
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .visibility(vis)
        .build();
    assertEquals(Visibility.valueOf(expected), event.getVisibility());
  }

  @Test
  void testGetVisibilityDefault() {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .build();
    assertEquals(Visibility.PUBLIC, event.getVisibility());
  }


  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting description -> Meeting description
          Conference details -> Conference details
          Event info -> Event info
          """
  )
  void testGetDescription(String expected, String description) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .description(description)
        .build();
    assertEquals(expected, event.getDescription());
  }


  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Room 101 -> Room 101
          Conference Hall -> Conference Hall
          Office -> Office
          """
  )
  void testGetLocation(String expected, String location) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .location(location)
        .build();
    assertEquals(expected, event.getLocation());
  }


  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          09:00 -> 09:00 -> 17:00
          10:30 -> 10:30 -> 11:30
          14:00 -> 14:00 -> 16:00
          """
  )
  void testSetStartTime(String expected, String newStartTime, String endTime) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(8, 0))
        .endTime(LocalTime.parse(endTime))
        .build();
    event.setStartTime(LocalTime.parse(newStartTime));
    assertEquals(LocalTime.parse(expected), event.getStartTime());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2025-01-01 -> 2025-01-01 -> 09:00 -> 17:00
          """
  )
  void testSetStartTimeNull(String subject, String startDate, String endDate, String startTime, String endTime) {
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(endDate))
        .startTime(LocalTime.parse(startTime))
        .endTime(LocalTime.parse(endTime))
        .build();
    assertThrows(IllegalArgumentException.class, () -> event.setStartTime(null));
  }


  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          17:00 -> 17:00
          11:30 -> 11:30
          16:00 -> 16:00
          """
  )
  void testSetEndTime(String expected, String newEndTime) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(10, 0))
        .build();
    event.setEndTime(LocalTime.parse(newEndTime));
    assertEquals(LocalTime.parse(expected), event.getEndTime());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2025-01-01 -> 2025-01-01 -> 09:00 -> 17:00
          """
  )
  void testSetEndTimeNull(String subject, String startDate, String endDate, String startTime, String endTime) {
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(endDate))
        .startTime(LocalTime.parse(startTime))
        .endTime(LocalTime.parse(endTime))
        .build();
    assertThrows(IllegalArgumentException.class, () -> event.setEndTime(null));
  }


  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          PUBLIC -> PUBLIC
          PRIVATE -> PRIVATE
          """
  )
  void testSetVisibility(String expected, String visibility) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    event.setVisibility(Visibility.valueOf(visibility));
    assertEquals(Visibility.valueOf(expected), event.getVisibility());
  }

  @Test
  void testSetVisibilityNull() {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    assertThrows(IllegalArgumentException.class, () -> event.setVisibility(null));
  }


  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          New description -> New description
          Updated info -> Updated info
          Changed details -> Changed details
          """
  )
  void testSetDescription(String expected, String description) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    event.setDescription(description);
    assertEquals(expected, event.getDescription());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          NULL -> NULL
          Test description -> Test description
          NULL -> NULL
          """
  )
  void testSetDescriptionNull(String expected, String description) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    event.setDescription("NULL".equals(description) ? null : description);
    if ("NULL".equals(expected)) {
      assertNull(event.getDescription());
    } else {
      assertEquals(expected, event.getDescription());
    }
  }


  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          New location -> New location
          Updated place -> Updated place
          Changed room -> Changed room
          """
  )
  void testSetLocation(String expected, String location) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    event.setLocation(location);
    assertEquals(expected, event.getLocation());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          NULL -> NULL
          Room 101 -> Room 101
          NULL -> NULL
          """
  )
  void testSetLocationNull(String expected, String location) {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    event.setLocation("NULL".equals(location) ? null : location);
    if ("NULL".equals(expected)) {
      assertNull(event.getLocation());
    } else {
      assertEquals(expected, event.getLocation());
    }
  }


  @Test
  void testSetSubject() {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    assertThrows(IllegalArgumentException.class, () -> event.setSubject(null));
    assertThrows(IllegalArgumentException.class, () -> event.setSubject(""));
    assertThrows(IllegalArgumentException.class, () -> event.setSubject("   "));
  }

  @Test
  void testSetStartDate() {
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    assertThrows(IllegalArgumentException.class, () -> event.setStartDate(null));
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2025-01-01 -> 09:00 -> 17:00
          """
  )
  void testSetEndDate(String subject, String startDate, String startTime, String endTime) {
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(startDate))
        .startTime(LocalTime.parse(startTime))
        .endTime(LocalTime.parse(endTime))
        .build();
    assertThrows(IllegalArgumentException.class, () -> event.setEndDate(null));
  }


  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          true -> Meeting -> 2025-01-01 -> Meeting -> 2025-01-01
          false -> Meeting -> 2025-01-01 -> Conference -> 2025-01-01
          false -> Meeting -> 2025-01-01 -> Meeting -> 2025-01-02
          """
  )
  void testEquals(String expected, String subject1, String startDate1, String subject2, String startDate2) {
    Event event1 = Event.builder(subject1, LocalDate.parse(startDate1)).build();
    Event event2 = Event.builder(subject2, LocalDate.parse(startDate2)).build();
    
    boolean equals = event1.equals(event2);
    boolean actualExpected = Boolean.parseBoolean(expected);
    assertEquals(actualExpected, equals, 
        String.format("Expected %s for events: %s vs %s", expected, event1, event2));
  }

  @Test
  void testHashCode() {
    Event event1 = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    Event event2 = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    
    assertAll(
        () -> assertEquals(event1.hashCode(), event2.hashCode()),
        () -> assertEquals(event1, event2),
        () -> assertEquals(event1.hashCode(), event2.hashCode())
    );
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          true -> Meeting -> 2025-01-01 -> NULL -> NULL
          false -> Meeting -> 2025-01-01 -> 09:00 -> 17:00
          true -> Conference -> 2025-06-01 -> NULL -> NULL
          """
  )
  void testIsAllDay(boolean expected, String subject, String startDate, String startTime, String endTime) {
    Event.Builder builder = Event.builder(subject, LocalDate.parse(startDate));
    if (startTime != null && !startTime.equals("NULL")) {
      builder.startTime(LocalTime.parse(startTime));
    }
    if (endTime != null && !endTime.equals("NULL")) {
      builder.endTime(LocalTime.parse(endTime));
    }
    if (startTime != null && !startTime.equals("NULL")) {
      builder.endDate(LocalDate.parse(startDate));
    }
    Event event = builder.build();
    assertEquals(expected, event.isAllDay());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          2025-01-01T00:00 -> Meeting -> 2025-01-01 -> NULL -> NULL
          2025-01-01T09:00 -> Meeting -> 2025-01-01 -> 09:00 -> 17:00
          2025-06-01T00:00 -> Conference -> 2025-06-01 -> NULL -> NULL
          """
  )
  void testGetStartDateTime(String expected, String subject, String startDate, String startTime, String endTime) {
    Event.Builder builder = Event.builder(subject, LocalDate.parse(startDate));
    if (startTime != null && !startTime.equals("NULL")) {
      builder.startTime(LocalTime.parse(startTime));
    }
    if (endTime != null && !endTime.equals("NULL")) {
      builder.endTime(LocalTime.parse(endTime));
    }
    if (startTime != null && !startTime.equals("NULL")) {
      builder.endDate(LocalDate.parse(startDate));
    }
    Event event = builder.build();
    assertEquals(LocalDateTime.parse(expected), event.getStartDateTime());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          2025-01-02T00:00 -> Meeting -> 2025-01-01 -> NULL -> NULL -> 2025-01-01
          2025-01-01T17:00 -> Meeting -> 2025-01-01 -> 09:00 -> 17:00 -> 2025-01-01
          2025-06-02T00:00 -> Conference -> 2025-06-01 -> NULL -> NULL -> 2025-06-01
          """
  )
  void testGetEndDateTime(String expected, String subject, String startDate, String startTime, String endTime, String endDate) {
    Event.Builder builder = Event.builder(subject, LocalDate.parse(startDate));
    if (endDate != null && !endDate.equals("NULL")) {
      builder.endDate(LocalDate.parse(endDate));
    }
    if (startTime != null && !startTime.equals("NULL")) {
      builder.startTime(LocalTime.parse(startTime));
    }
    if (endTime != null && !endTime.equals("NULL")) {
      builder.endTime(LocalTime.parse(endTime));
    }
    Event event = builder.build();
    assertEquals(LocalDateTime.parse(expected), event.getEndDateTime());
  }

}
