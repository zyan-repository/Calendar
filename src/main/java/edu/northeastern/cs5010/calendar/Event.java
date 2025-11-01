package edu.northeastern.cs5010.calendar;

import java.time.LocalDate;

public class Event {
  private String subject;
  private LocalDate startDate;
  private LocalDate endDate;

  public Event(String subject, LocalDate startDate, LocalDate endDate) {
    this.subject = subject;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getSubject() {
    return subject;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }
  
  @Override
  public String toString() {
    return "Event [subject=" + subject + ", startDate=" + startDate + ", endDate=" + endDate + "]";
  }

} 
