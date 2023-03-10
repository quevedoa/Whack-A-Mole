package Server;

import javax.jms.*;

import Classes.Move;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Random;

public class MoleThread extends Thread {
    private String url;
    private String topicSubject;
    private int numTopo;
    private int row;
    private int col;
    private int moleDelay;
    private Random rand = new Random();
    public MoleThread(String url, String topicSubject, int row, int col, int moleDelay) {
        this.url = url;
        this.topicSubject = topicSubject;
        this.row = row;
        this.col = col;
        this.moleDelay = moleDelay;
        this.numTopo = 0;
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
                Move nextMove = new Move(rand.nextInt(row), rand.nextInt(col), numTopo);

                mole = session.createObjectMessage();
                mole.setObject(nextMove);
                messageProducer.send(mole);

                Thread.sleep(moleDelay);
                numTopo = numTopo+1;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
