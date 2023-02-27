package Server;

import Classes.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;
import java.util.Random;

import javax.jms.JMSException;
import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Server {
    private Set<String> usernameDB;
    private Set<Player> playerDB;
    //= new HashSet<>();
    private int loginPort = 49152;
    private String topicURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private String monsterQueue = "MONSTER_QUEUE";


    public Server() {
        try {
            ServerSocket listenSocket = new ServerSocket(loginPort);
            while (true) {
                System.out.println("Verifying login...");
                Socket loginSocket = listenSocket.accept();
                LoginConnection c = new LoginConnection(loginSocket, topicURL, monsterQueue);
            }
        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }

    public void connectLoginSocket() {
        try {
            ServerSocket listenSocket = new ServerSocket(loginPort);
            while (true) {
                System.out.println("Verifying login...");
                Socket loginSocket = listenSocket.accept();
                LoginConnection
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

class LoginConnection extends Thread {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket loginSocket;
    private String topicURL;
    private String monsterQueue;

    public LoginConnection(Socket loginSocket, String topicURL, String monsterQueue) {
        try {
            this.loginSocket = loginSocket;
            this.topicURL = topicURL;
            this.monsterQueue = monsterQueue;
            out = new ObjectOutputStream(loginSocket.getOutputStream());
            in = new ObjectInputStream(loginSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // an echo server
            Player player = (Player) in.readObject();
            String username = player.getUsername();

            String[] response = validateUsername(username);
            System.out.println("Message received from: " + loginSocket.getRemoteSocketAddress());
            out.writeUTF(username);

        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                loginSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }

    public String[] validateUsername(String username) {
        String[] ipSubjectResponse = new String[2];

        if (!usernameDB.contains(username)) {
            usernameDB.add(username);
        } else {

        }
        return null;
    }
}
