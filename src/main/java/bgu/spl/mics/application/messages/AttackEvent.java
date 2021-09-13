package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.List;

public class AttackEvent implements Event<Boolean> {

    private Attack attack;
    private Boolean result;

    /**
     * default constructor for this class.
     * @param attack the {@link Attack} this {@link AttackEvent}  will represent.
     */
    public AttackEvent(Attack attack) {
        this.result = false;
        this.attack = attack;
    }

    /**
     * Getter method for the Attack duration.
     *
     * @return {@link Attack#getDuration()}.
     */
    public long getDuration() {
        return attack.getDuration();
    }

    /**
     * Getter method for the list of Ewoks needed for this AttackEvent.
     *
     * @return {@link Attack#getSerials()}.
     */
    public List<Integer> getSerials() {
        return attack.getSerials();
    }

}

