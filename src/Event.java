import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

/**
 * Class event
 * Has a name and one or more time intervals
 * If reccuring isReccuring is true and timeIntervals will hold more than one time interval
 * Users cannot create recurring Events
 * Can parse single and recurring events from text
 * Users cannot create recurring Events
 * Can parse single event from user
 * @author Wesley Zhao
 * @version 9/12/2022
 */
public class Event implements Comparable<Event> {
    private String name;
    private ArrayList<TimeInterval> timeIntervals = new ArrayList<>();
    private boolean isRecurring;
    //copied from text document
    private String recurringIntervalString;

    /**
     * Public constructor for Event
     *
     */
    public Event(){
        isRecurring = false;
    }

    /**
     * used to create tempoary event for day view
     * @param newName
     * @param ti
     */
    public Event(String newName, TimeInterval ti){
        name = newName;
        timeIntervals.add(ti);
        isRecurring = false;
    }

    /**
     * Determines wheather Events overlap
     * If Events overlap/conflict it will return true
     * If Events dont overlap it will return false
     * Checks all time intervals in both events
     * @param e the time interval to be checked
     * @return boolean true if they overlap/have conflict. False if they are not overlapping/conflicting.
     */
    public boolean hasConflict(Event e){
        for(TimeInterval thisTimeInterval: timeIntervals){
            for(TimeInterval eTimeInterval : e.timeIntervals){
                if(thisTimeInterval.isOverlap(eTimeInterval)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Parses event from string and creates a new event with correspoinding time intervals and name
     * Can parse single event or reoccuring events
     * Internally parses a single event
     * If it detects the event is reoccuring it will create the time intervals via parseRepeatEvent
     * @param name Name of the new event
     * @param timeStr Time String describing the time of the event.
     *                Repeat events are listed in the following format: "TR 10:30 11:45 8/23/22 12/6/22"
     *                Non-Repeating events TimeString are in the following format: "9/8/22 9:30 11:30"
     * @param fromFile tells parseEvent whether reading from a file.
     *                 True when reading from a file False when reading from user input
     * @return Returns a new event with Name and TimeInterval derived from the string.
     * Precondition: timeStr MUST be in format of "TR 10:30 11:45 8/23/22 12/6/22" for reoccuring events or "9/8/22 9:30 11:30" for one time events
     * Postcondition: Event object will be returned with correct name and TimeIntervals
     */
    public static Event parseEvent(String name, String timeStr,boolean fromFile){
        Event returnEvent = new Event();
        returnEvent.name = name;
        //if the first character of time string is a number it is not reoccuring
        if(Character.isDigit(timeStr.charAt(0))){
            returnEvent.timeIntervals.add(TimeInterval.parseTimeInterval(timeStr, fromFile));
        }
        //parse repeat event
        else{
            returnEvent.isRecurring = true;
            returnEvent.recurringIntervalString = timeStr;
            parseRepeatEvent(timeStr, returnEvent);
        }
        return returnEvent;
    }

    /**
     * Private method used by parseEvent to parse the repeat events from a file (repeat events cannot be added by users)
     *
     * @param timeStr Time String that determines the
     * @param repeatEvent new Event passed by
     */

    private static void parseRepeatEvent(String timeStr, Event repeatEvent) {
        //F 18:30 20:30  9/20/22 12/9/22 ** Example 1
        //TR 10:30 11:45 8/23/22 12/6/22 ** Example 2
        String[] split = timeStr.split("\\s+");
        //runs for every character in the repeat event. For Example2 runs twice for Tuesday and Thursday
        for(Character c : split[0].toCharArray()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy H:mm");

            String startDateString = split[3] + " " + split[1];
            LocalDateTime startTime = LocalDateTime.parse(startDateString, formatter);
            startTime = startTime.with(TemporalAdjusters.nextOrSame(dayOfWeekFromChar(c)));

            String endDateString = split[3] + " " + split[2];
            LocalDateTime endTime = LocalDateTime.parse(endDateString, formatter);
            endTime = endTime.with(TemporalAdjusters.nextOrSame(dayOfWeekFromChar(c)));

            String finalDateString = split[4] + " " + split[2];
            LocalDateTime finalTime = LocalDateTime.parse(finalDateString, formatter);

            while(!endTime.isAfter(finalTime)){
                repeatEvent.timeIntervals.add(new TimeInterval(startTime, endTime));
                startTime = startTime.plusWeeks(1);
                endTime = endTime.plusWeeks(1);
            }
        }

    }

    private static DayOfWeek dayOfWeekFromChar(Character c){
        DayOfWeek returnDay;
        switch(c){
            case 'M':  return DayOfWeek.MONDAY;
            case 'T':  return DayOfWeek.TUESDAY;
            case 'W':  return DayOfWeek.WEDNESDAY;
            case 'R':  return DayOfWeek.THURSDAY;
            case 'F':  return DayOfWeek.FRIDAY;
            case 'A':  return DayOfWeek.SATURDAY;
            case 'S':  return DayOfWeek.SUNDAY;
        }
        throw new IllegalArgumentException("Not a day of the week :" + c);
    }

    /**
     * Accessor for isRecurring
     * @return True if event is recurring and False if event is a one time event
     */
    public boolean isRecurring(){
        return isRecurring;
    }

    /**
     * Accessor for timeIntervals
     * @return Returns an ArrayList of TimeIntervals contaning all the time intervals which the event takes place
     */
    public ArrayList<TimeInterval> getTimeIntervals(){
        return timeIntervals;
    }

    /**
     * Accessor for the name
     * @return Returns the name of the Event
     */
    public String getName(){
        return name;
    }

    /**
     * Returns recurringIntervalString for this Event
     * For recurring events this is the timeString which was parsed to create all repeat events
     * @return Returns the recurring interval string
     * Precondition: Only use on recurring events to get valid string. When used on a one-time Event it will return an empty string
     * Postcondition: Returns the recurring interval string
     */
    public String getRecurringIntervalString(){
        return recurringIntervalString;
    }

    /**
     * Only used for testing
     * @return Returns String contaning the name and the first time interval
     */
    public String toString(){
        String rtString = name;
        rtString += "\n";
        if(isRecurring){
            rtString += recurringIntervalString;
        }
        else{
            rtString = rtString + timeIntervals.get(0).toString();
        }
        return rtString;
    }

    /**
     * compare the first start time of every event
     * used to sort events in a list
     * @param o the Event to be compared.
     * @return -1 if this event occurs before Event o. 1 If this event occurs after Event o. Will not return 0 because Events cannot occur at the same time
     */
    public int compareTo(Event o) {
        return timeIntervals.get(0).compareTo(o.timeIntervals.get(0));
    }
}
