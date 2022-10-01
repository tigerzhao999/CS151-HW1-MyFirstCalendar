public class EventTester {
    public static void main(String [] args)
    {

        String name = " new event";
        String date = "9/8/22 9:30 11:30";
        Event newEvent = Event.parseEvent(name, date, true);
        System.out.println(newEvent.toString());

        String repeatName = "repeat event";
        String repeatDate = "TR 10:30 11:45 8/23/22 12/6/22";
        Event repeatEvent = Event.parseEvent(repeatName, repeatDate, true);
        System.out.println(repeatEvent.toString());

    }
}
