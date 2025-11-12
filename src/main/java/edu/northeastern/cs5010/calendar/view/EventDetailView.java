package edu.northeastern.cs5010.calendar.view;

import edu.northeastern.cs5010.calendar.model.Calendar;
import edu.northeastern.cs5010.calendar.model.Event;
import edu.northeastern.cs5010.calendar.model.Visibility;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Creates a view for displaying and modifying an existing event.
 * This view shows all event details and allows the user to modify them.
 */
public class EventDetailView extends JFrame {
  private final Calendar calendar;
  private final Event originalEvent;
  
  // GUI components for event display and modification
  private JTextField subjectField;
  private JTextField startDateField;
  private JTextField endDateField;
  private JTextField startTimeField;
  private JTextField endTimeField;
  private JCheckBox allDayCheckBox;
  private JTextArea descriptionArea;
  private JTextField locationField;
  private JComboBox<Visibility> visibilityComboBox;
  
  /**
   * Creates a new EventDetailView with the specified calendar and event.
   * Initializes the GUI and populates fields with the event's current data.
   *
   * @param calendar the calendar containing the event
   * @param event the event to display and modify
   * @throws IllegalArgumentException if calendar or event is null
   */
  @SuppressFBWarnings({"CT_CONSTRUCTOR_THROW", "EI_EXPOSE_REP2"})
  public EventDetailView(Calendar calendar, Event event) {
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar cannot be null");
    }
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }
    // EI_EXPOSE_REP2: Storing calendar and event references is intentional for MVC pattern.
    // View needs to call calendar.updateEvent() and calendar.modifyRecurringEventInstance().
    // Both fields are final to prevent reassignment.
    this.calendar = calendar;
    this.originalEvent = event;
    
    // Initialize the GUI
    initializeGui();
    
    // Populate fields with event data
    populateFields();
  }
  
  /**
   * Initializes the graphical user interface components and layout.
   * Creates a form similar to CreateEventView but for editing an existing event.
   */
  private void initializeGui() {
    // Set up the main frame
    setTitle("Event Details - " + calendar.getTitle());
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));
    
    // Create main panel with padding
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);
    
    int row = 0;
    
    // Subject field (required)
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    mainPanel.add(new JLabel("Subject*:"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    subjectField = new JTextField(30);
    mainPanel.add(subjectField, gbc);
    row++;
    
    // Start date field (required)
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    mainPanel.add(new JLabel("Start Date* (yyyy-MM-dd):"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    startDateField = new JTextField(15);
    mainPanel.add(startDateField, gbc);
    row++;
    
    // End date field (required)
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    mainPanel.add(new JLabel("End Date* (yyyy-MM-dd):"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    endDateField = new JTextField(15);
    mainPanel.add(endDateField, gbc);
    row++;
    
    // All-day checkbox
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    mainPanel.add(new JLabel("All Day Event:"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    allDayCheckBox = new JCheckBox();
    // Add listener to enable/disable time fields based on all-day selection
    allDayCheckBox.addActionListener(e -> {
      boolean isAllDay = allDayCheckBox.isSelected();
      startTimeField.setEnabled(!isAllDay);
      endTimeField.setEnabled(!isAllDay);
      if (isAllDay) {
        startTimeField.setText("");
        endTimeField.setText("");
      }
    });
    mainPanel.add(allDayCheckBox, gbc);
    row++;
    
    // Start time field (optional, disabled if all-day)
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    mainPanel.add(new JLabel("Start Time (HH:mm):"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    startTimeField = new JTextField(10);
    mainPanel.add(startTimeField, gbc);
    row++;
    
    // End time field (optional, disabled if all-day)
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    mainPanel.add(new JLabel("End Time (HH:mm):"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    endTimeField = new JTextField(10);
    mainPanel.add(endTimeField, gbc);
    row++;
    
    // Location field (optional)
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    mainPanel.add(new JLabel("Location:"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    locationField = new JTextField(30);
    mainPanel.add(locationField, gbc);
    row++;
    
    // Visibility combo box
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    mainPanel.add(new JLabel("Visibility:"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    visibilityComboBox = new JComboBox<>(Visibility.values());
    mainPanel.add(visibilityComboBox, gbc);
    row++;
    
    // Description text area (optional, spans both columns)
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    mainPanel.add(new JLabel("Description:"), gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    descriptionArea = new JTextArea(5, 30);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    JScrollPane descScrollPane = new JScrollPane(descriptionArea);
    mainPanel.add(descScrollPane, gbc);
    row++;
    
    // Reset fill and weighty for buttons
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weighty = 0;
    
    // Add main panel to frame
    add(mainPanel, BorderLayout.CENTER);
    
    // Create button panel at the bottom
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
    
    // Save button - connected to the user-implemented handleSaveButtonClick method
    JButton saveButton = new JButton("Save Changes");
    saveButton.addActionListener(e -> handleSaveButtonClick());
    buttonPanel.add(saveButton);
    
    // Cancel button - closes the window without saving
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);
    
    add(buttonPanel, BorderLayout.SOUTH);
    
    // Finalize frame setup
    pack();
    setMinimumSize(new Dimension(500, 600));
    setLocationRelativeTo(null); // Center on screen
  }
  
  /**
   * Populates the GUI fields with data from the original event.
   * This method is called after the GUI is initialized to display the event's current values.
   */
  private void populateFields() {
    // Set subject
    subjectField.setText(originalEvent.getSubject());
    
    // Set dates
    startDateField.setText(originalEvent.getStartDate().toString());
    endDateField.setText(originalEvent.getEndDate().toString());
    
    // Set times (if event has times)
    LocalTime startTime = originalEvent.getStartTime();
    LocalTime endTime = originalEvent.getEndTime();
    
    if (startTime != null && endTime != null) {
      // Event has specific times (not all-day)
      allDayCheckBox.setSelected(false);
      startTimeField.setEnabled(true);
      endTimeField.setEnabled(true);
      startTimeField.setText(startTime.toString());
      endTimeField.setText(endTime.toString());
    } else {
      // All-day event
      allDayCheckBox.setSelected(true);
      startTimeField.setEnabled(false);
      endTimeField.setEnabled(false);
      startTimeField.setText("");
      endTimeField.setText("");
    }
    
    // Set location (may be null)
    String location = originalEvent.getLocation();
    locationField.setText(location != null ? location : "");
    
    // Set visibility
    visibilityComboBox.setSelectedItem(originalEvent.getVisibility());
    
    // Set description (may be null)
    String description = originalEvent.getDescription();
    descriptionArea.setText(description != null ? description : "");
  }
  
  /**
   * Displays the view window.
   * This method should be called on the Event Dispatch Thread.
   */
  public void display() {
    // If already on EDT, set visible directly; otherwise invoke later
    if (SwingUtilities.isEventDispatchThread()) {
      setVisible(true);
      toFront(); // Bring window to front
      requestFocus(); // Request focus
    } else {
      SwingUtilities.invokeLater(() -> {
        setVisible(true);
        toFront();
        requestFocus();
      });
    }
  }
  
  private void handleSaveButtonClick() {
    try {
      String subject = subjectField.getText().trim();
      if (subject.isEmpty()) {
        showError("Subject cannot be empty");
        return;
      }
  
      String startDateStr = startDateField.getText().trim();
      String endDateStr = endDateField.getText().trim();
      
      LocalDate startDate;
      try {
        startDate = LocalDate.parse(startDateStr);
      } catch (DateTimeParseException e) {
        showError("Invalid start date format. Use yyyy-MM-dd (e.g., 2025-01-15)");
        return;
      }
      
      LocalDate endDate;
      try {
        endDate = LocalDate.parse(endDateStr);
      } catch (DateTimeParseException e) {
        showError("Invalid end date format. Use yyyy-MM-dd (e.g., 2025-01-15)");
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
            showError("Invalid start time format. Use HH:mm (e.g., 14:30)");
            return;
          }
        }
        
        if (!endTimeStr.isEmpty()) {
          try {
            endTime = LocalTime.parse(endTimeStr);
          } catch (DateTimeParseException e) {
            showError("Invalid end time format. Use HH:mm (e.g., 15:30)");
            return;
          }
        }
        
        if (startTime != null && endTime == null) {
          showError("End time is required when start time is provided");
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
      
      // Show success message
      JOptionPane.showMessageDialog(this,
          "Event updated successfully!",
          "Success",
          JOptionPane.INFORMATION_MESSAGE);

    } catch (IllegalArgumentException e) {
      showError("Error updating event: " + e.getMessage());
    } catch (Exception e) {
      showError("Unexpected error: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  /**
   * Shows an error message dialog to the user.
   *
   * @param message the error message to display
   */
  private void showError(String message) {
    JOptionPane.showMessageDialog(this,
        message,
        "Error",
        JOptionPane.ERROR_MESSAGE);
    // Also print to console for debugging
    System.err.println("Error: " + message);
  }
}
