package rs.sf.flight.checkin;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import rs.sf.flight.domain.Passenger;
import rs.sf.flight.util.QueueLookup;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CheckInApp {

    private static final QueueLookup queueLookup = new QueueLookup();

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = queueLookup.lookupQueue(initialContext, "queue/requestQueue");
        Queue replyQueue = queueLookup.lookupQueue(initialContext, "queue/replyQueue");

        try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(); JMSContext jmsContext = factory.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
            for (Passenger p : getPassengers()) {
                ObjectMessage objectMessage1 = jmsContext.createObjectMessage();
                objectMessage1.setObject(p);
                producer.send(requestQueue, objectMessage1);
            }
            JMSConsumer consumer = jmsContext.createConsumer(replyQueue);
            consume(consumer);
        }
    }

    private static void consume(JMSConsumer consumer) throws JMSException {
        Message message;
        while ((message = consumer.receive(5000)) != null) {
            if (message instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message;
                System.out.println("checked in: " + mapMessage.getString("checked in"));
            } else {
                throw new RuntimeException("message is not an instance of javax.jms.MapMessage");
            }
        }
        System.out.println("Received no message after 5 sec wait. Terminating app...");
    }

    private static Passenger[] getPassengers() {
        Passenger p1 = new Passenger();
        p1.setId(1L);
        p1.setEmail("john.doe@gmail.com");
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setPhone("123456");

        Passenger p2 = new Passenger();
        p2.setId(2L);
        p2.setEmail("jane.doe@gmail.com");
        p2.setFirstName("Jane");
        p2.setLastName("Doe");
        p2.setPhone("123456");

        Passenger p3 = new Passenger();
        p3.setId(3L);
        p3.setEmail("jack.doe@gmail.com");
        p3.setFirstName("Jack");
        p3.setLastName("Doe");
        p3.setPhone("142536");

        Passenger p4 = new Passenger();
        p4.setId(4L);
        p4.setEmail("joe.doe@gmail.com");
        p4.setFirstName("Joe");
        p4.setLastName("Doe");
        p4.setPhone("362514");

        return new Passenger[]{p1, p2, p3, p4};
    }
}
