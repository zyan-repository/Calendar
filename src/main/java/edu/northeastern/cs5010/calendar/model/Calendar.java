package edu.northeastern.cs5010.calendar.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a calendar that can hold events.
 */
public class Calendar {

  private String title;
  private final List<Event> events = new ArrayList<>();
  private Visibility defaultVisibility = Visibility.PUBLIC;
  private final List<List<Event>> recurringSet = new ArrayList<>();
  private final List<CalendarListener> listeners =
      Collections.synchronizedList(new ArrayList<>());

  /**
   * Creates a new calendar with the specified title.
   *
   * @param title the title of the calendar
   * @throws IllegalArgumentException if title is null or blank
   */
  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  public Calendar(String title) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Title cannot be null or blank");
    }
    this.title = title;
  }

  /**
   * Creates a new calendar with the specified title and default visibility.
   * If defaultVisibility is null, uses Visibility.PUBLIC as the default.
   *
   * @param title the title of the calendar
   * @param defaultVisibility the default visibility level for events
   *                          (null to use Visibility.PUBLIC)
   * @throws IllegalArgumentException if title is null or blank
   */
  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  public Calendar(String title, Visibility defaultVisibility) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Title cannot be null or blank");
    }
    this.title = title;
    this.defaultVisibility = Objects.requireNonNullElse(defaultVisibility, Visibility.PUBLIC);
  }

  /**
   * Returns the title of the calendar.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of the calendar.
   *
   * @param title the new title
   * @throws IllegalArgumentException if title is null or blank
   */
  public void setTitle(String title) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Title cannot be null or blank");
    }
    this.title = title;
  }

  /**
   * Returns the default visibility level for events in this calendar.
   *
   * @return the default visibility
   */
  public Visibility getDefaultVisibility() {
    return defaultVisibility;
  }

  /**
   * Sets the default visibility level for events in this calendar.
   *
   * @param defaultVisibility the default visibility level
   * @throws IllegalArgumentException if defaultVisibility is null
   */
  public void setDefaultVisibility(Visibility defaultVisibility) {
    if (defaultVisibility == null) {
      throw new IllegalArgumentException("Default visibility cannot be null");
    }
    this.defaultVisibility = defaultVisibility;
  }

  /**
   * Adds a single event to the calendar.
   * Validates for duplicates and conflicts with existing events.
   *
   * @param event the event to add
   * @throws IllegalArgumentException if the event is null, duplicates an existing event,
   *         or conflicts with an existing event
   */
  public void addEvent(Event event) {
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }

    events.add(event);

    try {
      validateEventModification(event);
      announceEventAdded(event);
    } catch (IllegalArgumentException e) {
      events.remove(event);
      throw e;
    }

  }

  /**
   * Creates a recurring event that repeats on specific days of the week.
   * Each occurrence must start and end on the same day.
   *
   * @param subject the subject of the event (required)
   * @param startDate the first occurrence date (required)
   * @param daysOfWeek the days of the week on which the event repeats (required)
   * @param numOccurrences the number of occurrences (required)
   * @param startTime the start time (optional, null for all-day events)
   * @param endTime the end time (required if startTime is provided)
   * @param description the description (optional)
   * @param location the location (optional)
   * @param visibility the visibility (optional, uses default if null)
   * @throws IllegalArgumentException if validation fails or any occurrence would conflict
   */
  public void addRecurringEvent(String subject, LocalDate startDate, Set<DayOfWeek> daysOfWeek,
      int numOccurrences, LocalTime startTime, LocalTime endTime,
      String description, String location, Visibility visibility) {
    
    if (daysOfWeek == null || daysOfWeek.isEmpty()) {
      throw new IllegalArgumentException("Days of week cannot be null or empty");
    }

    if (numOccurrences <= 0) {
      throw new IllegalArgumentException("Number of occurrences must be positive");
    }

    Visibility eventVisibility = Objects.requireNonNullElse(visibility, defaultVisibility);

    int count = 0;
    LocalDate current = startDate;
    while (count < numOccurrences) {
      if (daysOfWeek.contains(current.getDayOfWeek())) {
        count++;
      }
      if (count < numOccurrences) {
        current = current.plusDays(1);
      }
    }
    LocalDate actualEndDate = current;

    List<Event> occurrences = new ArrayList<>();
    LocalDate currentDate = startDate;
    int occurrenceCount = 0;
    
    while (!currentDate.isAfter(actualEndDate)) {
      if (daysOfWeek.contains(currentDate.getDayOfWeek())) {
        Event.Builder builder = Event.builder(subject, currentDate)
            .endDate(currentDate)
            .visibility(eventVisibility);
        
        if (startTime != null) {
          builder.startTime(startTime).endTime(endTime);
        }
        if (description != null) {
          builder.description(description);
        }
        if (location != null) {
          builder.location(location);
        }
        
        Event occurrence = builder.build();

        if (hasDuplicate(occurrence)) {
          throw new IllegalArgumentException(
              "A recurring event occurrence would duplicate an existing event");
        }

        if (hasConflict(occurrence)) {
          throw new IllegalArgumentException(
              "A recurring event occurrence would conflict with an existing event");
        }
        
        occurrences.add(occurrence);
        occurrenceCount++;
        
        if (occurrenceCount >= numOccurrences) {
          break;
        }
      }
      currentDate = currentDate.plusDays(1);
    }

    events.addAll(occurrences);
    
    recurringSet.add(new ArrayList<>(occurrences));

    for (Event occurrence : occurrences) {
      announceEventAdded(occurrence);
    }
  }

  /**
   * Removes an event from the calendar.
   *
   * @param event the event to remove
   * @throws IllegalArgumentException if the event is null or not in the calendar
   */
  public void removeEvent(Event event) {
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }
    if (!events.contains(event)) {
      throw new IllegalArgumentException("Event is not in this calendar");
    }
    events.remove(event);

    for (List<Event> series : recurringSet) {
      series.remove(event);
    }
  }

  /**
   * Returns all events in the calendar.
   *
   * @return an unmodifiable copy of the events list
   */
  public List<Event> getEvents() {
    return Collections.unmodifiableList(new ArrayList<>(events));
  }

  /**
   * Returns an event by its subject, start date, and start time.
   *
   * @param subject the subject of the event
   * @param startDate the start date of the event
   * @param startTime the start time of the event (null for all-day events)
   * @return the event if found, null otherwise
   */
  public Event getEvent(String subject, LocalDate startDate, LocalTime startTime) {
    if (subject == null || startDate == null) {
      return null;
    }

    Event.Builder builder = Event.builder(subject, startDate);
    if (startTime != null) {
      builder.startTime(startTime);
    }

    builder.endDate(startDate);

    Event findEvent = builder.build();
    
    for (Event event : events) {
      if (event.equals(findEvent)) {
        return event;
      }
    }
    return null;
  }

  /**
   * Returns all events on a specific date.
   *
   * @param date the date to query
   * @return a list of events on that date
   */
  public List<Event> getEventsOnDate(LocalDate date) {
    if (date == null) {
      return Collections.emptyList();
    }
    List<Event> result = new ArrayList<>();
    for (Event event : events) {
      if (isEventOnDate(event, date)) {
        result.add(event);
      }
    }
    return result;
  }

  /**
   * Returns all events in a date range (inclusive).
   *
   * @param startDate the start date of the range (inclusive)
   * @param endDate the end date of the range (inclusive)
   * @return a list of events in the date range
   * @throws IllegalArgumentException if startDate is after endDate
   */
  public List<Event> getEventsInRange(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Start date and end date cannot be null");
    }
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    }
    List<Event> result = new ArrayList<>();
    for (Event event : events) {
      if (isEventInRange(event, startDate, endDate)) {
        result.add(event);
      }
    }
    return result;
  }

  /**
   * Returns whether the user is busy at a specific date and time.
   *
   * @param date the date to check
   * @param time the time to check
   * @return true if there is an event at that time, false otherwise
   */
  public boolean isBusy(LocalDate date, LocalTime time) {
    if (date == null || time == null) {
      return false;
    }
    LocalDateTime dateTime = LocalDateTime.of(date, time);
    for (Event event : events) {
      if (isDateTimeInEvent(event, dateTime)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Modifies a single instance of a recurring event.
   * The event should be modified using its setter methods before calling this method.
   *
   * @param event the specific occurrence to modify (already modified using setter methods)
   * @throws IllegalArgumentException if the event is not part of a recurring series
   */
  public void modifyRecurringEventInstance(Event event) {
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }
    
    List<Event> originalSeries = findSeriesByEvent(event);
    
    originalSeries.remove(event);
    
    validateEventModification(event);

    announceEventModified(event);
  }

  /**
   * Returns all events in a recurring series starting at a specific date/time.
   * The caller should modify these events using their setter methods,
   * then call modifyRecurringEventInstance for each modified event.
   *
   * @param event a representative event from the series
   * @param startDate the date from which to start modifying
   * @return a list of events that should be modified
   * @throws IllegalArgumentException if the event is not part of a recurring series
   */
  public List<Event> getRecurringEventsFromDate(Event event, LocalDate startDate) {
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }
    if (startDate == null) {
      throw new IllegalArgumentException("Start date cannot be null");
    }
    
    List<Event> series = findSeriesByEvent(event);
    
    List<Event> toModify = new ArrayList<>();
    for (Event e : series) {
      if (!e.getStartDate().isBefore(startDate)) {
        toModify.add(e);
      }
    }
    return new ArrayList<>(toModify);
  }

  /**
   * Returns all events in a recurring series.
   * The caller should modify these events using their setter methods,
   * then call modifyRecurringEventInstance for each modified event.
   *
   * @param event a representative event from the series
   * @return a list of all events in the series
   * @throws IllegalArgumentException if the event is not part of a recurring series
   */
  public List<Event> getRecurringEventsAll(Event event) {
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }
    
    List<Event> series = findSeriesByEvent(event);
    return new ArrayList<>(series);
  }

  /**
   * Updates an event by replacing it with a new event.
   *
   * @param oldEvent the event to replace (must be in the calendar)
   * @param newEvent the new event to replace it with
   * @throws IllegalArgumentException if oldEvent is not in calendar or newEvent violates rules
   */
  public void updateEvent(Event oldEvent, Event newEvent) {
    if (oldEvent == null || newEvent == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }
    if (!events.contains(oldEvent)) {
      throw new IllegalArgumentException("Event is not in this calendar");
    }
    
    events.remove(oldEvent);

    try {

      events.add(newEvent);
      validateEventModification(newEvent);
      announceEventModified(newEvent);

    } catch (IllegalArgumentException e) {
      events.remove(newEvent);
      events.add(oldEvent);
      throw e;
    }
  }

  /**
   * Exports the calendar to CSV format compatible with Google Calendar.
   *
   * @param writer the writer to write the CSV to
   */
  public void exportToCsv(Writer writer) {
    String[] headers = {"Subject", "Start Date", "Start Time", "End Date", 
        "End Time", "All Day Event", "Description", "Location", "Private"};
    
    List<String[]> data = new ArrayList<>();
    for (Event event : events) {
      data.add(convertEventToCsvRow(event));
    }
    
    try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
      // write headers
      bufferedWriter.write(String.join(",", headers));
      bufferedWriter.newLine();
      
      // write data
      for (String[] row : data) {
        bufferedWriter.write(String.join(",", row));
        bufferedWriter.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Imports events from a CSV file in the same format as exportToCsv.
   *
   * @param reader the reader to read the CSV from
   * @throws IOException if there is an error reading the file
   */
  public void importFromCsv(Reader reader) throws IOException {
    // TODO: Read CSV file and import events into this calendar
  }

  /**
   * Saves all calendars to CSV files in the specified directory.
   *
   * @param calendars the list of calendars to save
   * @param directoryPath the directory path where calendars should be saved
   * @throws IOException if there is an error creating the directory or writing files
   */
  public static void saveAllCalendars(List<Calendar> calendars, String directoryPath) throws IOException {
    // TODO: Save all calendars to CSV files
  }

  /**
   * Restores all calendars from CSV files in the specified directory.
   *
   * @param directoryPath the directory path where calendars are stored
   * @return a list of restored calendars (empty if directory doesn't exist)
   * @throws IOException if there is an error reading the directory or files
   */
  public static List<Calendar> restoreAllCalendars(String directoryPath) throws IOException {
    // TODO: Restore all calendars from CSV files
    return new ArrayList<>();
  }

  /**
   * Adds a calendar listener to receive notifications about calendar changes.
   * The listener will be notified when events are added or modified.
   *
   * <p>Example usage:
   * <pre>
   * calendar.addCalendarListener(new CalendarListener() {
   *     public void onEventAdded(Event event) {
   *         System.out.println("Event added: " + event.getSubject());
   *     }
   *     public void onEventModified(Event event) {
   *         System.out.println("Event modified: " + event.getSubject());
   *     }
   * });
   * </pre>
   *
   * @param listener the listener to add
   * @throws IllegalArgumentException if listener is null
   */
  public void addCalendarListener(CalendarListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null");
    }
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Removes a calendar listener so it no longer receives notifications.
   *
   * @param listener the listener to remove
   */
  public void removeCalendarListener(CalendarListener listener) {
    listeners.remove(listener);
  }

  private void announceEventAdded(Event event) {
    for (CalendarListener listener : listeners) {
      try {
        listener.onEventAdded(event);
      } catch (Exception e) {
        // Log error but continue notifying other listeners
        System.err.println("Listener notification failed: " + e.getMessage());
      }
    }
  }

  private void announceEventModified(Event event) {
    for (CalendarListener listener : listeners) {
      try {
        listener.onEventModified(event);
      } catch (Exception e) {
        // Log error but continue notifying other listeners
        System.err.println("Listener notification failed: " + e.getMessage());
      }
    }
  }

  @Override
  public String toString() {
    return "Calendar [title=" + title + ", events=" + events + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Calendar other)) {
      return false;
    }
    return Objects.equals(title, other.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title);
  }

  // ==================== Private Helper Methods ====================

  private boolean isEventOnDate(Event event, LocalDate date) {
    return !(date.isBefore(event.getStartDate()) || date.isAfter(event.getEndDate()));
  }

  private boolean isEventInRange(Event event, LocalDate startDate, LocalDate endDate) {
    return !(event.getEndDate().isBefore(startDate) || event.getStartDate().isAfter(endDate));
  }

  private boolean isDateTimeInEvent(Event event, LocalDateTime dateTime) {
    return !dateTime.isBefore(event.getStartDateTime())
        && dateTime.isBefore(event.getEndDateTime());
  }

  private boolean hasDuplicate(Event event) {
    for (Event existing : events) {
      if (event.equals(existing)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasConflict(Event event) {
    for (Event existing : events) {
      if (event.conflictsWith(existing)) {
        return true;
      }
    }
    return false;
  }

  private String[] convertEventToCsvRow(Event event) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    String[] row = new String[9];
    row[0] = event.getSubject();
    row[1] = event.getStartDate().format(dateFormatter);
    LocalTime startTime = event.getStartTime();
    row[2] = startTime != null ? startTime.toString() : "";
    row[3] = event.getEndDate().format(dateFormatter);
    LocalTime endTime = event.getEndTime();
    row[4] = endTime != null ? endTime.toString() : "";
    row[5] = event.isAllDay() ? "True" : "False";
    row[6] = Objects.requireNonNullElse(event.getDescription(), "");
    row[7] = Objects.requireNonNullElse(event.getLocation(), "");
    row[8] = event.getVisibility() == Visibility.PRIVATE ? "True" : "False";
    return row;
  }

  private List<Event> findSeriesByEvent(Event event) {
    for (List<Event> series : recurringSet) {
      for (Event e : series) {
        if (e == event) {
          return series;
        }
      }
    }
    throw new IllegalArgumentException("Event is not part of a recurring series");
  }

  private void validateEventModification(Event event) {
    events.remove(event);
    try {
      if (hasDuplicate(event)) {
        throw new IllegalArgumentException(
            "Update would create a duplicate event with the same subject, "
                + "start date, and start time");
      }
      if (hasConflict(event)) {
        throw new IllegalArgumentException(
            "Update would create a conflict with an existing event");
      }
    } finally {
      events.add(event);
    }
  }
  
}
