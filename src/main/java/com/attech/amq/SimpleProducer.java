package com.attech.amq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.*;

/**
 * @author Chuong Dang created on 5/8/15.
 */
@Component
public class SimpleProducer implements Runnable {
    private static Logger LOG = LoggerFactory.getLogger(SimpleProducer.class);
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    
    private static final Boolean NON_TRANSACTED = false;
    private static final long MESSAGE_TIME_TO_LIVE_MILLISECONDS = 0;
    private static final int MESSAGE_DELAY_MILLISECONDS = 100;
    private static final int NUM_MESSAGES_TO_BE_SENT = 100;
    private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
    private static final String DESTINATION_NAME = "queue/simple";
    private Thread producerThread;

    @PostConstruct
    public void init() {
        producerThread = new Thread(this);
        producerThread.start();
    }
    
    @Override
    public void run()  {

        Connection connection = null;

        try {
            ActiveMQConnectionFactory jmsConnectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            jmsConnectionFactory.setUserName("admin");
            jmsConnectionFactory.setPassword("admin");
            connection = jmsConnectionFactory.createConnection();
            connection.start();

            
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(DESTINATION_NAME);
            MessageProducer producer = session.createProducer(destination);

            producer.setTimeToLive(MESSAGE_TIME_TO_LIVE_MILLISECONDS);

            for (int i = 1; i <= NUM_MESSAGES_TO_BE_SENT; i++) {
                TextMessage message = session.createTextMessage(i + ". message sent");
                LOG.info("Sending to destination: " + destination.toString() + " this text: '" + message.getText());
                producer.send(message);
                Thread.sleep(MESSAGE_DELAY_MILLISECONDS);
            }

//            TextMessage message = session.createTextMessage(weatherService.getForecastJSON("Turku").getBody());
//            producer.send(message);
            // Cleanup
            producer.close();
            session.close();
            
        } catch (Throwable t) {
            LOG.error("Error sending message", t);
            
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
