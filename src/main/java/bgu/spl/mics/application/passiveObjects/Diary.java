package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    /*                           ---------- private fields -----------
     ---------- those fields need to be initialize by setters while the program is running  ------------ */


    private AtomicInteger totalAttacks; // can only be initialize by HanSolo and C3PO

    /* Finish time stamps*/

    private Long HanSoloFinish; //can only be initialize by HanSolo
    private Long C3POFinish; //can only be initialize by C3PO
    private Long R2D2Deactivate; //can only be initialize by R2D2

    /* Terminate time stamps */

    private Long LeiaTerminate; //can only be initialize by Leia
    private Long HanSoloTerminate; //can only be initialize by HanSolo
    private Long C3POTerminate; //can only be initialize by C3PO
    private Long R2D2Terminate; //can only be initialize by R2D2
    private Long LandoTerminate; //can only be initialize by Lando

    /* ----------- Constructor and GetInstance ----------- */

    private static class InstanceHolder {
        private static Diary instance = new Diary();
    }

    public static Diary getInstance() {
        return InstanceHolder.instance;
    }

    private Diary() {
        totalAttacks = new AtomicInteger(0);
    }

    /* ---------- Setters ----------- */

    /* this is an atomic operation so it will be thread safe */
    public final void increaseTotalAttacks(MicroService requestingMS) {
        if (requestingMS.getClass().equals(HanSoloMicroservice.class) || requestingMS.getClass().equals(C3POMicroservice.class))
            totalAttacks.incrementAndGet();
    }

    public final void setHanSoloFinish(MicroService hans, Long HanSoloFinish) {
        if (hans.getClass().equals(HanSoloMicroservice.class))
            this.HanSoloFinish = HanSoloFinish;
    }

    public final void setC3POFinish(MicroService c3po, Long C3POFinish) {
        if (c3po.getClass().equals(C3POMicroservice.class))
            this.C3POFinish = C3POFinish;
    }

    public final void setR2D2Deactivate(MicroService r2d2, Long R2D2Deactivate) {
        if (r2d2.getClass().equals(R2D2Microservice.class))
            this.R2D2Deactivate = R2D2Deactivate;
    }

    public final void setLeiaTerminate(MicroService leia, Long LeiaTerminate) {
        if (leia.getClass().equals(LeiaMicroservice.class))
            this.LeiaTerminate = LeiaTerminate;
    }

    public final void setHanSoloTerminate(MicroService hans, Long HanSoloTerminate) {
        if (hans.getClass().equals(HanSoloMicroservice.class))
            this.HanSoloTerminate = HanSoloTerminate;
    }

    public final void setC3POTerminate(MicroService c3po, Long C3POTerminate) {
        if (c3po.getClass().equals(C3POMicroservice.class))
            this.C3POTerminate = C3POTerminate;
    }

    public final void setR2D2Terminate(MicroService r2d2, Long R2D2Terminate) {
        if (r2d2.getClass().equals(R2D2Microservice.class))
            this.R2D2Terminate = R2D2Terminate;
    }

    public final void setLandoTerminate(MicroService lando, Long LandoTerminate) {
        if (lando.getClass().equals(LandoMicroservice.class))
            this.LandoTerminate = LandoTerminate;
    }

    /* those getters are mostly for testing */
    public AtomicInteger getNumberOfAttacks() {
        return totalAttacks;
    }

    public Long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public Long getC3POFinish() {
        return C3POFinish;
    }

    public Long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public Long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public Long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public Long getC3POTerminate() {
        return C3POTerminate;
    }

    public Long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public Long getLandoTerminate() {
        return LandoTerminate;
    }

    /* for testing */
    public void resetNumberAttacks() {
        totalAttacks.lazySet(0);
    }

}
