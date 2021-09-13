package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.CompleteMessage;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    Ewoks ewoks;

    public C3POMicroservice() {
        super("C3PO");
        ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        /* subscribing to TerminateBroadcast */
        super.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminateBroadcast) ->
        {
            diaryInstance.setC3POTerminate(this, System.currentTimeMillis());
            terminate();
        });
        /* subscribing to AttackEvent */
        super.subscribeEvent(AttackEvent.class, attackEvent ->
        {
            ewoks.acquire(attackEvent.getSerials());
            Thread.sleep(attackEvent.getDuration());
            ewoks.release(attackEvent.getSerials());
            diaryInstance.setC3POFinish(this, System.currentTimeMillis());
            diaryInstance.increaseTotalAttacks(this);
            sendBroadcast(new CompleteMessage()); //the microservice will tell leia that he finished
        });
    }
}
