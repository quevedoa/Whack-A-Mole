package Server;

import Classes.Move;
import Classes.Player;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MoveConnection extends Thread {
    private int moveSocketPort;
    private String activeMQURL;
    private String moveQueue;

    public MoveConnection(int moveSocketPort, String activeMQURL, String moveQueue) {
        this.moveSocketPort = moveSocketPort;
        this.activeMQURL = activeMQURL;
        this.moveQueue = moveQueue;
    }

    @Override
    public void run() {
        try {
            ServerSocket moveServerSocket = new ServerSocket(moveSocketPort);
            while (true) {
                Socket moveSocket = moveServerSocket.accept();
                RegisterMoveThread registerMove = new RegisterMoveThread(moveSocket, activeMQURL, moveQueue);
                registerMove.start();
            }
        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }
}

class RegisterMoveThread extends Thread {
    private ObjectInputStream in;
    private String activeMQURL;
    private String moveQueue;

    public RegisterMoveThread(Socket socket, String activeMQURL, String moveQueue) {
        this.activeMQURL = activeMQURL;
        this.moveQueue = moveQueue;

        try {
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

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQURL);
            connectionFactory.setTrustAllPackages(true);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Destination sendDestination = session.createQueue(moveQueue);
            MessageProducer messageProducer = session.createProducer(sendDestination);

            ObjectMessage movimiento = session.createObjectMessage();
            Object[] movimientoTupla = {player,move};
            movimiento.setObject(movimientoTupla);
            messageProducer.send(movimiento);

            connection.close();

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}