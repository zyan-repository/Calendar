package edu.northeastern.cs5010.calendar.controller;

import edu.northeastern.cs5010.calendar.model.Calendar;
import edu.northeastern.cs5010.calendar.model.CalendarListener;
import edu.northeastern.cs5010.calendar.model.Event;
import edu.northeastern.cs5010.calendar.view.CreateEventView;
import edu.northeastern.cs5010.calendar.view.EventDetailView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Controller for the Calendar application.
 */
public class CalendarController {
  
  private static final String DATA_DIRECTORY = "data/calendars";
  
  private final List<Calendar> allCalendars;
  private final Calendar selectedCalendar;

  /**
   * Creates a new CalendarController.
   * Restores calendars, selects one arbitrarily, and displays the views.
   */
  public CalendarController() {
    List<Calendar> calendars;
    try {
      calendars = Calendar.restoreAllCalendars(DATA_DIRECTORY);
    } catch (IOException e) {
      calendars = new ArrayList<>();
    }
    
    if (calendars.isEmpty()) {
      selectedCalendar = new Calendar("My Calendar");
      calendars.add(selectedCalendar);
    } else {
      selectedCalendar = calendars.get(0);
    }
    
    allCalendars = calendars;
    
    selectedCalendar.addCalendarListener(new CalendarListener() {
      @Override
      public void onEventAdded(Event event) {
        saveAllCalendars();
      }
      
      @Override
      public void onEventModified(Event event) {
        saveAllCalendars();
      }
    });
    
    SwingUtilities.invokeLater(() -> {
      CreateEventView createView = new CreateEventView(selectedCalendar);
      createView.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          saveAllCalendars();
        }
      });
      createView.display();
      
      List<Event> allEvents = selectedCalendar.getEvents();
      if (!allEvents.isEmpty()) {
        EventDetailView detailView = new EventDetailView(selectedCalendar, allEvents.get(0));
        detailView.addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            saveAllCalendars();
          }
        });
        detailView.display();
      }
    });
  }
  
  private void saveAllCalendars() {
    try {
      Calendar.saveAllCalendars(allCalendars, DATA_DIRECTORY);
    } catch (IOException e) {
      System.err.println("Error saving calendars: " + e.getMessage());
    }
  }

  /**
   * Main method to start the Calendar application.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    new CalendarController();
  }
}
