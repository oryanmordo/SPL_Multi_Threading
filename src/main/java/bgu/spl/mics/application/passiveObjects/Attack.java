package bgu.spl.mics.application.passiveObjects;

import java.util.List;

/**
 * Passive data-object representing an attack object.
 * You must not alter any of the given public methods of this class.
 * <p>
 * YDo not add any additional members/method to this class (except for getters).
 */
public class Attack {
    final List<Integer> serials;
    final long duration;

    /**
     * Constructor.
     */
    public Attack(List<Integer> serialNumbers, long duration) {
        this.serials = serialNumbers;
        this.duration = duration;
    }

    /* ----------- Getters ----------- */

    /**
     * getter for duration
     * @return the duration of the attack
     */
    public long getDuration() {
        return duration;
    }

    /**
     * getter for serials
     * @return the serial numbers of the Ewoks in this attack
     */
    public List<Integer> getSerials() {
        return serials;
    }
}
