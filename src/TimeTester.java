import java.time.LocalDateTime;

public class TimeTester {
    public static void testInterval()
    {
        LocalDateTime event1Start = LocalDateTime.now();
        LocalDateTime event1End = event1Start.plusHours(1);
        TimeInterval event1TI = new TimeInterval(event1Start, event1End);

        LocalDateTime event2Start = LocalDateTime.now().minusMinutes(61);
        LocalDateTime event2End = event2Start.plusHours(1);
        TimeInterval event2TI = new TimeInterval(event2Start, event2End);

        System.out.println(event1TI.isOverlap(event2TI));
    }
}
