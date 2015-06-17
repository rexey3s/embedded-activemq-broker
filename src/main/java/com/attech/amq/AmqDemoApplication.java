package com.attech.amq;

import org.apache.activemq.ActiveMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath*:./appContext.xml"})
public class AmqDemoApplication   {

    private static final Logger LOG = LoggerFactory.getLogger(AmqDemoApplication.class);
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "newSubject";


    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(AmqDemoApplication.class, args);


    }


}
