package edu.northeastern.cs5010.calendar;

import java.util.ArrayList;
import java.util.List;

public class Calendar {

  private final String title;
  private List<Event> events = new ArrayList<>();  

  public Calendar(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void addEvent(Event event) {
    events.add(event);
  }

  public void removeEvent(Event event) {
    events.remove(event);
  }

  public List<Event> getEvents() {
    return new ArrayList<>(events);
  }

  @Override
  public String toString() {
    return "Calendar [title=" + title + ", events=" + events + "]";
  }
  
}
