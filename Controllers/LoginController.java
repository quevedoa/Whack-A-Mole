package Controllers;

import Classes.*;

import java.net.*;
import java.io.*;

public class LoginController {

    private String ip = "localhost";
    private int serverPort = 49152;
    public LoginController() {}

    public String validateUsername(String username) {
        while (username == null) {}

        // Chequemos que el username está bien
        Socket s = null;
        Player jugador = new Player(username);

        try {
            s = new Socket(ip, serverPort);

            DataInputStream in = new DataInputStream(s.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

            out.writeObject(jugador);

            String response = in.readUTF();
            s.close();

            if (!response.equals("Usuario ya utilizado.")) {
                // Llama el GameView y el GameModel
            }

            return response;

        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            if (s != null) try {
                s.close();
            } catch (IOException e) {
                System.out.println("close:" + e.getMessage());
            }
        }

        return "Error";
    }
}
