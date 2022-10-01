import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Time interval class uses LocalDateTime
 * Holds a startTime and endTime
 * has methods to determine whether a time interval overlaps or not
 * Can handle parsing a time interval from a string
 * Java 11.0 API.
 * @version 9/12/2022
 * @author Wesley Zhao
 */
public class TimeInterval implements Comparable<TimeInterval> {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeInterval(LocalDateTime newStartTime, LocalDateTime newEndTime){
        startTime = newStartTime;
        endTime = newEndTime;
    }

    @Override
    public int compareTo(TimeInterval o) {
        return startTime.compareTo(o.startTime);
    }
    /**
     * isOverlap detemines whether two TimeInterval objects overlap
     * compares their start times and end times.
     * @param o The time interval to be compared to.
     * @return returns True if they conflict. Returns False if NO conflict
     */
    public boolean isOverlap(TimeInterval o){
        if(!startTime.isBefore(o.getEndTime())){
            return false;
        }
        if(!endTime.isAfter(o.getStartTime())){
            return false;
        }
        return true;
    }

    /**
     * parseTimeInterval creates a time interval supports creating time interval via both time formats
     * @param tiString Time string to be parsed into interval must be "M/d/yy H:mm" or "M/d/yyyy H:mm" format
     * @param fromFile Determines which format to be parsed
     *        Uses "M/d/yyyy H:mm H:mm" When parsing from user
     *        Uses "M/d/yy H:mm H:mm" when parsing from a file
     * @return TimeInterval correspoindong to input string.
     */
    public static TimeInterval parseTimeInterval(String tiString, boolean fromFile){
        String[] split = tiString.split("\\s+");
        String startString = split[0] + " " + split[1];
        String endString = split[0] + " " + split[2];
        DateTimeFormatter formatter;
        if(fromFile){
            formatter = DateTimeFormatter.ofPattern("M/d/yy H:mm");
        }
        else{
            formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
        }
        return new TimeInterval(LocalDateTime.parse(startString, formatter),LocalDateTime.parse(endString, formatter));
    }

    /**
     * Returns the start time
     * @return The start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }
    /**
     * Returns the end time
     * @return The end time
     */
    public LocalDateTime getEndTime(){
        return endTime;
    }
    /**
     * Returns a String with start time and end time *Only used for testing
     * @return A string with start time and end time
     */
    public String toString(){
        String rtString = startTime.toString();
        rtString = rtString + " " + endTime.toString();
        return rtString;
    }
}
