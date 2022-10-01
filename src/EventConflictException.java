/**
 * This Exception is thrown when two event overlap
 *
 */

public class EventConflictException extends RuntimeException{
    private Event conflictEvent;
    public EventConflictException(Event e){
        conflictEvent = e;
    }

    public Event getConflictEvent(){
        return conflictEvent;
    }
}
