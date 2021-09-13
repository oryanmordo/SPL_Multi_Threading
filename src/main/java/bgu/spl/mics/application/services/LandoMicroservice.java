package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;


/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {

    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        /* subscribing to TerminateBroadcast */
        super.subscribeBroadcast(TerminateBroadcast.class, b ->
        {
            diaryInstance.setLandoTerminate(this, System.currentTimeMillis());
            terminate();
        });
        /* subscribing to BombDestroyerEvent */
        super.subscribeEvent(BombDestroyerEvent.class, bombDestroyerEvent ->
        {
            Thread.sleep(duration);
            sendBroadcast(new TerminateBroadcast());
        });
    }
}
