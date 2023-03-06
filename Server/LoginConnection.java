package Server;

import Classes.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class LoginConnection extends Thread {
    private DataInputStream in;
    private ObjectOutputStream out;
    private Socket connectionSocket;
    String activeMQURL;
    String topoTopico;
    String winnerTopic;
    private HashMap<String, Player> juegoActual;
    private int rows;
    private int columns;
    private int moveSocketPort;


    public LoginConnection(Socket connectionSocket, int moveSocketPort, String activeMQURL, String topoTopico, String winnerTopic, int rows, int columns, HashMap<String, Player>juegoActual) {
        try {
            this.juegoActual = juegoActual;
            this.rows = rows;
            this.columns = columns;
            this.connectionSocket = connectionSocket;
            this.moveSocketPort = moveSocketPort;
            this.activeMQURL = activeMQURL;
            this.topoTopico = topoTopico;
            this.winnerTopic = winnerTopic;

            in = new DataInputStream(connectionSocket.getInputStream());
            out = new ObjectOutputStream(connectionSocket.getOutputStream());

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String username = in.readUTF();
            Player player;

            if (juegoActual.containsKey(username)) {
                player = juegoActual.get(username);
            } else {
                player = new Player(username);
                juegoActual.put(username,player);
            }

            out.writeObject(moveSocketPort);
            out.writeObject(activeMQURL);
            out.writeObject(topoTopico);
            out.writeObject(winnerTopic);
            out.writeObject(rows);
            out.writeObject(columns);
            out.writeObject(player);
            out.writeObject(juegoActual);

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