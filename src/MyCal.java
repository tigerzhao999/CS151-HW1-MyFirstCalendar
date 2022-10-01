import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class MyCal holds all Event objects in array list
 * Class has method to add
 * Java 11.0 API.
 * @version 9/12/2022
 * @author Wesley Zhao
 */
public class MyCal {
    private ArrayList<Event> eventArrayList = new ArrayList<>();

    //public accessor for eventArrayList;
    /**
     * Method to add Event Must not conflict with existing event
     * @param e Event to be added
     * @throws EventConflictException
     */
    //public mutator for eventArrayList;
    public void add(Event e) throws EventConflictException{
        Event conflict = hasConflict(e);
        if(conflict != null){
            throw new EventConflictException(conflict);
        }
        else{
            eventArrayList.add(e);
        }
    }

    /**
     * Determines whether an event has conflict with any existing events
     * Used by MyCal.add during adding
     * @param e Event to be Checked
     * @return Returns event if there is a conflict. Returns null if there is no conflict.
     */
    private Event hasConflict(Event e){
        for(Event event : eventArrayList){
            if(event.hasConflict(e)){
                return event;
            }
        }
        return null;
    }

    /**
     * Loads file from preset location: src/events.txt
     * Supports two formats
     * One-Time-Event format:
     * Name: "Some String" (Example: Interview at BigCorp)
     * Date: "M/D/YY H:mm H:mm" (Example: 9/28/22 9:30 11:30)
     * One-Time-Event format:
     * Name: "Some String" (Example: Interview at BigCorp)
     * Date: "M/D/YY H:mm H:mm" (Example: 9/28/22 9:30 11:30)
     */
    public void loadFromText() {
        try{
            BufferedReader readFromFile = new BufferedReader(new FileReader("./events.txt"));
            String name = readFromFile.readLine();
            String timeString = readFromFile.readLine();
            while(name != null){
                Event newEvent = Event.parseEvent(name, timeString, true);
                add(newEvent);
                name = readFromFile.readLine();
                timeString = readFromFile.readLine();
            }
            readFromFile.close();
        } catch(IOException e){
            System.out.println("File read error.");
        }
    }

    /**
     * Outputs all events to preset location: src/events.txt
     * Will output all events in the same format as the input text file
     */
    public void saveToText(){
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File("./output.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);

            for(Event e : eventArrayList){
                bufferedWriter.write(e.getName());
                bufferedWriter.newLine();
                if(e.isRecurring()){
                    bufferedWriter.write(e.getRecurringIntervalString());
                    bufferedWriter.newLine();
                }
                else{
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
                    String singleDate = e.getTimeIntervals().get(0).getStartTime().format(dateFormat);
                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
                    singleDate = singleDate + " " + e.getTimeIntervals().get(0).getStartTime().format(timeFormat);
                    singleDate = singleDate + " " + e.getTimeIntervals().get(0).getEndTime().format(timeFormat);
                    bufferedWriter.write(singleDate);
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally
        {
            try{
                if(bufferedWriter!=null)
                    bufferedWriter.close();
            }catch(Exception ex){
                System.out.println("Error in closing the BufferedWriter"+ex);
            }
        }
    }

    /**
     * Accessor for all Events
     * @return Arraylist with all currently created events
     */
    public ArrayList<Event> getAllTimeEvents(){
        return eventArrayList;
    }

    /**
     * Accessor for all events which do not reoccur
     * @return Returns a Arraylist with all currently created events which do not repeat
     */
    public ArrayList<Event> getOneTimeEvents(){
        ArrayList<Event> result = new ArrayList<>();
        for(Event event: eventArrayList){
            if(!event.isRecurring()){
                result.add(event);
            }
        }
        Collections.sort(result);
        return result;
    }
    /**
     * Accessor for all events which reoccur
     * @return Returns a Arraylist with all currently created events which repeat
     */
    public ArrayList<Event> getRepeatTimeEvents(){
        ArrayList<Event> result = new ArrayList<>();
        for(Event event: eventArrayList){
            if(event.isRecurring()){
                result.add(event);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Used to determine if there are events on a given date
     * Checks both one-time and reoccuring events
     * @param day Day to determine if there are events on
     * @return True if there are events that occur on this day. False if there are no events that occur.
     */
    public boolean dayHasEvent(LocalDate day){
        for(Event event : eventArrayList){
            for(TimeInterval ti : event.getTimeIntervals()){
                LocalDate startTime = ti.getStartTime().toLocalDate();
                if(day.equals(startTime)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Used to get all events matching the supplied date
     * @param selectedDay The day for which events occur on to be returned
     * @return Returns an Arraylist of events matching the date of the given date
     */
    public ArrayList<Event> getEventsOnGivenDay(LocalDate selectedDay){
        ArrayList<Event> todaysEventsList = new ArrayList<>();
        //Check every timeinterval in every event to see if it starts on selected date
        for(Event e : getAllTimeEvents()){
            for(TimeInterval ti : e.getTimeIntervals()){
                if(ti.getStartTime().toLocalDate().equals(selectedDay)){
                    if(!e.isRecurring()){
                        todaysEventsList.add(e);
                    }
                    else{
                        todaysEventsList.add(new Event(e.getName(),ti));
                    }
                }
            }
        }
        Collections.sort(todaysEventsList);
        return todaysEventsList;
    }

    /**
     * Used to delete one-time Event on supplied date by name
     * Will only delete the event which matches the Date and the name.
     * @param name The name of the event to be deleted
     * @param date The date of the event to be deleted
     * @return True if the event was found. False if the event was not found.
     * Precondition: The event must exist
     * Postcondition: The event will be removed from the eventArrayList
     */
    public boolean deleteOneTimeEventByDateName(String name, LocalDate date){
        return eventArrayList.removeIf(e -> (e.getName().equals(name) &&
                e.getTimeIntervals().get(0).getStartTime().toLocalDate().equals(date) &&
                !e.isRecurring()));
    }

    /**
     * Will delete all the one-time Events on a given date
     * @param date The date to delete all the one time events from
     * @return True if there were events deleted. False if events were not deleted.
     */
    public boolean deleteOneTimeEventsByDate(LocalDate date){
        return eventArrayList.removeIf(e -> (e.getTimeIntervals().get(0).getStartTime().toLocalDate().equals(date) &&
                !e.isRecurring()));
    }

    /**
     * Will delete repeating Events with name matching the input string
     * @param name Name of event to delete.
     * @return True if there were events deleted. False if events were not deleted.
     */
    public boolean deleteRepeatByName(String name){
        return eventArrayList.removeIf(e -> (e.getName().equals(name) && e.isRecurring()));
    }
}
