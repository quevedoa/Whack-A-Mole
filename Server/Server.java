package Server;

import Classes.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

import Views.GameView;
import org.apache.activemq.ActiveMQConnection;

public class Server {
    private ArrayList<String> usernameDB;
    private ArrayList<Player> playerDB;
    private int loginSocketPort = 50000;
    private int moveSocketPort = 55000;
    public static String activeMQURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    public static String monsterQueue = "MONSTER_QUEUE";
    public static String winnerQueue = "WINNER_QUEUE";
    private int rows;
    private int columns;


    public Server(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        this.usernameDB = new ArrayList<>();
        this.playerDB = new ArrayList<>();

        try {
            ServerSocket loginServerSocket = new ServerSocket(loginSocketPort);
            ServerSocket moveServerSocket = new ServerSocket(moveSocketPort);
            // Empezar hilo que agrega monstruos

            while (true) {
                System.out.println("Waiting for connections...");

                Socket loginSocket = loginServerSocket.accept();
                LoginConnection loginConnection = new LoginConnection(loginSocket, moveSocketPort, rows, columns, usernameDB, playerDB);
                loginConnection.start();

//                Socket moveSocket = moveServerSocket.accept();
//                MoveConnection moveConnection = new MoveConnection();
//                moveConnection.start();
            }
        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        Server server = new Server(3,3);
    }
}

class LoginConnection extends Thread {
    private DataInputStream in;
    private Socket connectionSocket;
    private ArrayList<String> usernameDB;
    private ArrayList<Player> playerDB;
    private int rows;
    private int columns;
    private int moveSocketPort;

    public LoginConnection(Socket connectionSocket, int moveSocketPort, int rows, int columns, ArrayList<String> usernameDB, ArrayList<Player> playerDB) {
        try {
            this.rows = rows;
            this.columns = columns;
            this.connectionSocket = connectionSocket;
            this.moveSocketPort = moveSocketPort;
            this.usernameDB = usernameDB;
            this.playerDB = playerDB;

            in = new DataInputStream(connectionSocket.getInputStream());

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String username = in.readUTF();
            Player player;

            if (!usernameDB.contains(username)) {
                player = new Player(username);
                usernameDB.add(username);
                playerDB.add(player);
            } else {
                player = playerDB.get(usernameDB.indexOf(username));
            }

            HashMap<String, String> addresses = new HashMap<>();
            addresses.put("activeMQURL", Server.activeMQURL);
            addresses.put("monsterQueue", Server.monsterQueue);
            addresses.put("winnerQueue", Server.winnerQueue);
            addresses.put("moveSocketPort", ""+moveSocketPort);

            GameView gameView = new GameView(rows, columns, player, addresses);

        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                connectionSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }
}

class MoveConnection extends Thread {
    private ObjectInputStream in;
    private Socket connectionSocket;
    public MoveConnection(Socket connectionSocket) {
        try {
            this.connectionSocket = connectionSocket;

            in = new ObjectInputStream(connectionSocket.getInputStream());

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {

    }
}