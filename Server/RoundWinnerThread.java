package Server;

import javax.jms.*;

import Classes.Move;
import Classes.Player;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.HashMap;
import java.util.Set;

public class RoundWinnerThread extends Thread {
    private HashMap<String, Player> juegoActual;
    int ronda;
    int maxScore;
    boolean existeGanadorDeRonda;

    public RoundWinnerThread(HashMap<String, Player> juegoActual, int maxScore) {
        this.juegoActual = juegoActual;
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
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Server.activeMQURL);
            connectionFactory.setTrustAllPackages(true);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

            Destination sendDestination = session.createQueue(Server.moveQueue);
            moveMessageConsumer = session.createConsumer(sendDestination);

            Topic winnerTopic = session.createTopic(Server.winnerTopic);
            winnerMessageProducer = session.createProducer(winnerTopic);
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
                    System.out.println("TAMANO: " + juegoActual.size());
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

