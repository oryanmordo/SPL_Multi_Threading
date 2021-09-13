package bgu.spl.mics;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class MessageBusImpl implements MessageBus {

    private HashMap<Class<? extends Message>, LinkedList<MicroService>> roundRobinQue; // Every message.type holds a list of the microservices that are subscribed to it.
    private ConcurrentHashMap<MicroService, LinkedList<Message>> queOfMessages; // Que of messages for each microservice
    private HashMap<Message, Future> messagesToFollow; // every message and its future object to follow
    private Object EventLock; // lock for sendEvent.

    /**
     * holder class for {@code MessageBusImpl} for a thread safe singleton.
     */
    private static class InstanceHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    /**
     * {@code MessageBusImpl} private and default constructor to maintain it a thread safe singleton
     */
    private MessageBusImpl() {
        roundRobinQue = new HashMap<>();
        queOfMessages = new ConcurrentHashMap<>();
        messagesToFollow = new HashMap<>();
        EventLock = new Object();
    }

    /**
     * this method will return an instance of {@link InstanceHolder#instance} from the private class {@link InstanceHolder}.
     *
     * @return the single instance of {@code MessageBusImpl} class.
     */
    public static MessageBusImpl getInstance() {
        return MessageBusImpl.InstanceHolder.instance;
    }

    /* ---------- Methods ---------- */

    /**
     * A Microservice calls this method to subscribe itself for some type
     * of event (the specific class type of the event is passed as a parameter).
     *
     * @param type The type of {@link Event} {@code m} want's to to subscribe to.
     * @param m    The subscribing micro-service.
     * @param <T>  The Class type of the message object.
     */
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        subscribeMessage(type, m);
    }

    /**
     * A Microservice calls this method to subscribe itself for some
     * type of broadcast message (The specific class type of the broadcast is passed as
     * a parameter).
     *
     * @param type The type of {@link Broadcast} {@code m} want's to to subscribe to.
     * @param m    The subscribing micro-service.
     */
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        subscribeMessage(type, m);
    }


    /**
     * A Microservice calls this method to notify the
     * Message-Bus that the event was handled. The Microservice provides the result
     * of handling the request. The Future object associated with event e should be
     * resolved to the result given as a parameter.
     *
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     * @param <T>    The {@code Future<T>} Event that is removed from  {@code messagesToFollow}.
     */
    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = messagesToFollow.get(e); // getting the future object associated with this event.
        if (future != null) { // a simple check.
            future.resolve(result); // the method will resolve the future object with the result provided as input.
            messagesToFollow.remove(e);// after the future object has been resolved we dont need to keep it.
        }
    }

    /**
     * A Microservice calls this method to add a broadcast message to
     * the queues of all the Microservices that are interested in it.
     *
     * @param b The message to be added to the queues.
     */
    public void sendBroadcast(Broadcast b) {
        LinkedList<MicroService> MSList = roundRobinQue.get(b.getClass());
        if (shouldReturnNull(b)) // notice that in case of broadcast will will just exit the method.
            return;
        synchronized (MSList) { // synchronizing the list of microservices that are subscribed to b.type broadcast
            LinkedList<Message> messagesQue;
            for (MicroService m : MSList) { //for every microservice in the list mentioned above
                messagesQue = queOfMessages.get(m); // we will get the messagesQue of that microservice and will synchronized it
                synchronized (messagesQue) {
                    /* adding the broadcast to the messageQue and notifying the microservice that is waiting in waitMessage */
                    messagesQue.add(b);
                    messagesQue.notifyAll();
                }
            }
        }

    }

    /**
     * A Microservice calls this method to add event {@code e}
     * to the messages queue of one of the Microservices that are subscribed to receive
     * events of type {@link Event#getClass()}. The messages are added in a round-robin manner.
     * This method returns a Future object - from which the sending Microservice can
     * retrieve the result of processing the event once it is completed.
     *
     * @param e The event to add to the queue.
     * @return {@link Future} object - from which the sending Microservice can retrieve the result of processing
     */
    public <T> Future<T> sendEvent(Event<T> e) {
        synchronized (EventLock) {
            if (shouldReturnNull(e)) // a condition to check the if the method should return null.
                return null;
            /* the method will create a new future object and will put it in the messagesToFollow hashMap */
            Future<T> future = new Future<>();
            messagesToFollow.put(e, future);
            MicroService m;
            synchronized (roundRobinQue) {
                LinkedList<MicroService> MSList = roundRobinQue.get(e.getClass()); // getting the current que of the round robin of microservices
                synchronized (MSList) { // for round robin safety
                    /* removing the first microservice from the round robin que */
                    m = MSList.removeFirst();
                    MSList.addLast(m);
                }
            }
            LinkedList<Message> messagesQue = queOfMessages.get(m);
            /* now we are inserting the event to the microservice that is needed */
            synchronized (messagesQue) {
                messagesQue.add(e);
                messagesQue.notifyAll();
            }
            return future; // returning the future object of that event.
        }
    }


    /**
     * A Microservice calls this method to register itself. This method
     * creates a {@link java.util.Queue} implemented by {@link LinkedList} for the Microservice in the MessageBus.
     */
    @Override
    public void register(MicroService m) {
        queOfMessages.put(m, new LinkedList<>());
    }


    /**
     * A MicroService calls this method in order to unregister itself from the
     * MessageBus. The MessageBus will remove the {@link java.util.Queue} implemented by {@link LinkedList}
     * allocated to the {@link MicroService}, and clean all the references related to it.
     */
    @Override
    public void unregister(MicroService m) {
        /* remove the microservice from roundRobinQue */
        Collection<LinkedList<MicroService>> queues = roundRobinQue.values(); // making a collection of all message types currently in the round robin
        for (LinkedList<MicroService> q : queues) {
            synchronized (q) {
                q.remove(m); // the method will remove m from every que of message that was found earlier.
            }
        }
        queOfMessages.remove(m); // finally the method will remove
    }

    /**
     * A Microservice calls this method to fetch a from its queue.
     * This method is blocking!
     *
     * @param m The {@link MicroService} requesting to take a {@link Message} from it's {@link #queOfMessages}.
     * @return the first message in the MicroService's queOfMessages.
     * @throws {@link InterruptedException} - if there is a Queue of messages for the requesting MicroService.
     */
    public Message awaitMessage(MicroService m) {
        if (!queOfMessages.containsKey(m)) // checking if there is a Queue of messages for m. if not will throw IllegalStateException.
            throw new IllegalStateException();
        LinkedList<Message> msgQue = queOfMessages.get(m);

        synchronized (msgQue) { //the microservice will wait (blocking) for a message.
            while (msgQue.isEmpty()) {
                try {
                    msgQue.wait(); // the MicroService will wait until another Microservice will notify it by notifying the queOfMessages for this current MicroService.
                } catch (InterruptedException e) {
                }
            }
            return msgQue.poll(); // will return the message that the method polled from the MicroService's queOfMessages.
        }
    }


    /* ---------- private methods --------- */

    /**
     * We found that there is no need to write the same method twice as we treat
     * {@link Event} and {@link Broadcast} the same way because they are marked by {@link Message} marker.
     *
     * @param type The type of {@link Event} {@code m} want's to to subscribe to.
     * @param m    The subscribing micro-service.
     */
    private void subscribeMessage(Class<? extends Message> type, MicroService m) {
        /* here the method will check if it is the first time that this type of message was subscribed to. if so, the method
         * will create a new que of microservices for this message. */
        if (!roundRobinQue.containsKey(type))
            synchronized (roundRobinQue) { // as we change the RoundRobinQue we want to ensure no thread changes it.
                createNewQueue(type);
            }

        if (!roundRobinQue.get(type).contains(m)) //checking if the same microservice tries to subscribe to the same type of message more then once
            /* the method will add m to the que of type in roundRobinQue*/
            synchronized (roundRobinQue.get(type)) {
                addMicroServiceToMessageList(type, m);
            }
    }

    /**
     * this method helps
     * {@link #sendEvent(Event)} and {@link #sendBroadcast(Broadcast)} determine if they should send the event or not
     *
     * @param message The message the current {@link MicroService} want's to send.
     * @return {@code true} if message should not be sent.
     */
    private boolean shouldReturnNull(Message message) {
        if (!roundRobinQue.containsKey(message.getClass()) // if there is no list of microservices that are subscribed to this message
                || roundRobinQue.get(message.getClass()).isEmpty()) // if the list of microservices that are subscribed to this message is empty.
            return true;
        return false;
    }

    private void createNewQueue(Class<? extends Message> type) {
        roundRobinQue.put(type, new LinkedList<>());
    }

    private void addMicroServiceToMessageList(Class<? extends Message> type, MicroService m) {
        roundRobinQue.get(type).addLast(m);
    }

}
