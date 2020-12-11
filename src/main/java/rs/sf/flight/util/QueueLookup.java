package rs.sf.flight.util;

import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QueueLookup {

    public Queue lookupQueue(InitialContext initialContext, String queueName) throws NamingException {
        Object requestQueueLookup = initialContext.lookup(queueName);
        if (requestQueueLookup instanceof Queue) {
            return (Queue) requestQueueLookup;
        } else {
            throw new RuntimeException(queueName + " is not an instance of javax.jms.Queue");
        }
    }
}
