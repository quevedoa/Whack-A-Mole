package Server;

import Classes.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import org.apache.activemq.ActiveMQConnection;

public class Server {
//    private HashMap<Integer, ArrayList<Player>> juegos;
//    private Set<Player> juegoActual;
    private HashMap<String, Player> juegoActual;
    private Player ganadorDeRonda = null;
    private ArrayList<String> usernameDB;
    private ArrayList<Player> playerDB;
    private int loginSocketPort = 50000;
    private int moveSocketPort = 55000;
    public static String activeMQURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    public static String monsterQueue = "MONSTER_QUEUE";
    public static String moveQueue = "MOVE_QUEUE";
    public static String winnerTopic = "WINNER_TOPIC";
    private int rows;
    private int columns;
    private int maxScore = 3;
    private int numJuego;



    public Server(int rows, int columns) {
//        this.juegoActual = new HashSet<>();
        this.juegoActual = new HashMap<>();

        this.rows = rows;
        this.columns = columns;

        this.usernameDB = new ArrayList<>();
        this.playerDB = new ArrayList<>();

        this.numJuego = 1;

        try {
            MoveConnection moveConnection = new MoveConnection(moveSocketPort, juegoActual, ganadorDeRonda, maxScore, numJuego);
            moveConnection.start();

            RoundWinnerThread roundWinnerThread = new RoundWinnerThread(juegoActual, maxScore);
            roundWinnerThread.start();

            ServerSocket loginServerSocket = new ServerSocket(loginSocketPort);

            // Echamos a andar hilo que solo hace topos
            MoleThread moleThread = new MoleThread(activeMQURL, monsterQueue, numJuego, rows, columns);
            moleThread.start();

            while (true) {
                System.out.println("Waiting for connections...");
                System.out.println(usernameDB);

                Socket loginSocket = loginServerSocket.accept();
                LoginConnection loginConnection = new LoginConnection(loginSocket, moveSocketPort, rows, columns, usernameDB, playerDB, juegoActual);
                loginConnection.start();
            }
        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        Server server = new Server(2,2);
    }
}