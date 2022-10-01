import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * TextMenu runs the Menu in the console the user interacts with
 * Has a selected date object of type LocalDate.
 * This determines the current day of the calender
 * Java 11.0 API.
 * @author Wesley Zhao
 * @version 9/12/2022
 */

public class TextMenu {
    private String state = "MainMenu";
    private boolean intialMonthView = true;
    private LocalDate selectedDate = LocalDate.now();
    private Scanner sc = new Scanner(System.in, "UTF-8");
    private MyCal cal = new MyCal();

    /**
     * Prints Main Menu options to the console
     */
    public static void showMainMenuOptions(){
        System.out.println("");
        System.out.println("Select one of the following main menu options:");
        System.out.println("[V]iew by  [C]reate, [G]o to [E]vent list [D]elete  [Q]uit");
    }

    /**
     * Shows the main menu. Initally shows the current day in month view.
     */
    public void showMainMenu() {
        cal.loadFromText();
        this.printCalendar();
        System.out.println(" ");
        System.out.println("Loading is done!");
        String usrInput = "intital";
        String quit = "Q";
        while (!usrInput.equals(quit)) {
            showMainMenuOptions();
            usrInput = sc.nextLine();
            if (usrInput.equals("V")) {
                vMenu();
            }
            else if (usrInput.equals("G")) {
                try{
                    gMenu();
                } catch(Exception e) {
                    System.out.println("Invalid input returning to Main Menu");
                }
            }
            else if (usrInput.equals("C")) {
                try{
                    cMenu();
                } catch(Exception e) {
                    System.out.println("Invalid input or Event Conflict returning to Main Menu");
                }
            }
            else if (usrInput.equals("D")) {
                try{
                    dMenu();
                } catch(Exception e) {
                    System.out.println("Invalid input returning to Main Menu");
                }
            }
            else if (usrInput.equals("E")) {
                eMenu();
            }
            else{
                if(!usrInput.equals(quit))
                {
                    System.out.println("Invalid input returning to Main Menu");
                }
            }
        }
        cal.saveToText();
        System.out.println("Bye!");
    }

    private void gMenu() {
        System.out.println("Jump to date menu:");
        System.out.println("Enter Date in following format: MM/DD/YYYY");
        String dateString = sc.nextLine();
        DateTimeFormatter jumpFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
        LocalDate newSelectedDate = LocalDate.parse(dateString,jumpFormat);
        selectedDate = newSelectedDate;
        System.out.println("Current date is:");
        System.out.println(selectedDate.getDayOfWeek() + " " + selectedDate.getDayOfMonth() + " " + selectedDate.getMonth() + " " + selectedDate.getYear());
    }

    private void cMenu() {
        System.out.println("Enter Name of New Event");
        String name = sc.nextLine();

        System.out.println("Enter Date in following format: MM/DD/YYYY");
        String dateString = sc.nextLine();

        System.out.println("Enter Start Time: H:MM in 24-Hour Format (Military Time)");
        String startTimeText = sc.nextLine();
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
        String startDateString = dateString + " " + startTimeText;

        System.out.println("Enter End Time: H:MM in 24-Hour Format (Military Time)");
        String endTimeText = sc.nextLine();
        String endDateString = dateString + " " + endTimeText;
        //checks for valid
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try{
            startDateTime = LocalDateTime.parse(startDateString, inputFormat);
            endDateTime = LocalDateTime.parse(endDateString, inputFormat);
        }catch(Exception e){
            System.out.println("Invalid Input Returning to Main Menu");
            return;
        }

        DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
        String timeStr = startDateTime.format(parseFormat);
        timeStr = timeStr + " " + endTimeText;
        // single parse event uses string format:9/8/22 9:30 11:30
        System.out.println("Attempting to add Event...");
        cal.add(Event.parseEvent(name,timeStr,false));
        System.out.println("Event added successfully");
    }

