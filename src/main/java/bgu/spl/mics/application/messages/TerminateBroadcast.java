package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * This class is a message being sent by {@link bgu.spl.mics.application.services.R2D2Microservice} to {@link bgu.spl.mics.application.services.LandoMicroservice}
 * when he completed the {@link DeactivationEvent}.
 */
public class TerminateBroadcast implements Broadcast {

    /**
     * default constructor for TerminateBroadcast.
     */
    public TerminateBroadcast() {
    }

}
