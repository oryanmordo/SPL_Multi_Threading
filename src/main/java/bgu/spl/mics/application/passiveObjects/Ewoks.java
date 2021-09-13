package bgu.spl.mics.application.passiveObjects;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private static class InstanceHolder{
        private static Ewoks instance = new Ewoks();
    }

    private Vector ewoks;

    /* ----------- Constructor -----------*/

    public static Ewoks getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * This method sets the number of Ewoks that a MicroService can use for attacks.
     * @param numberOfEwoks the number of Ewoks that HansSolo and C3PO can use for attacks.
     */
    public void setEwoks(int numberOfEwoks) {
        ewoks = new Vector();
        for (int i = 0; i < numberOfEwoks; i++) {
            ewoks.add(new Ewok(i + 1)); // The serial numbers start at 1 so it is needed to add 1 to the ewok serials
        }
    }

    private Ewoks() {}

    /* ---------- Public Methods ---------- */

    /**
     * A MicroService calls this method when it needs to acquire Ewoks for an AttackEvent.
     *
     * @param ewoksToAcquire a list of the Ewok's serial numbers that the MicroService would like to acquire.
     */
    public void acquire(List<Integer> ewoksToAcquire) {
        /* no need to synchronize this because the method sorts the list */
        Collections.sort(ewoksToAcquire);
            for (Integer ewokToAcquire : ewoksToAcquire)
                /* Acquiring the ewoks. if the ewok is not available, it will wait inside the ewok's acquire method */
                ((Ewok) ewoks.get(ewokToAcquire - 1)).acquire();
    }

    /**
     * A MicroService calls this method when it is done with the Ewoks it
     * already {@link #acquire(List)} for an AttackEvent.
     *
     * @param ewoksToRelease a list of the Ewok's serial numbers that the MicroService would like to release.
     */
    public void release(List<Integer> ewoksToRelease) {
        for (Integer ewokToRelease : ewoksToRelease)
            ((Ewok) ewoks.get(ewokToRelease - 1)).release();
    }
}
