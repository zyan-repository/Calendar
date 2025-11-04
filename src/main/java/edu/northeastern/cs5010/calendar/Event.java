package edu.northeastern.cs5010.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents an event in a calendar.
 */
public class Event {
  private String subject;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalTime startTime;
  private LocalTime endTime;
  private Visibility visibility;
  private String description;
  private String location;

  private Event(Builder builder) {

    if (builder.startTime == null && builder.endDate == null) {
      builder.endDate = builder.startDate;
    }

    this.visibility = Objects.requireNonNullElse(builder.visibility, Visibility.PUBLIC);

    validateState(builder.subject, builder.startDate, builder.endDate, builder.startTime,
        builder.endTime);

    this.subject = builder.subject;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.description = builder.description;
    this.location = builder.location;
  }
  
  private static void validateState(String subject, LocalDate startDate, LocalDate endDate,
      LocalTime startTime, LocalTime endTime) {

    if (subject == null || subject.isBlank()) {
      throw new IllegalArgumentException("Subject cannot be null or empty");
    }
    if (startDate == null) {
      throw new IllegalArgumentException("Start date cannot be null");
    }

    if (startTime == null && endTime != null) {
      throw new IllegalArgumentException(
          "End time cannot be set without start time for all-day events");
    }

    if (startTime != null && endDate == null) {
      throw new IllegalArgumentException(
          "End date is required when start time is provided");
    }

    if (startTime != null && endTime == null) {
      throw new IllegalArgumentException(
          "End time is required when start time is provided");
    }

    if (endDate != null && endDate.isBefore(startDate)) {
      throw new IllegalArgumentException(
          "End date cannot be before start date");
    }

    if (startTime != null && endTime != null && endDate != null) {
      if (startDate.equals(endDate) && endTime.isBefore(startTime)) {
        throw new IllegalArgumentException(
            "End time cannot be before start time on the same day");
      }
    }
  }

  /**
   * Creates a new Builder with required parameters.
   *
   * @param subject the subject of the event (required)
   * @param startDate the start date of the event (required)
   * @return a new Builder instance
   */
  public static Builder builder(String subject, LocalDate startDate) {
    return new Builder(subject, startDate);
  }

  /**
   * Builder class for constructing Event instances.
   */
  public static class Builder {
    // required parameters
    private String subject;
    private LocalDate startDate;
    
    // optional parameters
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Visibility visibility;
    private String description;
    private String location;
    
    private Builder(String subject, LocalDate startDate) {
      this.subject = subject;
      this.startDate = startDate;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime the start time
     * @return this builder for method chaining
     */
    public Builder startTime(LocalTime startTime) {
      this.startTime = startTime;
      return this;
    }
    
    /**
     * Sets the end time of the event.
     *
     * @param endTime the end time
     * @return this builder for method chaining
     */
    public Builder endTime(LocalTime endTime) {
      this.endTime = endTime;
      return this;
    }
    
    /**
     * Sets the end date of the event.
     * Note: Required if start time is provided.
     *
     * @param endDate the end date
     * @return this builder for method chaining
     */
    public Builder endDate(LocalDate endDate) {
      this.endDate = endDate;
      return this;
    }
    
    /**
     * Sets the visibility of the event.
     *
     * @param visibility the visibility level
     * @return this builder for method chaining
     */
    public Builder visibility(Visibility visibility) {
      this.visibility = visibility;
      return this;
    }
    
    /**
     * Sets the description of the event.
     *
     * @param description the description
     * @return this builder for method chaining
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }
    
    /**
     * Sets the location of the event.
     *
     * @param location the location
     * @return this builder for method chaining
     */
    public Builder location(String location) {
      this.location = location;
      return this;
    }
    
    /**
     * Builds the Event instance after validating all parameters.
     *
     * @return a new Event instance
     * @throws IllegalArgumentException if validation fails
     */
    public Event build() {
      return new Event(this);
    }
  }


  // ==================== Getters ====================

  /**
   * Gets the subject of the event.
   *
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Gets the start date of the event.
   *
   * @return the start date
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * Gets the end date of the event.
   *
   * @return the end date
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * Gets the start time of the event.
   *
   * @return the start time
   */
  public LocalTime getStartTime() {
    return startTime;
  }

  /**
   * Gets the end time of the event.
   *
   * @return the end time
   */
  public LocalTime getEndTime() {
    return endTime;
  }
  
  /**
   * Gets the visibility of the event.
   *
   * @return the visibility
   */
  public Visibility getVisibility() {
    return visibility;
  }

