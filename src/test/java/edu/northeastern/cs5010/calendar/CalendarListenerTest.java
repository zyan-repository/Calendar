package edu.northeastern.cs5010.calendar;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.northeastern.cs5010.calendar.model.Calendar;
import edu.northeastern.cs5010.calendar.model.CalendarListener;
import edu.northeastern.cs5010.calendar.model.Event;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class CalendarListenerTest {

  private static class TestListener implements CalendarListener {
    private final List<Event> addedEvents = new ArrayList<>();
    private final List<Event> modifiedEvents = new ArrayList<>();

    @Override
    public void onEventAdded(Event event) {
      addedEvents.add(event);
    }

    @Override
    public void onEventModified(Event event) {
      modifiedEvents.add(event);
    }

    public List<Event> getAddedEvents() {
      return Collections.unmodifiableList(new ArrayList<>(addedEvents));
    }

    public List<Event> getModifiedEvents() {
      return Collections.unmodifiableList(new ArrayList<>(modifiedEvents));
    }

    public void clear() {
      addedEvents.clear();
      modifiedEvents.clear();
    }

    public int getAddedCount() {
      return addedEvents.size();
    }

    public int getModifiedCount() {
      return modifiedEvents.size();
    }
  }

  @Test
  void testSingleListenerReceivesAddNotification() {
    Calendar calendar = new Calendar("Test Calendar");
    TestListener listener1 = new TestListener();
    calendar.addCalendarListener(listener1);

    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .build();

    calendar.addEvent(event);

    assertAll(
        () -> assertEquals(1, listener1.getAddedCount()),
        () -> assertEquals(event, listener1.getAddedEvents().get(0)),
        () -> assertEquals(0, listener1.getModifiedCount()));
  }

  @Test
  void testMultipleListenersReceiveAddNotification() {
    Calendar calendar = new Calendar("Test Calendar");
    TestListener listener1 = new TestListener();
    TestListener listener2 = new TestListener();
    TestListener listener3 = new TestListener();
    calendar.addCalendarListener(listener1);
    calendar.addCalendarListener(listener2);
    calendar.addCalendarListener(listener3);

    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .build();

    calendar.addEvent(event);

    assertAll(
        () -> assertEquals(1, listener1.getAddedCount()),
        () -> assertEquals(1, listener2.getAddedCount()),
        () -> assertEquals(1, listener3.getAddedCount()),
        () -> assertEquals(event, listener1.getAddedEvents().get(0)),
        () -> assertEquals(event, listener2.getAddedEvents().get(0)),
        () -> assertEquals(event, listener3.getAddedEvents().get(0)));
  }

  @Test
  void testListenersReceiveModifyNotification() {
    Calendar calendar = new Calendar("Test Calendar");
    TestListener listener1 = new TestListener();
    TestListener listener2 = new TestListener();
    calendar.addCalendarListener(listener1);
    calendar.addCalendarListener(listener2);

    Event originalEvent = Event.builder("Original", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .build();
    calendar.addEvent(originalEvent);

    listener1.clear();
    listener2.clear();

    Event modifiedEvent = Event.builder("Modified", LocalDate.of(2025, 1, 2))
        .endDate(LocalDate.of(2025, 1, 2))
        .build();

    calendar.updateEvent(originalEvent, modifiedEvent);

    assertAll(
        () -> assertEquals(0, listener1.getAddedCount()),
        () -> assertEquals(0, listener2.getAddedCount()),
        () -> assertEquals(1, listener1.getModifiedCount()),
        () -> assertEquals(1, listener2.getModifiedCount()),
        () -> assertEquals(modifiedEvent, listener1.getModifiedEvents().get(0)),
        () -> assertEquals(modifiedEvent, listener2.getModifiedEvents().get(0)));
  }

  @Test
  void testRecurringEventNotifiesForEachOccurrence() {
    Calendar calendar = new Calendar("Test Calendar");
    TestListener listener2 = new TestListener();
    TestListener listener1 = new TestListener();
    calendar.addCalendarListener(listener2);
    calendar.addCalendarListener(listener1);

    LocalDate startDate = LocalDate.of(2025, 1, 6);
    calendar.addRecurringEvent(
        "Weekly Meeting",
        startDate,
        Set.of(startDate.getDayOfWeek()),
        3,
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        null,
        null,
        null);

    assertAll(
        () -> assertEquals(3, listener1.getAddedCount()),
        () -> assertEquals(3, listener2.getAddedCount()));
  }

  @Test
  void testModifyRecurringEventInstanceNotifies() {
    Calendar calendar = new Calendar("Test Calendar");
    TestListener listener3 = new TestListener();
    TestListener listener1 = new TestListener();
    TestListener listener2 = new TestListener();

    calendar.addCalendarListener(listener3);
    calendar.addCalendarListener(listener1);
    calendar.addCalendarListener(listener2);

    LocalDate startDate = LocalDate.of(2025, 1, 6);
    calendar.addRecurringEvent(
        "Weekly Meeting",
        startDate,
        Set.of(startDate.getDayOfWeek()),
        3,
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        null,
        null,
        null);

    listener1.clear();
    listener2.clear();
    listener3.clear();

    List<Event> events = calendar.getEventsOnDate(startDate);
    Event eventToModify = events.get(0);
    eventToModify.setSubject("Modified Meeting");
    eventToModify.setDescription("Updated description");

    calendar.modifyRecurringEventInstance(eventToModify);

    assertAll(
        () -> assertEquals(0, listener1.getAddedCount()),
        () -> assertEquals(1, listener1.getModifiedCount()),
        () -> assertEquals(eventToModify, listener1.getModifiedEvents().get(0)),
        () -> assertEquals(0, listener2.getAddedCount()),
        () -> assertEquals(1, listener2.getModifiedCount()),
        () -> assertEquals(eventToModify, listener2.getModifiedEvents().get(0)),
        () -> assertEquals(0, listener3.getAddedCount()),
        () -> assertEquals(1, listener3.getModifiedCount()),
        () -> assertEquals(eventToModify, listener3.getModifiedEvents().get(0)));
  }

  @Test
  void testUpdateEventNotifies() {
    Calendar calendar = new Calendar("Test Calendar");
    TestListener listener2 = new TestListener();
    TestListener listener3 = new TestListener();
    TestListener listener1 = new TestListener();

    calendar.addCalendarListener(listener2);
    calendar.addCalendarListener(listener3);
    calendar.addCalendarListener(listener1);

    LocalDate startDate = LocalDate.of(2025, 1, 6);
    calendar.addRecurringEvent(
        "Weekly Meeting",
        startDate,
        Set.of(startDate.getDayOfWeek()),
        3,
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        null,
        null,
        null);

    listener1.clear();
    listener2.clear();
    listener3.clear();

    List<Event> events = calendar.getEventsOnDate(startDate);
    Event oldEvent = events.get(0);

    Event newEvent = Event.builder("Updated Meeting", oldEvent.getStartDate())
        .endDate(oldEvent.getEndDate())
        .startTime(oldEvent.getStartTime())
        .endTime(oldEvent.getEndTime())
        .description("New description")
        .location("New location")
        .build();

    calendar.updateEvent(oldEvent, newEvent);

    assertAll(
        () -> assertEquals(0, listener1.getAddedCount()),
        () -> assertEquals(1, listener1.getModifiedCount()),
        () -> assertEquals(newEvent, listener1.getModifiedEvents().get(0)),
        () -> assertEquals(0, listener2.getAddedCount()),
        () -> assertEquals(1, listener2.getModifiedCount()),
        () -> assertEquals(newEvent, listener2.getModifiedEvents().get(0)),
        () -> assertEquals(0, listener3.getAddedCount()),
        () -> assertEquals(1, listener3.getModifiedCount()),
        () -> assertEquals(newEvent, listener3.getModifiedEvents().get(0)));
  }

  @Test
  void testUnregisteredListenerDoesNotReceiveNotifications() {
    Calendar calendar = new Calendar("Test Calendar");
    TestListener unregisteredListener = new TestListener();
    TestListener registeredListener1 = new TestListener();
    TestListener registeredListener2 = new TestListener();

    calendar.addCalendarListener(registeredListener2);
    calendar.addCalendarListener(registeredListener1);

    Event event = Event.builder("Meeting", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .build();
    calendar.addEvent(event);

    Event modifiedEvent = Event.builder("Modified Meeting", LocalDate.of(2025, 1, 2))
        .endDate(LocalDate.of(2025, 1, 2))
        .build();
    calendar.updateEvent(event, modifiedEvent);

    assertAll(
        () -> assertEquals(0, unregisteredListener.getAddedCount()),
        () -> assertEquals(0, unregisteredListener.getModifiedCount()),
        () -> assertEquals(1, registeredListener1.getAddedCount()),
        () -> assertEquals(1, registeredListener1.getModifiedCount()),
        () -> assertEquals(1, registeredListener2.getAddedCount()),
        () -> assertEquals(1, registeredListener2.getModifiedCount()));
  }

  @Test
  void testRemovedListenerDoesNotReceiveNotifications() {
    Calendar calendar = new Calendar("Test Calendar");
    TestListener listener1 = new TestListener();
    TestListener listener2 = new TestListener();
    TestListener listener3 = new TestListener();

    calendar.addCalendarListener(listener3);
    calendar.addCalendarListener(listener1);
    calendar.addCalendarListener(listener2);

    Event event1 = Event.builder("Event 1", LocalDate.of(2025, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .build();
    calendar.addEvent(event1);

    calendar.removeCalendarListener(listener1);

    Event event2 = Event.builder("Event 2", LocalDate.of(2025, 1, 2))
        .endDate(LocalDate.of(2025, 1, 2))
        .build();
    calendar.addEvent(event2);

    Event modifiedEvent2 = Event.builder("Modified Event 2", LocalDate.of(2025, 1, 3))
        .endDate(LocalDate.of(2025, 1, 3))
        .build();
    calendar.updateEvent(event2, modifiedEvent2);

    assertAll(
        () -> assertEquals(1, listener1.getAddedCount()),
        () -> assertEquals(0, listener1.getModifiedCount()),
        () -> assertEquals(event1, listener1.getAddedEvents().get(0)),
        () -> assertEquals(2, listener2.getAddedCount()),
        () -> assertEquals(1, listener2.getModifiedCount()),
        () -> assertEquals(2, listener3.getAddedCount()),
        () -> assertEquals(1, listener3.getModifiedCount()));
  }
}
