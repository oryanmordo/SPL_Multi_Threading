package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * This class is a broadcast letting every microservice who subscribed to it know that a task has been completed by another microservice.
 * In this implementation {@link bgu.spl.mics.application.services.HanSoloMicroservice} amd {@link bgu.spl.mics.application.services.C3POMicroservice} will let
 * {@link bgu.spl.mics.application.services.LeiaMicroservice} know when they completed an {@link AttackEvent}.
 */
public class CompleteMessage implements Broadcast {
    public CompleteMessage() { }
}