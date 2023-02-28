package Views;

import Classes.Move;
import Classes.Player;
import Controllers.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class GameView {
    public GameView(int rows, int columns, Player player, HashMap<String, String> addresses) {
        GameController controller = new GameController();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setTitle("Bienvenido " + player.getUsername());
        frame.setSize(500, 500);

        JPanel scorePanel = new JPanel();
        JLabel scoreLabel = new JLabel("Score: " + player.getScore());
        scorePanel.add(scoreLabel);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(rows, columns));

        int totalMoles = rows*columns;
        JCheckBox[] checkboxes = new JCheckBox[totalMoles];
        for (int i = 0; i < totalMoles; i++) {
            checkboxes[i] = new JCheckBox("Hole" + (i+1));
            checkboxes[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    JCheckBox checkBox = (JCheckBox) e.getItem();
                    if (checkBox.isSelected()) {
                        System.out.println(checkBox.getText() + " is checked");
                        int r = totalMoles/columns;
                        int c = totalMoles%rows;
                        sendMove(new Move(r,c));
                        checkBox.setSelected(false);
                    }
                }
            });

            JPanel checkboxPanel = new JPanel();
            checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
            checkboxPanel.add(checkboxes[i]);
            checkboxPanel.add(new JLabel());

            panel.add(checkboxPanel);
        }

        // Add the panels to the window with a BorderLayout
        frame.add(scorePanel, BorderLayout.SOUTH);
        frame.add(panel, BorderLayout.CENTER);

        // Show the window
        frame.setVisible(true);
    }

    private String sendMove(Move move) {
        String ip = "localhost";
        int serverPort = 49152; // Estos los deberían mandar desde loginView

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

    public static void main(String[] args) {

//        GameView checkboxGrid = new GameView(3,3,);
    }
}
