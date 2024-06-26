package com.dreamlabs.activemq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * The Retailer orders computers from the Vendor by sending a message via
 * the VendorOrderQueue. It then syncronously receives the reponse message
 * and reports if the order was successful or not.
 */
public class Retailer implements Runnable {
    private String url;
    private String user;
    private String password;

    public Retailer(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void run() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        try {
            Connection connection = connectionFactory.createConnection();

            // The Retailer's session is non-trasacted.
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination vendorOrderQueue = session.createQueue("VendorOrderQueue");
            TemporaryQueue retailerConfirmQueue = session.createTemporaryQueue();

            MessageProducer producer = session.createProducer(vendorOrderQueue);
            MessageConsumer replyConsumer = session.createConsumer(retailerConfirmQueue);

            connection.start();

            for (int i = 0; i < 5; i++) {
                MapMessage message = session.createMapMessage();
                message.setString("Item", "Computer(s)");
                int quantity = (int)(Math.random() * 4) + 1;
                message.setInt("Quantity", quantity);
                message.setJMSReplyTo(retailerConfirmQueue);
                producer.send(message);
                System.out.println("Retailer: Ordered " + quantity + " computers.");

                MapMessage reply = (MapMessage) replyConsumer.receive();
                if (reply.getBoolean("OrderAccepted")) {
                    System.out.println("Retailer: Order Filled");
                } else {
                    System.out.println("Retailer: Order Not Filled");
                }
            }

            // Send a non-MapMessage to signal the end
            producer.send(session.createMessage());

            replyConsumer.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url = "tcp://localhost:61616";
        String user = null;
        String password = null;

        if (args.length >= 1) {
            url = args[0];
        }

        if (args.length >= 2) {
            user = args[1];
        }

        if (args.length >= 3) {
            password = args[2];
        }

        Retailer r = new Retailer(url, user, password);

        new Thread(r, "Retailer").start();
    }
}