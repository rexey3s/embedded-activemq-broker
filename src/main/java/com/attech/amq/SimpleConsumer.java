package com.attech.amq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.*;

/**
 * @author Chuong Dang created on 5/8/15.
 */
@Component
public class SimpleConsumer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleConsumer.class);

    private static final Boolean NON_TRANSACTED = false;
    private static final String DESTINATION_NAME = "queue/simple";
    private static final int MESSAGE_TIMEOUT_MILLISECONDS = 120000;
    private Thread consumerThread;
    
    @PostConstruct
    public void init() {
        consumerThread = new Thread(this);
        consumerThread.start();
    }

    @Override
    public void run() {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            ActiveMQConnectionFactory jmsConnectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            jmsConnectionFactory.setUserName("admin");
            jmsConnectionFactory.setPassword("admin");
            connection = jmsConnectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(DESTINATION_NAME);
            MessageConsumer consumer = session.createConsumer(destination);

            LOG.info("Start consuming messages from " + destination.toString() + " with " + MESSAGE_TIMEOUT_MILLISECONDS + "ms timeout");

            // Synchronous message consumer
            /*
            int i = 1;
            while (true) {
                Message message = consumer.receive(MESSAGE_TIMEOUT_MILLISECONDS);
                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        LOG.info("Got " + (i++) + ". message: " + text);
                    }
                } else {
                    break;
                }
            }
            */

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        LOG.info("Got message: " + text);

                    }
                } catch (JMSException ex) {
                    LOG.info("Error while receiving message {}", ex.getMessage());
                }
            });
            
            Thread.sleep(MESSAGE_TIMEOUT_MILLISECONDS);
            consumer.close();
            session.close();
        } catch (Throwable t) {
            LOG.error("Error receiving message", t);
        } finally {
            // Cleanup code
            // In general, you should always close producers, consumers,
            // sessions, and connections in reverse order of creation.
            // For this simple example, a JMS connection.close will
            // clean up all other resources.
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOG.error("Error closing connection", e);
                }
            }
        }
    }

}
