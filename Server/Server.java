package Server;

import Classes.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import org.apache.activemq.ActiveMQConnection;

public class Server {
    private Set<String> usernameDB;
    private Set<Player> playerDB;
    //= new HashSet<>();
    private int socketPort = 49152;
    private String topicURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private String monsterQueue = "MONSTER_QUEUE";


    public Server() {
        try {
            ServerSocket listenSocket = new ServerSocket(socketPort);
            // Empezar hilo que agrega monstruos
            while (true) {
                System.out.println("Connecting...");
                Socket connectionSocket = listenSocket.accept();
                Connection c = new Connection(connectionSocket, topicURL, monsterQueue);
            }
        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }

    public void connectLoginSocket() {
        try {
            ServerSocket listenSocket = new ServerSocket(socketPort);
            while (true) {
                System.out.println("Verifying login...");
                Socket loginSocket = listenSocket.accept();
                Connection connection = new Connection(loginSocket,topicURL,monsterQueue);
                connection.start();
            }
        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }

    public void addPlayer(Player jugador) {
        this.playerDB.add(jugador);
    }
    public void addUsername(String username) {
        this.usernameDB.add(username);
    }

}

class Connection extends Thread {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket connectionSocket;
    private String topicURL;
    private String monsterQueue;

    public Connection(Socket connectionSocket, String topicURL, String monsterQueue) {
        try {
            this.connectionSocket = connectionSocket;
            this.topicURL = topicURL;
            this.monsterQueue = monsterQueue;
            out = new ObjectOutputStream(connectionSocket.getOutputStream());
            in = new ObjectInputStream(connectionSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // an echo server
            Object object = in.readObject();
            if (object instanceof Player) {

            } else if (object instanceof Move) {

            }
            Player player = (Player) in.readObject();
            String username = player.getUsername();

            String[] response = validateUsername(username);
            System.out.println("Message received from: " + connectionSocket.getRemoteSocketAddress());
            out.writeUTF(username);

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

    public String[] validateUsername(String username) {
        String[] ipSubjectResponse = new String[2];

//        if (!usernameDB.contains(username)) {
//            usernameDB.add(username);
//        } else {
//
//        }
        return null;
    }
}
