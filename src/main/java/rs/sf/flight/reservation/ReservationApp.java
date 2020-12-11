package rs.sf.flight.reservation;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import rs.sf.flight.util.QueueLookup;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ReservationApp {

    private static final QueueLookup queueLookup = new QueueLookup();

    public static void main(String[] args) throws NamingException, InterruptedException {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = queueLookup.lookupQueue(initialContext, "queue/requestQueue");

        try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(); JMSContext jmsContext = factory.createContext()) {
            JMSConsumer consumer1 = jmsContext.createConsumer(requestQueue);
            JMSConsumer consumer2 = jmsContext.createConsumer(requestQueue);
            consumer1.setMessageListener(new ReservationListener());
            consumer2.setMessageListener(new ReservationListener());

            System.out.println("Reservation app running and listening...");
            while (true) ;
        }
    }
}
