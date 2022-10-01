public class CalTester
{
    public static void main(String [] args)
    {
        Event newEvent = Event.parseEvent(" new event", "9/8/22 9:30 11:30", true);
        Event secondEvent = Event.parseEvent(" second event", "9/9/22 9:30 11:30", true);
        Event thirdEvent = Event.parseEvent("third event", "9/8/22 9:30 11:30", true);
        MyCal firstCal = new MyCal();
        firstCal.add(newEvent);
        firstCal.add(secondEvent);
        firstCal.add(thirdEvent);
    }
}
