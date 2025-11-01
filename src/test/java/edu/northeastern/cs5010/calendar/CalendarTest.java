package edu.northeastern.cs5010.calendar;

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
          Calendar 2024 -> Calendar 2024
          Test's Calendar -> Test's Calendar
          # Long title
          Very Long Calendar Title For Testing Purposes -> Very Long Calendar Title For Testing Purposes
          """
  )
  void testCalendarConstructor(String input, String expected) {
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
          Calendar 2024 -> Calendar 2024
          Test's Calendar -> Test's Calendar
          """
  )
  void testGetTitle(String input, String expected) {
    Calendar calendar = new Calendar(input);
    String result = calendar.getTitle();
    assertEquals(expected, result);
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2024-01-01 -> 2024-01-01 -> Meeting -> 1
          Lunch -> 2024-01-02 -> 2024-01-02 -> Lunch -> 1
          Conference -> 2024-06-01 -> 2024-06-05 -> Conference -> 1
          """
  )
  void testAddEvent(String subject, String startDate, String endDate,
                                             String expectedSubject, int expectedSize) {
    Calendar calendar = new Calendar("Test Calendar");
    Event event = new Event(subject, LocalDate.parse(startDate), LocalDate.parse(endDate));
    calendar.addEvent(event);
    
    assertEquals(expectedSize, calendar.getEvents().size());
    assertEquals(expectedSubject, calendar.getEvents().getFirst().getSubject());
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "->",
      textBlock = """
          Meeting -> 2024-01-01 -> 2024-01-01 -> 0
          Lunch -> 2024-01-02 -> 2024-01-02 -> 0
          Conference -> 2024-06-01 -> 2024-06-05 -> 0
          """
  )
  void testRemoveEvent(String subject, String startDate, String endDate,
                                                    int expectedSize) {
    Calendar calendar = new Calendar("Test Calendar");
    Event event = new Event(subject, LocalDate.parse(startDate), LocalDate.parse(endDate));
    calendar.addEvent(event);
    calendar.removeEvent(event);
    
    assertEquals(expectedSize, calendar.getEvents().size());
  }

  @ParameterizedTest
  @CsvSource(
    delimiterString = "->",
    textBlock = """
        My Calendar -> 0 -> Calendar [title=My Calendar, events=[]]
        Work -> 1 -> Calendar [title=Work, events=[Event [subject=Meeting, startDate=2024-01-01, endDate=2024-01-01]]]
        Personal -> 2 -> Calendar [title=Personal, events=[Event [subject=Meeting, startDate=2024-01-01, endDate=2024-01-01], Event [subject=Event2, startDate=2024-01-02, endDate=2024-01-02]]]
        """
  )
  void testToString(String title, int eventCount, String expected) {
    Calendar calendar = new Calendar(title);

    if (eventCount >= 1) {
      Event event1 = new Event("Meeting", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1));
      calendar.addEvent(event1);
    }
    if (eventCount >= 2) {
      Event event2 = new Event("Event2", LocalDate.of(2024, 1, 2), LocalDate.of(2024, 1, 2));
      calendar.addEvent(event2);
    }
    
    String result = calendar.toString();
    assertEquals(expected, result);
  }

}
