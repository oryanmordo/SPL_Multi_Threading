package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import java.util.function.BooleanSupplier;
import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    Ewok ewok;

    @BeforeEach
    void setUp() {
        ewok = new Ewok(0);
    }

    @AfterEach
    void tearDown() {
        ewok = null;
    }

    @Test
    void testAcquire() throws InterruptedException {
        assertTrue(ewok.isAvailable());
        ewok.acquire();
        assertFalse(ewok.isAvailable());
    }

    @Test
    void testRelease() {
        assertFalse(ewok.isAvailable());
        ewok.release();
        assertTrue(ewok.isAvailable());
    }

    @Test
    void isAvailable() throws InterruptedException {
        assertTrue(ewok.available);
        ewok.acquire();
        assertFalse(ewok.available);
        ewok.release();
        assertTrue(ewok.available);
    }

    @Test
    void getSerialNumber() {
        assertEquals(0, ewok.getSerialNumber());
    }

}