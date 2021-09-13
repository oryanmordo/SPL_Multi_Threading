package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    /* those should be private but came public */
    int serialNumber;
    boolean available;

    /**
     * creates a new Ewok
     *
     * @param serialNumber the serial number from the new Ewok.
     */
    public Ewok(int serialNumber) {
        this.serialNumber = serialNumber;
        this.available = true;
    }

    //this method can only be accesses by Ewoks.
    public synchronized void acquire() {
        while (!isAvailable()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        available = false;
    }

    public synchronized void release() {
        available = true;
        this.notifyAll();
    }
    /* ---------- Getters and setters ----------*/

    public boolean isAvailable() {
        return available;
    }

    public int getSerialNumber() {
        return serialNumber;
    }
}
