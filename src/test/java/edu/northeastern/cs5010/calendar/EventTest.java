package edu.northeastern.cs5010.calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EventTest {

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          # Basic event creation
          Meeting -> 2024-01-01 -> 2024-01-01 -> Meeting -> 2024-01-01 -> 2024-01-01
          # Different dates
          Lunch -> 2024-03-15 -> 2024-03-15 -> Lunch -> 2024-03-15 -> 2024-03-15
          Conference -> 2024-06-01 -> 2024-06-05 -> Conference -> 2024-06-01 -> 2024-06-05
          # Event with special characters in subject
          Team's Meeting -> 2024-01-10 -> 2024-01-10 -> Team's Meeting -> 2024-01-10 -> 2024-01-10
          # Long subject
          Annual Company Meeting 2024 -> 2024-12-01 -> 2024-12-01 -> Annual Company Meeting 2024 -> 2024-12-01 -> 2024-12-01
          """
  )
  void testEventConstructor(String subject, String startDate, String endDate,
                                           String expectedSubject, String expectedStartDate, String expectedEndDate) {
    Event event = new Event(subject, LocalDate.parse(startDate), LocalDate.parse(endDate));
    assertEquals(expectedSubject, event.getSubject());
    assertEquals(LocalDate.parse(expectedStartDate), event.getStartDate());
    assertEquals(LocalDate.parse(expectedEndDate), event.getEndDate());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> New Meeting -> New Meeting
          Event -> Updated Event -> Updated Event
          Original -> Changed -> Changed
          """
  )
  void testSetSubject(String originalSubject, String newSubject, String expected) {
    Event event = new Event(originalSubject, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1));
    event.setSubject(newSubject);
    assertEquals(expected, event.getSubject());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          2024-01-01 -> 2024-02-01 -> 2024-02-01
          2024-03-15 -> 2024-04-20 -> 2024-04-20
          2024-06-01 -> 2024-07-15 -> 2024-07-15
          """
  )
  void testSetStartDate(String originalDate, String newDate, String expected) {
    Event event = new Event("Event", LocalDate.parse(originalDate), LocalDate.of(2024, 12, 31));
    event.setStartDate(LocalDate.parse(newDate));
    assertEquals(LocalDate.parse(expected), event.getStartDate());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          2024-01-01 -> 2024-03-01 -> 2024-03-01
          2024-03-15 -> 2024-05-20 -> 2024-05-20
          2024-06-01 -> 2024-08-15 -> 2024-08-15
          """
  )
  void testSetEndDate(String originalDate, String newDate, String expected) {
    Event event = new Event("Event", LocalDate.of(2024, 1, 1), LocalDate.parse(originalDate));
    event.setEndDate(LocalDate.parse(newDate));
    assertEquals(LocalDate.parse(expected), event.getEndDate());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          # Basic toString
          Meeting -> 2024-01-01 -> 2024-01-01 -> Event [subject=Meeting, startDate=2024-01-01, endDate=2024-01-01]
          # Different dates
          Conference -> 2024-06-01 -> 2024-06-05 -> Event [subject=Conference, startDate=2024-06-01, endDate=2024-06-05]
          # Special characters
          Team's Meeting -> 2024-01-10 -> 2024-01-10 -> Event [subject=Team's Meeting, startDate=2024-01-10, endDate=2024-01-10]
          """
  )
  void testToString(String subject, String startDate, String endDate, String expected) {
    Event event = new Event(subject, LocalDate.parse(startDate), LocalDate.parse(endDate));
    assertEquals(expected, event.toString());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2024-01-01 -> 2024-01-01 -> Meeting
          Conference -> 2024-06-01 -> 2024-06-05 -> Conference
          Team's Meeting -> 2024-01-10 -> 2024-01-10 -> Team's Meeting
          """
  )
  void testGetSubject(String subject, String startDate, String endDate, String expected) {
    Event event = new Event(subject, LocalDate.parse(startDate), LocalDate.parse(endDate));
    assertEquals(expected, event.getSubject());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2024-01-01 -> 2024-01-01 -> 2024-01-01
          Conference -> 2024-06-01 -> 2024-06-05 -> 2024-06-01
          Event -> 2024-12-25 -> 2024-12-31 -> 2024-12-25
          """
  )
  void testGetStartDate(String subject, String startDate, String endDate, String expected) {
    Event event = new Event(subject, LocalDate.parse(startDate), LocalDate.parse(endDate));
    assertEquals(LocalDate.parse(expected), event.getStartDate());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2024-01-01 -> 2024-01-01 -> 2024-01-01
          Conference -> 2024-06-01 -> 2024-06-05 -> 2024-06-05
          Event -> 2024-12-25 -> 2024-12-31 -> 2024-12-31
          """
  )
  void testGetEndDate(String subject, String startDate, String endDate, String expected) {
    Event event = new Event(subject, LocalDate.parse(startDate), LocalDate.parse(endDate));
    assertEquals(LocalDate.parse(expected), event.getEndDate());
  }
}
