package Server;

import javax.jms.*;

import Classes.Move;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import java.io.Serializable;
import java.util.Random;

public class MoleThread extends Thread {
    private String url;
    private String topicSubject;
    private int numJuego;
    private int row;
    private int col;
    private Random rand = new Random();
    public MoleThread(String url, String topicSubject, int numJuego, int row, int col) {
        this.url = url;
        this.topicSubject = topicSubject;
        this.numJuego = numJuego;
        this.row = row;
        this.col = col;
    }

    @Override
    public void run() {
        MessageProducer messageProducer;
        ObjectMessage mole;

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connectionFactory.setTrustAllPackages(true);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Topic sendDestination = session.createTopic(topicSubject);
            messageProducer = session.createProducer(sendDestination);

            while (true) {
                Move nextMove = new Move(rand.nextInt(row), rand.nextInt(col));
                mole = session.createObjectMessage();
                mole.setObject(nextMove);
                messageProducer.send(mole);

                Thread.sleep(5000);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
