package edu.northeastern.cs5010.calendar.view;

import edu.northeastern.cs5010.calendar.model.Calendar;
import edu.northeastern.cs5010.calendar.model.Event;
import edu.northeastern.cs5010.calendar.model.Visibility;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Creates a view for displaying and modifying an existing event.
 */
public class EventDetailView {
  private final Calendar calendar;
  private final Event originalEvent;
  
  private javax.swing.JTextField subjectField;
  private javax.swing.JTextField startDateField;
  private javax.swing.JTextField endDateField;
  private javax.swing.JTextField startTimeField;
  private javax.swing.JTextField endTimeField;
  private javax.swing.JCheckBox allDayCheckBox;
  private javax.swing.JTextArea descriptionArea;
  private javax.swing.JTextField locationField;
  private javax.swing.JComboBox<Visibility> visibilityComboBox;
  
  /**
   * Creates a new EventDetailView with the specified calendar and event.
   *
   * @param calendar the calendar containing the event
   * @param event the event to display and modify
   * @throws IllegalArgumentException if calendar or event is null
   */
  public EventDetailView(Calendar calendar, Event event) {
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar cannot be null");
    }
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }
    this.calendar = calendar;
    this.originalEvent = event;
  }
  
  private void handleSaveButtonClick() {
    try {
      String subject = subjectField.getText().trim();
      if (subject.isEmpty()) {
        System.out.println("Error: Subject cannot be empty");
        return;
      }
  
      String startDateStr = startDateField.getText().trim();
      String endDateStr = endDateField.getText().trim();
      
      LocalDate startDate;
      try {
        startDate = LocalDate.parse(startDateStr);
      } catch (DateTimeParseException e) {
        System.out.println("Error: Invalid start date format. Use yyyy-MM-dd");
        return;
      }
      
      LocalDate endDate;
      try {
        endDate = LocalDate.parse(endDateStr);
      } catch (DateTimeParseException e) {
        System.out.println("Error: Invalid end date format. Use yyyy-MM-dd");
        return;
      }
      
      LocalTime startTime = null;
      LocalTime endTime = null;
      
      if (!allDayCheckBox.isSelected()) {
        String startTimeStr = startTimeField.getText().trim();
        String endTimeStr = endTimeField.getText().trim();
        
        if (!startTimeStr.isEmpty()) {
          try {
            startTime = LocalTime.parse(startTimeStr);
          } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid start time format. Use HH:mm");
            return;
          }
        }
        
        if (!endTimeStr.isEmpty()) {
          try {
            endTime = LocalTime.parse(endTimeStr);
          } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid end time format. Use HH:mm");
            return;
          }
        }
        
        if (startTime != null && endTime == null) {
          System.out.println("Error: End time is required when start time is provided");
          return;
        }
      }
      
      String description = descriptionArea.getText().trim();
      String location = locationField.getText().trim();
      
      Visibility visibility = (Visibility) visibilityComboBox.getSelectedItem();
      if (visibility == null) {
        visibility = calendar.getDefaultVisibility();
      }
      
      boolean isRecurringEvent = false;
      try {
        calendar.getRecurringEventsAll(originalEvent);
        isRecurringEvent = true;
      } catch (IllegalArgumentException e) {
        isRecurringEvent = false;
      }

      if (isRecurringEvent) {
        originalEvent.setSubject(subject);
        originalEvent.setStartDate(startDate);
        originalEvent.setEndDate(endDate);
        originalEvent.setStartTime(startTime);
        originalEvent.setEndTime(endTime);
        originalEvent.setDescription(description.isEmpty() ? null : description);
        originalEvent.setLocation(location.isEmpty() ? null : location);
        originalEvent.setVisibility(visibility);
        calendar.modifyRecurringEventInstance(originalEvent);
      } else {
        Event.Builder builder = Event.builder(subject, startDate)
            .endDate(endDate)
            .visibility(visibility);
        if (startTime != null) {
          builder.startTime(startTime).endTime(endTime);
        }
        if (!description.isEmpty()) {
          builder.description(description);
        }
        if (!location.isEmpty()) {
          builder.location(location);
        }
        Event newEvent = builder.build();
        calendar.updateEvent(originalEvent, newEvent);
      }

    } catch (IllegalArgumentException e) {
      System.out.println("Error updating event: " + e.getMessage());
    }
  }
}
