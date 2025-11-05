package edu.northeastern.cs5010.calendar;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CalendarTest {

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          # Basic titles
          title -> title
          My Calendar -> My Calendar
          Work Schedule -> Work Schedule
          Personal -> Personal
          # Special characters
          Calendar 2025 -> Calendar 2025
          Test's Calendar -> Test's Calendar
          # Long title
          Very Long Calendar Title For Testing Purposes -> Very Long Calendar Title For Testing Purposes
          """
  )
  void testCalendarConstructor(String expected, String input) {
    Calendar calendar = new Calendar(input);
    assertEquals(expected, calendar.getTitle());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          # Basic getTitle
          title -> title
          My Calendar -> My Calendar
          Work Schedule -> Work Schedule
          Personal -> Personal
          # Special characters
          Calendar 2025 -> Calendar 2025
          Test's Calendar -> Test's Calendar
          """
  )
  void testGetTitle(String expected, String input) {
    Calendar calendar = new Calendar(input);
    String result = calendar.getTitle();
    assertEquals(expected, result);
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 1 -> Meeting -> 2025-01-01 -> 2025-01-01
          Lunch -> 1 -> Lunch -> 2025-01-02 -> 2025-01-02
          Conference -> 1 -> Conference -> 2025-06-01 -> 2025-06-05
          """
  )
  void testAddEvent(String expectedSubject, int expectedSize, String subject, String startDate, String endDate) {
    Calendar calendar = new Calendar("Test Calendar");
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(endDate))
        .build();
    calendar.addEvent(event);
    
    assertAll(
        () -> assertEquals(expectedSize, calendar.getEvents().size()),
        () -> assertEquals(expectedSubject, calendar.getEvents().getFirst().getSubject())
    );
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          0 -> Meeting -> 2025-01-01 -> 2025-01-01
          0 -> Lunch -> 2025-01-02 -> 2025-01-02
          0 -> Conference -> 2025-06-01 -> 2025-06-05
          """
  )
  void testRemoveEvent(int expectedSize, String subject, String startDate, String endDate) {
    Calendar calendar = new Calendar("Test Calendar");
    Event event = Event.builder(subject, LocalDate.parse(startDate))
        .endDate(LocalDate.parse(endDate))
        .build();
    calendar.addEvent(event);
    calendar.removeEvent(event);
    
    assertEquals(expectedSize, calendar.getEvents().size());
  }

  @ParameterizedTest
  @CsvSource(
    delimiterString = "->",
    textBlock = """
        Calendar [title=My Calendar, events=[]] -> My Calendar -> 0
        Calendar [title=Work, events=[Event[subject=Meeting, startDate=2025-01-01, endDate=2025-01-01 (All-Day), visibility=PUBLIC]]] -> Work -> 1
        Calendar [title=Personal, events=[Event[subject=Meeting, startDate=2025-01-01, endDate=2025-01-01 (All-Day), visibility=PUBLIC], Event[subject=Event2, startDate=2025-01-02, endDate=2025-01-02 (All-Day), visibility=PUBLIC]]] -> Personal -> 2
        """
  )
  void testToString(String expected, String title, int eventCount) {
    Calendar calendar = new Calendar(title);

    if (eventCount >= 1) {
      Event event1 = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
          .build();
      calendar.addEvent(event1);
    }
    if (eventCount >= 2) {
      Event event2 = Event.builder("Event2", LocalDate.of(2025, 1, 2))
          .build();
      calendar.addEvent(event2);
    }
    
    String result = calendar.toString();
    assertEquals(expected, result);
  }

}
