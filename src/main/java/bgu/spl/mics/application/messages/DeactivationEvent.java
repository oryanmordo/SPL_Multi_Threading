package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 * This class is a message being sent by {@link bgu.spl.mics.application.services.LeiaMicroservice} to {@link bgu.spl.mics.application.services.R2D2Microservice}
 * when all of the {@link AttackEvent} were completed.
 */
public class DeactivationEvent implements Event<Long> {

    /**
     * default constructor for DeactivationEvent.
     */
    public DeactivationEvent() {
    }

}
