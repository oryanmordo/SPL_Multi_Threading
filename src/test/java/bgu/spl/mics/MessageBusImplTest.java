package bgu.spl.mics;

//import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl messageBusTester;

    String[] eventString;
    String[] eventStringReceiver;
    String[] broadcastString;
    ExampleEvent event;
    ExampleBroadcast broadcast;
    /* message sender and receiver*/
    ExampleMessageSenderService someMessageSender; // Some message sender
    ExampleEventHandlerService someEventReceiver; // Some event receiver
    /* for broadcast testing we need two receivers */
    ExampleBroadcastListenerService someBroadcastReceiver1;
    ExampleBroadcastListenerService someBroadcastReceiver2;

    @BeforeEach
    void setUp() {
        messageBusTester = MessageBusImpl.getInstance();
        eventString = new String[]{"event"};
        eventStringReceiver = new String[]{"1"};
        broadcastString = new String[]{"broadcast"};
        event = new ExampleEvent("someSender");
        broadcast = new ExampleBroadcast("someSender");
        /* we dont need to the the register function because if it doesnt work every test wont work*/
        someMessageSender = new ExampleMessageSenderService("someSender", eventString);
        someEventReceiver = new ExampleEventHandlerService("someReceiver", eventStringReceiver);
        messageBusTester.register(someMessageSender);
        messageBusTester.register(someEventReceiver);
        messageBusTester.register(someBroadcastReceiver1);
        messageBusTester.register(someBroadcastReceiver2);
    }

    @AfterEach
    void tearDown() {}

    /* NO NEED TO TEST */
    @Test
    void testSubscribeEvent() {
    }

    /* NO NEED TO TEST*/
    @Test
    void testSubscribeBroadcast() {
    }


    /* NEED TO TEST */
    @Test
    void testComplete() throws InterruptedException {
        someMessageSender = new ExampleMessageSenderService("someSender", eventString);
        someEventReceiver = new ExampleEventHandlerService("someReceiver", eventString);
        messageBusTester.subscribeEvent(event.getClass(),someEventReceiver);
        Future<String> future = someMessageSender.sendEvent(event);
        messageBusTester.complete(event, "result");
        assert future != null;
        assertEquals("result", future.get());
        assertTrue(future.isDone());
    }

    @Test
    void testSendBroadcast() throws InterruptedException {
        /* ill create 2 microservices that will subscribe to this broadcast */
        someBroadcastReceiver1 = new ExampleBroadcastListenerService("someReceiver1", broadcastString);
        messageBusTester.subscribeBroadcast(broadcast.getClass(), someBroadcastReceiver1);
        someBroadcastReceiver2 = new ExampleBroadcastListenerService("someReceiver2", broadcastString);
        messageBusTester.subscribeBroadcast(broadcast.getClass(), someBroadcastReceiver2);
        /* creating a sender to the broadcast */
        someMessageSender = new ExampleMessageSenderService("someSender", broadcastString);
        /* sending the broadcast*/
        someMessageSender.sendBroadcast(broadcast);
        /* making sure that the two receivers got the broadcast */
        assertEquals(broadcast, messageBusTester.awaitMessage(someBroadcastReceiver1)); //blocking!!!
        assertEquals(broadcast, messageBusTester.awaitMessage(someBroadcastReceiver2)); //blocking!!!

    }

    @Test
    void testSendEvent() throws InterruptedException {
        /* setup: creating a microservices that will send and receive the event */

        /* subscribing the microservice to the event that was just created */
        messageBusTester.subscribeEvent(event.getClass(),someEventReceiver);
        /* sending the event */
        someMessageSender.sendEvent(event);
        /* we are asserting that the message that is first in the que is the message we sent */
        assertEquals(event, messageBusTester.awaitMessage(someEventReceiver));
    }

    /* NO NEED TO TEST - WAS TOLD IN OFFICE HOURS*/
    @Test
    void testRegister() {


    }

    /* NO NEED TO TEST - WAS TOLD IN THE FORUM*/
    @Test
    void testUnregister() {
    }

    /* TEST WITH THE ASSUMTION THAT THE QUE IS EMPTY */
    @Test
    void testAwaitMessage() throws InterruptedException {
        /* setup: creating a microservices that will send and receive the event */
        someMessageSender = new ExampleMessageSenderService("someSender", eventString);
        someEventReceiver = new ExampleEventHandlerService("someReceiver", eventString);
        /* subscribing the microservice to the event that was just created */
        messageBusTester.subscribeEvent(event.getClass(),someEventReceiver);
        /* sending the event */
        someMessageSender.sendEvent(event);
        /* we are asserting that the message that is first in the que is the message we sent */
        assertEquals(event, messageBusTester.awaitMessage(someEventReceiver));
    }
}