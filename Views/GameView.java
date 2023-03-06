package Views;

import Classes.Move;
import Classes.Player;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;

public class GameView {
    private Player player;
    private int rows;
    private int columns;
    private int ronda = 0;
    private Set<Player> juegoActual;

    private int moveSocketPort;
    public GameView(int rows, int columns, Player player, int moveSocketPort,
                    String activeMQURL, String topoQueue, String winnerTopic, Set<Player> juegoActual) {
//        GameController controller = new GameController();

        this.player = player;
        this.juegoActual = juegoActual;

        this.moveSocketPort = moveSocketPort;

        this.rows = rows;
        this.columns = columns;

        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setTitle("Bienvenido " + player.getUsername());
        frame.setSize(500, 500);

        JPanel ganadorDeRondaPanel = new JPanel();
        JLabel ganadorDeRondaLabel = new JLabel();
        ganadorDeRondaPanel.add(ganadorDeRondaLabel);

        JPanel scorePanel = new JPanel();
        JLabel scoreLabel = new JLabel("Intenta pegarle a los topos antes que los demás");
        scorePanel.add(scoreLabel);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(rows, columns));

        int totalMoles = rows*columns;
        JCheckBox[] checkboxes = new JCheckBox[totalMoles];
        for (int i = 0; i < totalMoles; i++) {
            checkboxes[i] = new JCheckBox("Hole" + (i+1));
            int r = i/columns;
            int c = i%columns;
            checkboxes[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBox checkBox = (JCheckBox) e.getSource();
                    if (checkBox.isSelected()) {
                        checkBox.setSelected(false);
                        scoreLabel.setText("Pendejo no te mames");
                    } else if (!checkBox.isSelected()) {
                        System.out.println(checkBox.getText() + " was wacked!");
                        sendMove(new Move(r,c,ronda));
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
        frame.add(ganadorDeRondaLabel);
        frame.add(scorePanel, BorderLayout.SOUTH);
        frame.add(panel, BorderLayout.CENTER);

        // Show the window
        frame.setVisible(true);

        Thread thread1 = new Thread(() -> {
            boolean state = true;
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQURL);
                connectionFactory.setTrustAllPackages(true);
                Connection connection = connectionFactory.createConnection();
                connection.start();

                MessageConsumer topoMessageConsumer;
                ObjectMessage moleMessage;
                Move moleLocation;

//                MessageConsumer winnerMessageConsumer;
//                ObjectMessage playerMessage;
//                Player winnerPlayer;

                Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

                Topic topoTopic = session.createTopic(topoQueue);
                topoMessageConsumer = session.createConsumer(topoTopic);

//                Topic winnerTopic = session.createTopic(winnerQueue);
//                winnerMessageConsumer = session.createConsumer(winnerTopic);

                while (true) {
                    moleMessage = (ObjectMessage) topoMessageConsumer.receive();
                    moleLocation = (Move) moleMessage.getObject();

//                    System.out.println("pre atorada");
//                    playerMessage = (ObjectMessage) winnerMessageConsumer.receive();
//                    winnerPlayer = (Player) playerMessage.getObject();
//                    System.out.println("post atorada");
//
//                    if (this.player == winnerPlayer) {
//                        scoreLabel.setText(""+winnerPlayer.getScore());
//                    }

                    this.ronda = moleLocation.getRonda();

                    int row = moleLocation.getRow();
                    int col = moleLocation.getColumn();

                    int index = row*columns+col;

                    clearCheckboxes(checkboxes);
                    checkboxes[index].setSelected(state);
//                    state = !state;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQURL);
                connectionFactory.setTrustAllPackages(true);
                Connection connection = connectionFactory.createConnection();
                connection.start();

                MessageConsumer winnerMessageConsumer;
                ObjectMessage playerMessage;
                Player winnerPlayer;

                Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

                Topic topic = session.createTopic(winnerTopic);
                winnerMessageConsumer = session.createConsumer(topic);

                while (true) {
                    playerMessage = (ObjectMessage) winnerMessageConsumer.receive();
                    winnerPlayer = (Player) playerMessage.getObject();

                    if (winnerPlayer.getUsername().equals(this.player.getUsername())) {
                        this.player = winnerPlayer;
                    }

                    if (winnerPlayer.isCurrentGameWinner()) {
                        scoreLabel.setText("Ganó " + winnerPlayer.getUsername() + "!!!!");
                        this.player.reset();
                    } else {
                        ganadorDeRondaLabel.setText("Esta ronda la ganó " + winnerPlayer.getUsername());
                        scoreLabel.setText("Juegos Ganados: " + this.player.getJuegosGanados() + " | Score: " + this.player.getScore());
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        thread1.start();
        thread2.start();
    }

    private void sendMove(Move move) {
        String ip = "localhost";
        int serverPort = moveSocketPort; // Estos los deberían mandar desde loginView

        // Chequemos que el username está bien
        Socket s = null;

        try {
            s = new Socket(ip, serverPort);

            DataInputStream in = new DataInputStream(s.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

            out.writeObject(move);
            out.writeObject(this.player);

            s.close();
        } catch (Exception e) {
            System.out.println("Sending Move Error:" + e.getMessage());
        }
    }

    private void clearCheckboxes(JCheckBox[] checkBoxes) {
        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].setSelected(false);
        }
    }

    public static void main(String[] args) {

//        GameView checkboxGrid = new GameView(3,3,);
    }
}
