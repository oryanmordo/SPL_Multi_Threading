package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class BombDestroyerEvent implements Event<Long> {
    private Long result;

    /**
     * default constructor for BombDestroyerEvent.
     */
    public BombDestroyerEvent() {
        result = null;
    }
}