    private void dMenu(){
        System.out.println("[S]elected  [A]ll   [DR]");
        String userInput = sc.nextLine();
        if(userInput.equals("S")){
            selectedDelete();
        }
        else if(userInput.equals("A")){
            allDelete();
        }
        else if(userInput.equals("DR")){
            recurringDelete();
        }
    }
    public void recurringDelete() {
        System.out.println("Enter the name of the event to delete:");
        String deleteName = sc.nextLine();
        if(cal.deleteRepeatByName(deleteName)){
            System.out.println("Event Removed");
        }
        else{
            System.out.println("Event Not Found");
        }

    }
    public void allDelete(){
        System.out.println("Enter the date [MM/DD/YYYY]");
        String deleteString = sc.nextLine();
        DateTimeFormatter deleteFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate deleteDate = LocalDate.parse(deleteString, deleteFormat);
        if(cal.deleteOneTimeEventsByDate(deleteDate)){
            System.out.println("Event Removed");
        }
        else{
            System.out.println("Event Not Found");
        }
    }
    private void selectedDelete(){
        System.out.println("Enter the date [MM/DD/YYYY]");
        String deleteString = sc.nextLine();
        DateTimeFormatter deleteFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate deleteDate = LocalDate.parse(deleteString, deleteFormat);
        ArrayList<Event> todaysEventsList = cal.getEventsOnGivenDay(deleteDate);
        for(Event e : todaysEventsList){
            TimeInterval ti = e.getTimeIntervals().get(0);
            String dayString = "  ";
            dayString = dayString + ti.getStartTime().toLocalTime() + " - ";
            dayString = dayString + ti.getEndTime().toLocalTime();
            dayString = dayString + " " + e.getName();
            System.out.println(dayString);
        }
        System.out.println("Enter the name of the event to delete:");
        String deleteName = sc.nextLine();
        if(cal.deleteOneTimeEventByDateName(deleteName, deleteDate)){
            System.out.println("Event Removed");
        }
        else{
            System.out.println("Event Not Found");
        }
    }
    private void eMenu() {
        ArrayList<Event> oneTimeEventsList = cal.getOneTimeEvents();
        int currentYear = 0;
        for (int i = 0; i < oneTimeEventsList.size(); i++) {
            int eventYear = oneTimeEventsList.get(i).getTimeIntervals().get(0).getStartTime().getYear();
            if(eventYear != currentYear){
                currentYear = eventYear;
                System.out.println(currentYear);
            }
            TimeInterval currentDateTime = oneTimeEventsList.get(i).getTimeIntervals().get(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E L d HH:mm");
            String result = "  " + currentDateTime.getStartTime().format(formatter);
            DateTimeFormatter formatterEndTime = DateTimeFormatter.ofPattern("HH:mm");
            result = result + " - " + currentDateTime.getEndTime().format(formatterEndTime);;
            result = result + " " + oneTimeEventsList.get(i).getName();
            System.out.println(result);
        }
        System.out.println("RECURRING EVENTS");
        ArrayList<Event> repeatEventsList = cal.getRepeatTimeEvents();
        for(Event repeatEvent : repeatEventsList){
            System.out.println(repeatEvent.getName());
            System.out.println(repeatEvent.getRecurringIntervalString());
        }
    }
    private void vMenu() {
        System.out.println("[D]ay view or [M]view ?");
        String input = sc.nextLine();
        if(input.equals("D")){
            printDay();
            return;
        }
        else if(input.equals("M")){
            intialMonthView = false;
            printCalendar();

        }
        else{
            intialMonthView = true;
            System.out.println("Unknown input returning to Main Menu");
        }

    }

    private void dayMenu(){
        //show Menu
        System.out.println(" ");
        System.out.println("[P]revious or [N]ext or [G]o back to main menu ? ");
        String input = sc.nextLine();
        if (input.equals("G"))
        {
            intialMonthView = true;
            return;
        }
        else if (input.equals("P"))
        {
            selectedDate = selectedDate.minusDays(1); // LocalDateTime is immutable
            printDay();
        }
        else if (input.equals("N"))
        {
            selectedDate = selectedDate.plusDays(1); // LocalDateTime is immutable
            printDay();
        }
        else{
            System.out.println("Invalid input returning to Main Menu");
            intialMonthView = true;
            return;

        }
    }
    private void printDay(){
        ArrayList<Event> todaysEventsList = cal.getEventsOnGivenDay(selectedDate);
        System.out.println(selectedDate.getDayOfWeek() + " " + selectedDate.getDayOfMonth() + " " + selectedDate.getMonth());
        for(Event e : todaysEventsList){
            String dayString = e.getName() + " : ";
            TimeInterval ti = e.getTimeIntervals().get(0);
            dayString = dayString + ti.getStartTime().toLocalTime().toString() + " - ";
            dayString = dayString + ti.getEndTime().toLocalTime().toString();
            System.out.println(dayString);
        }
        if(todaysEventsList.size() == 0){
            System.out.println("No Events on this day");
        }
        dayMenu();
    }
    private void monthMenu(){
        System.out.println(" ");
        System.out.println("[P]revious or [N]ext or [G]o back to main menu ? ");
        String input = sc.nextLine();
        if (input.equals("G"))
        {
            intialMonthView = true;
            return;
        }
        else if (input.equals("P"))
        {
            selectedDate = selectedDate.minusMonths(1);
            printCalendar();
        }
        else if (input.equals("N"))
        {
            selectedDate = selectedDate.plusMonths(1);
            printCalendar();
        }
        else{
            System.out.println("Invalid input returning to Main Menu");
            intialMonthView = true;
            return;

        }
    }
    private void printCalendar()
    {
        System.out.println(selectedDate.getDayOfWeek() + " " + selectedDate.getDayOfMonth() + " " + selectedDate.getMonth() + " " + selectedDate.getYear());
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d yyyy");
        LocalDate firstDayofMonth = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);

        //System.out.println("the month starts on day: ");
        //System.out.println(firstDayofMonth.getDayOfWeek().getValue());

        System.out.println("Su Mo Tu We Th Fr Sa");

        int firstDay = firstDayofMonth.getDayOfWeek().getValue();
        int daysperMonth = selectedDate.lengthOfMonth();
        firstDay = firstDay % 7;
        int j = firstDay;
        for(int i = 1; i <= daysperMonth; i++) {
            //insert padding
            String printString = highlightDate(i);
            while (j >= 1) {
                System.out.print("   ");
                j--;
            }
            //last day of the week add new line
            if ((i + firstDay) % 7 == 0) {
                if (i < 10) {
                    System.out.print(" ");
                    System.out.print(printString);
                    System.out.println("");
                } else {
                    System.out.print(printString);
                    System.out.println("");
                }
            } else {
                if (i < 10) {
                    System.out.print(" ");
                    System.out.print(printString);
                    System.out.print(" ");
                } else {
                    System.out.print(printString);
                    System.out.print(" ");
                }
            }
        }
        if(!intialMonthView){
            monthMenu();
        }
    }

    private String highlightDate(int i){
        if(intialMonthView){
            int getDayOfMonth = selectedDate.getDayOfMonth();
            if(i == getDayOfMonth){
                return "[" + i + "]";
            }
            else{
                return String.valueOf(i);
            }
        }
        LocalDate currentDay = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i);
        if(cal.dayHasEvent(currentDay)){
            return "{" + i + "}";
        }
        return String.valueOf(i);
     }
}
