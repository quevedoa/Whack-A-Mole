package Controllers;

import Classes.*;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameController {
    private String ip = "localhost";
    private int serverPort = 49152; // Estos los deberían mandar desde loginView

    public GameController() {

    }

    public String sendMove(Move move) {
        // Chequemos que el username está bien
        Socket s = null;

        try {
            s = new Socket(ip, serverPort);

            DataInputStream in = new DataInputStream(s.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

            out.writeObject(move);

            String response = in.readUTF();
            s.close();

            // Trata de leer el topico de quien gano y quedate escuchando hasta que llegue

            if (!response.equals("Usuario ya utilizado.")) {
                // Llama el GameView y el GameModel
            }

            if (s != null) try {
                s.close();
            } catch (IOException e) {
                System.out.println("close:" + e.getMessage());
            }

            return response;

        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            return null;
        }
    }
}
