package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        /* subscribing to TerminateBroadcast */
        super.subscribeBroadcast(TerminateBroadcast.class, b ->
        {
            diaryInstance.setR2D2Terminate(this, System.currentTimeMillis());
            terminate();
        });
        /* subscribing to DeactivationEvent */
        super.subscribeEvent(DeactivationEvent.class, deactivationEvent ->
        {
            Thread.sleep(duration); // waiting the time needed to deactivate the shields
            diaryInstance.setR2D2Deactivate(this, System.currentTimeMillis());
            sendEvent(new BombDestroyerEvent()); //it was written in the forum that we can ignore the instruction that r2d2 cant send messages.
        });
    }
}
