package Server;

import Classes.Player;
import Views.GameView;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class LoginConnection extends Thread {
    private DataInputStream in;
    private ObjectOutputStream out;
    private Socket connectionSocket;
    private ArrayList<String> usernameDB;
    private ArrayList<Player> playerDB;
    private Set<Player> juegoActual;
    private int rows;
    private int columns;
    private int moveSocketPort;


    public LoginConnection(Socket connectionSocket, int moveSocketPort, int rows, int columns,
                           ArrayList<String> usernameDB, ArrayList<Player> playerDB, Set<Player> juegoActual) {
        try {
            this.juegoActual = juegoActual;
            this.rows = rows;
            this.columns = columns;
            this.connectionSocket = connectionSocket;
            this.moveSocketPort = moveSocketPort;
            this.usernameDB = usernameDB;
            this.playerDB = playerDB;

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

            if (!usernameDB.contains(username)) {
                player = new Player(username);
                usernameDB.add(username);
                playerDB.add(player);
            } else {
                player = playerDB.get(usernameDB.indexOf(username));
            }

            this.juegoActual.add(player);

            out.writeObject(moveSocketPort);
            out.writeObject(Server.activeMQURL);
            out.writeObject(Server.monsterQueue);
            out.writeObject(Server.winnerQueue);
            out.writeObject(rows);
            out.writeObject(columns);
            out.writeObject(player);

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