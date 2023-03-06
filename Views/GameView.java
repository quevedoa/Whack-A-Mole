package Views;

import Classes.Move;
import Classes.Player;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import javax.swing.*;
import java.awt.*;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameView {
    private Player player;
    private int ronda = 0;

    private int moveSocketPort;
    public GameView(int rows, int columns, Player player, int moveSocketPort,
                    String activeMQURL, String topoQueue, String winnerTopic) {

        this.player = player;

        this.moveSocketPort = moveSocketPort;

        JFrame frame = new JFrame();

        frame.setTitle("Bienvenido " + player.getUsername());
        frame.setSize(500, 500);

        JPanel ganadorDeRondaPanel = new JPanel();
        JLabel ganadorDeRondaLabel = new JLabel();
        ganadorDeRondaPanel.add(ganadorDeRondaLabel);

        JPanel scorePanel = new JPanel();
        JLabel scoreLabel = new JLabel("Intenta pegarle a los topos antes que los demás");
        scorePanel.add(scoreLabel);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.Y_AXIS));
        labelPanel.add(ganadorDeRondaLabel);
        labelPanel.add(scoreLabel);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(rows, columns));

        int totalMoles = rows*columns;
        JCheckBox[] checkboxes = new JCheckBox[totalMoles];
        for (int i = 0; i < totalMoles; i++) {
            checkboxes[i] = new JCheckBox("Hole" + (i+1));
            int r = i/columns;
            int c = i%columns;
            checkboxes[i].addActionListener( e -> {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox.isSelected()) {
                    checkBox.setSelected(false);
                } else if (!checkBox.isSelected()) {
                    System.out.println(checkBox.getText() + " was wacked!");
                    sendMove(new Move(r,c,ronda));
                }
            });

            JPanel checkboxPanel = new JPanel();
            checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
            checkboxPanel.add(checkboxes[i]);
            checkboxPanel.add(new JLabel());

            panel.add(checkboxPanel);
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(labelPanel,BorderLayout.CENTER);

        frame.add(contentPanel);
        frame.setVisible(true);

        // Hilo que checa el tópico de topos y actualiza juego
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

                Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

                Topic topoTopic = session.createTopic(topoQueue);
                topoMessageConsumer = session.createConsumer(topoTopic);

                while (true) {
                    moleMessage = (ObjectMessage) topoMessageConsumer.receive();
                    moleLocation = (Move) moleMessage.getObject();

                    this.ronda = moleLocation.getRonda();

                    int row = moleLocation.getRow();
                    int col = moleLocation.getColumn();

                    int index = row*columns+col;

                    clearCheckboxes(checkboxes);
                    checkboxes[index].setSelected(state);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });

        // Hilo que checa el topico de ganadores y actualiza los puntajes
        // también avisa si alguien ya ganó
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
                        ganadorDeRondaLabel.setText("Ronda " + ronda + " la ganó " + winnerPlayer.getUsername());
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
        Socket s;

        try {
            s = new Socket(ip, serverPort);

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
}
