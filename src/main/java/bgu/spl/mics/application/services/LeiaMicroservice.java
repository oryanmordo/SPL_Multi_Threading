package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.CompleteMessage;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private AtomicInteger completeCounter; // every time leia will get a complete event she will check if the number of completed events matches the number of attacks she sent so she will know when to send the deactivationEvent.


    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        this.completeCounter = new AtomicInteger(0);
    }

    @Override
    protected void initialize() {
        /* subscribing to TerminateBroadcast */
        super.subscribeBroadcast(TerminateBroadcast.class, terminate ->
        {
            MicroService.countDownRegister = new CountDownLatch((int) countDownRegister.getCount() + 1); //resetting the countdownlatch
            diaryInstance.setLeiaTerminate(this, System.currentTimeMillis());
            /* resetting counters for new test */
            this.completeCounter.lazySet(0); // resetting the complete attacks counter.
            terminate();
        });
        /* complete is a broadcast because we dont need it to return anything */
        super.subscribeBroadcast(CompleteMessage.class, complete ->
        {
            if (this.completeCounter.incrementAndGet() == attacks.length)
                sendEvent(new DeactivationEvent());
        });
        /* Leia will wait until all the threads will register before she will send the attack events */
        try {
            countDownRegister.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /* sending the attack events */
        for (Attack attack : attacks) {
            sendEvent(new AttackEvent(attack)); //sending the AttackEvent and saving its Future
        }
    }
}