  /**
   * Gets the description of the event.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the location of the event.
   *
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  // ==================== Setters ====================

  /**
   * Sets the subject of the event.
   *
   * @param subject the new subject
   * @throws IllegalArgumentException if subject is null or empty
   */
  public void setSubject(String subject) {
    validateState(subject, this.startDate, this.endDate, this.startTime, this.endTime);
    this.subject = subject;
  }

  /**
   * Sets the start date of the event.
   *
   * @param startDate the new start date
   * @throws IllegalArgumentException if start date is null
   */
  public void setStartDate(LocalDate startDate) {
    validateState(subject, startDate, this.endDate, this.startTime, this.endTime);
    this.startDate = startDate;
  }

  /**
   * Sets the end date of the event.
   *
   * @param endDate the new end date
   * @throws IllegalArgumentException if end date is null
   */
  public void setEndDate(LocalDate endDate) {
    validateState(subject, this.startDate, endDate, this.startTime, this.endTime);
    this.endDate = endDate;
  }
  
  /**
   * Sets the start time of the event.
   *
   * @param startTime the new start time
   * @throws IllegalArgumentException if start time is null
   */
  public void setStartTime(LocalTime startTime) {
    validateState(subject, this.startDate, this.endDate, startTime, this.endTime);
    this.startTime = startTime;
  }

  /**
   * Sets the end time of the event.
   *
   * @param endTime the new end time
   * @throws IllegalArgumentException if end time is null
   */
  public void setEndTime(LocalTime endTime) {
    validateState(subject, this.startDate, this.endDate, this.startTime, endTime);
    this.endTime = endTime;
  }

  /**
   * Sets the visibility of the event.
   *
   * @param visibility the new visibility
   * @throws IllegalArgumentException if visibility is null
   */
  public void setVisibility(Visibility visibility) {
    if (visibility == null) {
      throw new IllegalArgumentException("Visibility cannot be null");
    }
    this.visibility = visibility;
  }

  /**
   * Sets the description of the event.
   *
   * @param description the new description
   * @throws IllegalArgumentException if description is null
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the location of the event.
   *
   * @param location the new location
   * @throws IllegalArgumentException if location is null
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Checks if the event is all-day.
   *
   * @return true if the event is an all-day event, false otherwise
   */
  public boolean isAllDay() {
    return startTime == null && endTime == null;
  }

  /**
   * Gets the exact start moment of the event (inclusive).
   * For all-day events, this is 00:00 on the start date.
   *
   * @return the start LocalDateTime
   */
  public LocalDateTime getStartDateTime() {
    if (isAllDay()) {
      return this.startDate.atStartOfDay();
    }
    return LocalDateTime.of(this.startDate, this.startTime);
  }

  /**
   * Gets the exact end moment of the event (exclusive).
   * For all-day events, this is 00:00 on the day after their end date.
   * For timed events, this is their specified end date and time.
   *
   * @return the end LocalDateTime (exclusive)
   */
  public LocalDateTime getEndDateTime() {
    if (isAllDay()) {
      return this.endDate.plusDays(1).atStartOfDay();
    }

    return LocalDateTime.of(this.endDate, this.endTime);
  }

  /**
   * Checks if this event conflicts with another event.
   * Two events conflict if their time intervals [start, end) overlap.
   *
   * @param other the other event to check
   * @return true if the events conflict, false otherwise
   */
  public boolean conflictsWith(Event other) {
    if (other == null) {
      return false;
    }

    LocalDateTime startA = this.getStartDateTime();
    LocalDateTime endA = this.getEndDateTime();

    LocalDateTime startB = other.getStartDateTime();
    LocalDateTime endB = other.getEndDateTime();

    return startA.isBefore(endB) && startB.isBefore(endA);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Event[");
    sb.append("subject=").append(subject);
    sb.append(", startDate=").append(startDate);

    if (startTime != null) {
      sb.append(", startTime=").append(startTime);
      sb.append(", endDate=").append(endDate);
      sb.append(", endTime=").append(endTime);
    } else {
      sb.append(", endDate=").append(endDate);
      sb.append(" (All-Day)");
    }

    sb.append(", visibility=").append(visibility);

    if (location != null && !location.isBlank()) {
      sb.append(", location=").append(location);
    }

    sb.append("]");
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Event other)) {
      return false;
    }
    return Objects.equals(subject, other.subject)
        && Objects.equals(startDate, other.startDate)
        && Objects.equals(startTime, other.startTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, startDate, startTime);
  }

} 
