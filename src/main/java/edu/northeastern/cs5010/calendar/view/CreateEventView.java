package edu.northeastern.cs5010.calendar.view;

import edu.northeastern.cs5010.calendar.model.Calendar;
import edu.northeastern.cs5010.calendar.model.Event;
import edu.northeastern.cs5010.calendar.model.Visibility;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Creates a view for creating new events in a calendar.
 */
public class CreateEventView {
  private final Calendar calendar;
  
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
   * Creates a new CreateEventView with the specified calendar.
   *
   * @param calendar the calendar to add the event to
   * @throws IllegalArgumentException if calendar is null
   */
  public CreateEventView(Calendar calendar) {
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar cannot be null");
    }
    this.calendar = calendar;
  }
  
  /**
   * Creates an Event from the provided data and adds it to the calendar.
   *
   * @param subject the event subject (required)
   * @param startDate the start date (required)
   * @param endDate the end date (required)
   * @param startTime the start time (optional, null for all-day events)
   * @param endTime the end time (required if startTime is provided)
   * @param description the description (optional)
   * @param location the location (optional)
   * @param visibility the visibility (optional)
   * @throws IllegalArgumentException if event creation fails or event conflicts with existing events
   */
  public void handleSave(
      String subject,
      LocalDate startDate,
      LocalDate endDate,
      LocalTime startTime,
      LocalTime endTime,
      String description,
      String location,
      Visibility visibility) {
    
    Event.Builder builder = Event.builder(subject, startDate)
        .endDate(endDate);
    
    if (startTime != null) {
      builder.startTime(startTime).endTime(endTime);
    }
    
    if (description != null && !description.isBlank()) {
      builder.description(description);
    }
    
    if (location != null && !location.isBlank()) {
      builder.location(location);
    }
    
    builder.visibility(visibility);
    
    Event event = builder.build();
    
    calendar.addEvent(event);
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
      
      handleSave(subject, startDate, endDate, startTime, endTime, 
                 description, location, visibility);
      
    } catch (IllegalArgumentException e) {
      System.out.println("Error creating event: " + e.getMessage());
    }
  }
}
