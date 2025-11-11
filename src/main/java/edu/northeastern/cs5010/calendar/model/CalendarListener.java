package edu.northeastern.cs5010.calendar.model;

/**
 * Interface for listeners that want to be notified of calendar changes.
 */
public interface CalendarListener {

  void onEventAdded(Event event);

  void onEventModified(Event event);
}