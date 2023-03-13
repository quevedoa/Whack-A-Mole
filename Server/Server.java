package Server;

import Classes.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.activemq.ActiveMQConnection;

public class Server {
    private HashMap<String, Player> juegoActual;
    private int loginSocketPort = 50000;
    private int moveSocketPort = 55000;
    public String activeMQURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    public String topoTopico = "TOPO_TOPIC";
    public String moveQueue = "MOVE_QUEUE";
    public String winnerTopic = "WINNER_TOPIC";
    private int maxScore;

    public Server(int rows, int columns, int moleDelay, int maxScore) {
        this.juegoActual = new HashMap<>();
        this.maxScore = maxScore;

        try {
            // Echamos a andar hilo que lee los movimientos
            MoveConnection moveConnection = new MoveConnection(moveSocketPort, activeMQURL, moveQueue);
            moveConnection.start();

            // Echamos a andar hilo que calcula quien gana las rondas
            RoundWinnerThread roundWinnerThread = new RoundWinnerThread(juegoActual, maxScore, activeMQURL, moveQueue, winnerTopic);
            roundWinnerThread.start();

            // Echamos a andar hilo que solo hace topos
            MoleThread moleThread = new MoleThread(activeMQURL, topoTopico, rows, columns, moleDelay);
            moleThread.start();

            ServerSocket loginServerSocket = new ServerSocket(loginSocketPort);

            while (true) {
                Socket loginSocket = loginServerSocket.accept();
                LoginConnection loginConnection = new LoginConnection(loginSocket, moveSocketPort, activeMQURL, topoTopico, winnerTopic, rows, columns, juegoActual);
                loginConnection.start();
            }
        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        new Server(2,2, 1000, 3);
    }
}