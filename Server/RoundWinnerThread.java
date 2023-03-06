package Server;

import javax.jms.*;

import Classes.Move;
import Classes.Player;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.HashMap;

public class RoundWinnerThread extends Thread {
    private HashMap<String, Player> juegoActual;
    String activeMQURL;
    String moveQueue;
    String winnerTopic;
    int ronda;
    int maxScore;
    boolean existeGanadorDeRonda;

    public RoundWinnerThread(HashMap<String, Player> juegoActual, int maxScore, String activeMQURL, String moveQueue, String winnerTopic) {
        this.juegoActual = juegoActual;
        this.activeMQURL = activeMQURL;
        this.moveQueue = moveQueue;
        this.winnerTopic = winnerTopic;
        this.maxScore = maxScore;
        this.ronda = -1;
        this.existeGanadorDeRonda = false;
    }

    @Override
    public void run() {
        MessageConsumer moveMessageConsumer;
        MessageProducer winnerMessageProducer;
        ObjectMessage playerMessage;
        Object[] movimientoTupla;
        Player player;
        Move move;

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQURL);
            connectionFactory.setTrustAllPackages(true);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

            Destination sendDestination = session.createQueue(moveQueue);
            moveMessageConsumer = session.createConsumer(sendDestination);

            Topic winnerTopicDestination = session.createTopic(winnerTopic);
            winnerMessageProducer = session.createProducer(winnerTopicDestination);
            ObjectMessage winnerMessage;

            while (true) {
                playerMessage = (ObjectMessage) moveMessageConsumer.receive();
                movimientoTupla = (Object[]) playerMessage.getObject();

                player = (Player) movimientoTupla[0];
                move = (Move) movimientoTupla[1];

                if (move.getRonda() > ronda) {
                    ronda = move.getRonda();
                    player.givePoint();

                    if (player.getScore() >= maxScore) {
                        player.setCurrentGameWinner(true);
                        player.giveJuego();
                    }
                    winnerMessage = session.createObjectMessage();
                    winnerMessage.setObject(player);
                    winnerMessageProducer.send(winnerMessage);

                    juegoActual.put(player.getUsername(),player);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

