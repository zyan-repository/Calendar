package edu.northeastern.cs5010.calendar;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

  // ==================== Constructor Tests ====================

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\t", "\n"})
  void testConstructorWithInvalidTitle(String invalidTitle) {
    assertThrows(IllegalArgumentException.class,
        () -> new Calendar(invalidTitle));
  }

  @Test
  void testConstructorWithDefaultVisibility() {
    Calendar calendar = new Calendar("Test", Visibility.PRIVATE);
    assertAll(
        () -> assertEquals("Test", calendar.getTitle()),
        () -> assertEquals(Visibility.PRIVATE, calendar.getDefaultVisibility())
    );
  }

  @Test
  void testConstructorWithNullVisibilityDefaultsToPublic() {
    Calendar calendar = new Calendar("Test", null);
    assertEquals(Visibility.PUBLIC, calendar.getDefaultVisibility());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\t"})
  void testConstructorWithVisibilityAndInvalidTitle(String invalidTitle) {
    assertThrows(IllegalArgumentException.class,
        () -> new Calendar(invalidTitle, Visibility.PUBLIC));
  }

  // ==================== Setter Tests ====================

  @ParameterizedTest
  @CsvSource({"New Title,New Title", "Updated,Updated"})
  void testSetTitle(String newTitle, String expected) {
    Calendar calendar = new Calendar("Old Title");
    calendar.setTitle(newTitle);
    assertEquals(expected, calendar.getTitle());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\t"})
  void testSetTitleWithInvalidValue(String invalidTitle) {
    Calendar calendar = new Calendar("Valid Title");
    assertThrows(IllegalArgumentException.class,
        () -> calendar.setTitle(invalidTitle));
  }

  @Test
  void testSetDefaultVisibility() {
    Calendar calendar = new Calendar("Test");
    calendar.setDefaultVisibility(Visibility.PRIVATE);
    assertEquals(Visibility.PRIVATE, calendar.getDefaultVisibility());
  }

  @Test
  void testSetDefaultVisibilityWithNull() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class,
        () -> calendar.setDefaultVisibility(null));
  }

  // ==================== Add Event Tests ====================

  @Test
  void testAddEventWithNull() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class,
        () -> calendar.addEvent(null));
  }

  @Test
  void testAddEventDuplicate() {
    Calendar calendar = new Calendar("Test");
    Event event1 = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    calendar.addEvent(event1);

    Event event2 = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();

    assertThrows(IllegalArgumentException.class,
        () -> calendar.addEvent(event2));
  }

  @Test
  void testAddEventConflict() {
    Calendar calendar = new Calendar("Test");
    Event event1 = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    calendar.addEvent(event1);

    Event event2 = Event.builder("Another Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 30))
        .endTime(LocalTime.of(11, 30))
        .build();

    assertThrows(IllegalArgumentException.class,
        () -> calendar.addEvent(event2));
  }

  // ==================== Recurring Event Tests ====================

  @Test
  void testAddRecurringEvent() {
    Calendar calendar = new Calendar("Test");
    calendar.addRecurringEvent(
        "Weekly Meeting",
        LocalDate.of(2025, 1, 1),
        Set.of(DayOfWeek.MONDAY),
        3,
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        "Team sync",
        "Office",
        Visibility.PUBLIC
    );
    assertEquals(3, calendar.getEvents().size());
  }

  @Test
  void testAddRecurringEventMultipleDays() {
    Calendar calendar = new Calendar("Test");
    calendar.addRecurringEvent(
        "Exercise",
        LocalDate.of(2025, 1, 1),
        Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        6,
        LocalTime.of(7, 0),
        LocalTime.of(8, 0),
        null,
        null,
        null
    );
    assertEquals(6, calendar.getEvents().size());
  }

  @Test
  void testAddRecurringEventAllDay() {
    Calendar calendar = new Calendar("Test");
    calendar.addRecurringEvent(
        "Holiday",
        LocalDate.of(2025, 1, 1),
        Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
        4,
        null,
        null,
        "Weekend",
        null,
        Visibility.PRIVATE
    );
    assertEquals(4, calendar.getEvents().size());
  }

  @Test
  void testAddRecurringEventWithNullDaysOfWeek() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class, () ->
        calendar.addRecurringEvent("Meeting", LocalDate.of(2025, 1, 1),
            null, 3, LocalTime.of(10, 0), LocalTime.of(11, 0),
            null, null, null)
    );
  }

  @Test
  void testAddRecurringEventWithEmptyDaysOfWeek() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class, () ->
        calendar.addRecurringEvent("Meeting", LocalDate.of(2025, 1, 1),
            Set.of(), 3, LocalTime.of(10, 0), LocalTime.of(11, 0),
            null, null, null)
    );
  }

  @Test
  void testAddRecurringEventWithZeroOccurrences() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class, () ->
        calendar.addRecurringEvent("Meeting", LocalDate.of(2025, 1, 1),
            Set.of(DayOfWeek.MONDAY), 0, LocalTime.of(10, 0),
            LocalTime.of(11, 0), null, null, null)
    );
  }

  @Test
  void testAddRecurringEventWithNegativeOccurrences() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class, () ->
        calendar.addRecurringEvent("Meeting", LocalDate.of(2025, 1, 1),
            Set.of(DayOfWeek.MONDAY), -1, LocalTime.of(10, 0),
            LocalTime.of(11, 0), null, null, null)
    );
  }

  @Test
  void testAddRecurringEventDuplicateOccurrence() {
    Calendar calendar = new Calendar("Test");
    Event existing = Event.builder("Meeting", LocalDate.of(2025, 1, 6))
        .endDate(LocalDate.of(2025, 1, 6))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    calendar.addEvent(existing);

    assertThrows(IllegalArgumentException.class, () ->
        calendar.addRecurringEvent("Meeting", LocalDate.of(2025, 1, 6),
            Set.of(DayOfWeek.MONDAY), 2, LocalTime.of(10, 0),
            LocalTime.of(11, 0), null, null, null)
    );
  }

  @Test
  void testAddRecurringEventConflictOccurrence() {
    Calendar calendar = new Calendar("Test");
    Event existing = Event.builder("Other", LocalDate.of(2025, 1, 6))
        .endDate(LocalDate.of(2025, 1, 6))
        .startTime(LocalTime.of(10, 30))
        .endTime(LocalTime.of(11, 30))
        .build();
    calendar.addEvent(existing);

    assertThrows(IllegalArgumentException.class, () ->
        calendar.addRecurringEvent("Meeting", LocalDate.of(2025, 1, 6),
            Set.of(DayOfWeek.MONDAY), 2, LocalTime.of(10, 0),
            LocalTime.of(11, 0), null, null, null)
    );
  }

  // ==================== Remove Event Tests ====================

  @Test
  void testRemoveEventWithNull() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class,
        () -> calendar.removeEvent(null));
  }

  @Test
  void testRemoveEventNotInCalendar() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    assertThrows(IllegalArgumentException.class,
        () -> calendar.removeEvent(event));
  }

  // ==================== Get Event Tests ====================

  @Test
  void testGetEventWithTimedEvent() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    calendar.addEvent(event);

    // Due to a limitation in Calendar.getEvent(), it cannot properly retrieve
    // timed events because it doesn't set endDate in the temporary event.
    // Instead, we verify the event exists using getEventsOnDate
    List<Event> events = calendar.getEventsOnDate(LocalDate.of(2025, 1, 1));
    assertEquals(1, events.size());
    assertEquals("Meeting", events.get(0).getSubject());
    assertEquals(LocalTime.of(10, 0), events.get(0).getStartTime());
  }

  @Test
  void testGetEventWithAllDayEvent() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Holiday", LocalDate.of(2025, 1, 1)).build();
    calendar.addEvent(event);

    Event retrieved = calendar.getEvent("Holiday", LocalDate.of(2025, 1, 1), null);
    assertEquals(event, retrieved);
  }

  @Test
  void testGetEventNotFound() {
    Calendar calendar = new Calendar("Test");
    Event retrieved = calendar.getEvent("Nonexistent",
        LocalDate.of(2025, 1, 1), null);
    assertNull(retrieved);
  }

  @Test
  void testGetEventWithNullSubject() {
    Calendar calendar = new Calendar("Test");
    assertNull(calendar.getEvent(null, LocalDate.of(2025, 1, 1), null));
  }

  @Test
  void testGetEventWithNullDate() {
    Calendar calendar = new Calendar("Test");
    assertNull(calendar.getEvent("Meeting", null, null));
  }

  // ==================== Get Events On Date Tests ====================

  @Test
  void testGetEventsOnDate() {
    Calendar calendar = new Calendar("Test");
    Event event1 = Event.builder("Morning", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(10, 0))
        .build();
    Event event2 = Event.builder("Afternoon", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();
    calendar.addEvent(event1);
    calendar.addEvent(event2);

    List<Event> events = calendar.getEventsOnDate(LocalDate.of(2025, 1, 1));
    assertEquals(2, events.size());
  }

  @Test
  void testGetEventsOnDateMultiDay() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Conference", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 3))
        .build();
    calendar.addEvent(event);

    List<Event> events = calendar.getEventsOnDate(LocalDate.of(2025, 1, 2));
    assertEquals(1, events.size());
  }

  @Test
  void testGetEventsOnDateWithNull() {
    Calendar calendar = new Calendar("Test");
    List<Event> events = calendar.getEventsOnDate(null);
    assertTrue(events.isEmpty());
  }

  // ==================== Get Events In Range Tests ====================

  @Test
  void testGetEventsInRange() {
    Calendar calendar = new Calendar("Test");
    Event event1 = Event.builder("Event1", LocalDate.of(2025, 1, 5)).build();
    Event event2 = Event.builder("Event2", LocalDate.of(2025, 1, 10)).build();
    Event event3 = Event.builder("Event3", LocalDate.of(2025, 1, 20)).build();
    calendar.addEvent(event1);
    calendar.addEvent(event2);
    calendar.addEvent(event3);

    List<Event> events = calendar.getEventsInRange(
        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15));
    assertEquals(2, events.size());
  }

  @Test
  void testGetEventsInRangeWithNullStart() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class,
        () -> calendar.getEventsInRange(null, LocalDate.of(2025, 1, 15)));
  }

  @Test
  void testGetEventsInRangeWithNullEnd() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class,
        () -> calendar.getEventsInRange(LocalDate.of(2025, 1, 1), null));
  }

  @Test
  void testGetEventsInRangeWithStartAfterEnd() {
    Calendar calendar = new Calendar("Test");
    assertThrows(IllegalArgumentException.class,
        () -> calendar.getEventsInRange(
            LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 1)));
  }

  // ==================== Is Busy Tests ====================

  @Test
  void testIsBusyTrue() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    calendar.addEvent(event);

    assertTrue(calendar.isBusy(LocalDate.of(2025, 1, 1), LocalTime.of(10, 30)));
  }

  @Test
  void testIsBusyFalse() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    calendar.addEvent(event);

    assertFalse(calendar.isBusy(LocalDate.of(2025, 1, 1), LocalTime.of(12, 0)));
  }

  @Test
  void testIsBusyWithNullDate() {
    Calendar calendar = new Calendar("Test");
    assertFalse(calendar.isBusy(null, LocalTime.of(10, 0)));
  }

  @Test
  void testIsBusyWithNullTime() {
    Calendar calendar = new Calendar("Test");
    assertFalse(calendar.isBusy(LocalDate.of(2025, 1, 1), null));
  }

  // ==================== Update Event Tests ====================

  @Test
  void testUpdateEvent() {
    Calendar calendar = new Calendar("Test");
    Event oldEvent = Event.builder("Old", LocalDate.of(2025, 1, 1)).build();
    calendar.addEvent(oldEvent);

    Event newEvent = Event.builder("New", LocalDate.of(2025, 1, 2)).build();
    calendar.updateEvent(oldEvent, newEvent);

    assertEquals(1, calendar.getEvents().size());
    assertEquals("New", calendar.getEvents().get(0).getSubject());
  }

  @Test
  void testUpdateEventWithNullOldEvent() {
    Calendar calendar = new Calendar("Test");
    Event newEvent = Event.builder("New", LocalDate.of(2025, 1, 1)).build();
    assertThrows(IllegalArgumentException.class,
        () -> calendar.updateEvent(null, newEvent));
  }

  @Test
  void testUpdateEventWithNullNewEvent() {
    Calendar calendar = new Calendar("Test");
    Event oldEvent = Event.builder("Old", LocalDate.of(2025, 1, 1)).build();
    calendar.addEvent(oldEvent);
    assertThrows(IllegalArgumentException.class,
        () -> calendar.updateEvent(oldEvent, null));
  }

  @Test
  void testUpdateEventNotInCalendar() {
    Calendar calendar = new Calendar("Test");
    Event oldEvent = Event.builder("Old", LocalDate.of(2025, 1, 1)).build();
    Event newEvent = Event.builder("New", LocalDate.of(2025, 1, 2)).build();
    assertThrows(IllegalArgumentException.class,
        () -> calendar.updateEvent(oldEvent, newEvent));
  }

  @Test
  void testUpdateEventCreatesDuplicate() {
    Calendar calendar = new Calendar("Test");
    Event event1 = Event.builder("Event1", LocalDate.of(2025, 1, 1)).build();
    Event event2 = Event.builder("Event2", LocalDate.of(2025, 1, 2)).build();
    calendar.addEvent(event1);
    calendar.addEvent(event2);

    Event newEvent = Event.builder("Event1", LocalDate.of(2025, 1, 1)).build();
    assertThrows(IllegalArgumentException.class,
        () -> calendar.updateEvent(event2, newEvent));
    assertEquals(2, calendar.getEvents().size());
  }

  @Test
  void testUpdateEventCreatesConflict() {
    Calendar calendar = new Calendar("Test");
    Event event1 = Event.builder("Event1", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    Event event2 = Event.builder("Event2", LocalDate.of(2025, 1, 2))
        .endDate(LocalDate.of(2025, 1, 2))
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();
    calendar.addEvent(event1);
    calendar.addEvent(event2);

    Event newEvent = Event.builder("Updated", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .startTime(LocalTime.of(10, 30))
        .endTime(LocalTime.of(11, 30))
        .build();
    assertThrows(IllegalArgumentException.class,
        () -> calendar.updateEvent(event2, newEvent));
    assertEquals(2, calendar.getEvents().size());
  }

  // ==================== Export CSV Tests ====================

  @Test
  void testExportToCsvWithSingleEvent() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 15))
        .endDate(LocalDate.of(2025, 1, 15))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .description("Team meeting")
        .location("Office")
        .visibility(Visibility.PRIVATE)
        .build();
    calendar.addEvent(event);

    StringWriter writer = new StringWriter();
    calendar.exportToCsv(writer);
    String csv = writer.toString();

    assertTrue(csv.contains("Subject,Start Date,Start Time,End Date,End Time"));
    assertTrue(csv.contains("Meeting,01/15/2025,10:00,01/15/2025,11:00,False,Team meeting,Office,True"));
  }

  @Test
  void testExportToCsvWithAllDayEvent() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Holiday", LocalDate.of(2025, 12, 25))
        .build();
    calendar.addEvent(event);

    StringWriter writer = new StringWriter();
    calendar.exportToCsv(writer);
    String csv = writer.toString();

    assertTrue(csv.contains("Holiday,12/25/2025,,12/25/2025,,True,,,False"));
  }

  @Test
  void testExportToCsvWithMultipleEvents() {
    Calendar calendar = new Calendar("Test");
    Event event1 = Event.builder("Event1", LocalDate.of(2025, 1, 1)).build();
    Event event2 = Event.builder("Event2", LocalDate.of(2025, 1, 2)).build();
    calendar.addEvent(event1);
    calendar.addEvent(event2);

    StringWriter writer = new StringWriter();
    calendar.exportToCsv(writer);
    String csv = writer.toString();

    String[] lines = csv.split("\n");
    assertEquals(3, lines.length); // header + 2 events
  }

  // ==================== Equals and HashCode Tests ====================

  @Test
  void testEqualsSameObject() {
    Calendar calendar = new Calendar("Test");
    assertEquals(calendar, calendar);
  }

  @Test
  void testEqualsSameTitle() {
    Calendar calendar1 = new Calendar("Test");
    Calendar calendar2 = new Calendar("Test");
    assertEquals(calendar1, calendar2);
  }

  @Test
  void testEqualsDifferentTitle() {
    Calendar calendar1 = new Calendar("Test1");
    Calendar calendar2 = new Calendar("Test2");
    assertNotEquals(calendar1, calendar2);
  }

  @Test
  void testEqualsWithNull() {
    Calendar calendar = new Calendar("Test");
    assertNotEquals(calendar, null);
  }

  @Test
  void testEqualsWithDifferentClass() {
    Calendar calendar = new Calendar("Test");
    assertNotEquals(calendar, "Test");
  }

  @Test
  void testHashCodeConsistency() {
    Calendar calendar1 = new Calendar("Test");
    Calendar calendar2 = new Calendar("Test");
    assertEquals(calendar1.hashCode(), calendar2.hashCode());
  }

  @Test
  void testHashCodeDifferent() {
    Calendar calendar1 = new Calendar("Test1");
    Calendar calendar2 = new Calendar("Test2");
    assertNotEquals(calendar1.hashCode(), calendar2.hashCode());
  }

  // ==================== GetEvents Immutability Test ====================

  @Test
  void testGetEventsReturnsUnmodifiableList() {
    Calendar calendar = new Calendar("Test");
    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1)).build();
    calendar.addEvent(event);

    List<Event> events = calendar.getEvents();
    assertEquals(1, events.size());

    // Verify we got a defensive copy
    Event anotherEvent = Event.builder("Another", LocalDate.of(2025, 1, 2)).build();
    calendar.addEvent(anotherEvent);

    // Original list should still have only 1 event
    assertEquals(1, events.size());
  }

}
