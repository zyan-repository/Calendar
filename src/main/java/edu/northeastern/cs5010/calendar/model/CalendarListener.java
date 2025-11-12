package edu.northeastern.cs5010.calendar.model;

/**
 * Interface for listeners that want to be notified of calendar changes.
 * Implementations of this interface can register with a Calendar to receive
 * notifications when events are added or modified.
 *
 * <p>The listener pattern allows for decoupled observation of calendar changes,
 * enabling multiple observers to react to the same events without tight coupling.
 *
 * <p>Example implementation:
 * <pre>
 * public class EventLogger implements CalendarListener {
 *     {@literal @}Override
 *     public void onEventAdded(Event event) {
 *         System.out.println("Event added: " + event.getSubject());
 *     }
 *
 *     {@literal @}Override
 *     public void onEventModified(Event event) {
 *         System.out.println("Event modified: " + event.getSubject());
 *     }
 * }
 * </pre>
 *
 * @see Calendar#addCalendarListener(CalendarListener)
 * @see Calendar#removeCalendarListener(CalendarListener)
 */
public interface CalendarListener {

  /**
   * Called when an event is added to the calendar.
   * This includes both single events and individual occurrences
   * of recurring events.
   *
   * @param event the event that was added (never null)
   */
  void onEventAdded(Event event);

  /**
   * Called when an event in the calendar is modified.
   * This is called after the modification has been successfully applied.
   *
   * @param event the event after modification (never null)
   */
  void onEventModified(Event event);
}