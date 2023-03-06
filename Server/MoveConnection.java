package Server;

import Classes.Move;
import Classes.Player;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.Iterator;

public class MoveConnection extends Thread {
    private int moveSocketPort;
    private int numJuego;
    private int maxScore;
    private Set<Player> juegoActual;
    private Player ganadorDeRonda;
    public MoveConnection(int moveSocketPort, Set<Player> juegoActual, Player ganadorDeRonda, int maxScore, int numJuego) {
        this.maxScore = maxScore;
        this.juegoActual = juegoActual;
        this.ganadorDeRonda = ganadorDeRonda;
        this.moveSocketPort = moveSocketPort;
        this.numJuego = numJuego;
    }

    @Override
    public void run() {
        try {
            ServerSocket moveServerSocket = new ServerSocket(moveSocketPort);
            while (true) {
                Socket moveSocket = moveServerSocket.accept();
                RegisterMoveThread registerMove = new RegisterMoveThread(moveSocket, juegoActual, ganadorDeRonda, maxScore);
                registerMove.start();
            }
        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }
}

class RegisterMoveThread extends Thread {
    private ObjectInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private Set<Player> juegoActual;
    private Player ganadorDeRonda;
    private int maxScore;
    public RegisterMoveThread(Socket socket, Set<Player> juegoActual, Player ganadorDeRonda, int maxScore) {
        try {
            this.juegoActual = juegoActual;
            this.ganadorDeRonda = ganadorDeRonda;
            this.socket = socket;
            this.maxScore = maxScore;
            out = new DataOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Register Move Connection Error:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            Move move = (Move) in.readObject();
            Player player = (Player) in.readObject();

            // Checar si alguien ganó
//                player.givePoint();
//                int playerScore = player.getScore();
//                if (playerScore >= maxScore) {
//                    player.setCurrentGameWinner(true);
//                }
//                juegoActual.add(player);

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Server.activeMQURL);
            connectionFactory.setTrustAllPackages(true);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
//                Topic sendDestination = session.createTopic(Server.winnerQueue);
            Destination sendDestination = session.createQueue(Server.moveQueue);
            MessageProducer messageProducer = session.createProducer(sendDestination);

            ObjectMessage movimiento = session.createObjectMessage();
            Object[] movimientoTupla = {player,move};
            movimiento.setObject(movimientoTupla);
            messageProducer.send(movimiento);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private boolean alguienGano(Set<Player> juegoActual) {
        Iterator<Player> iterator = juegoActual.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getScore() >= maxScore)
                return true;
        }
        return false;
    }
}