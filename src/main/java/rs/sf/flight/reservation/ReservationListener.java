package rs.sf.flight.reservation;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import rs.sf.flight.domain.Passenger;
import rs.sf.flight.util.QueueLookup;

import javax.jms.*;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ReservationListener implements MessageListener {

    private static final Set<String> checkedInPassengers = new HashSet<>();

    static {
        checkedInPassengers.add("john.doe@gmail.com");
        checkedInPassengers.add("jane.doe@gmail.com");
    }

    private final QueueLookup queueLookup = new QueueLookup();

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                InitialContext initialContext = new InitialContext();
                Queue replyQueue = queueLookup.lookupQueue(initialContext, "queue/replyQueue");
                try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(); JMSContext jmsContext = factory.createContext()) {
                    MapMessage mapMessage = jmsContext.createMapMessage();
                    Serializable object = objectMessage.getObject();
                    if (object instanceof Passenger) {
                        Passenger passenger = (Passenger) object;
                        mapMessage.setString("checked in", passenger.getEmail() + ": " + checkedInPassengers.contains(passenger.getEmail()));
                        JMSProducer producer = jmsContext.createProducer();
                        producer.send(replyQueue, mapMessage);
                    } else {
                        throw new RuntimeException("object is not an instance of rs.sf.flight.domain.Passenger");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("message is not an instance of javax.jms.ObjectMessage");
        }
    }
}
