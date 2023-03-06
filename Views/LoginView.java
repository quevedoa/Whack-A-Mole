package Views;

import Classes.Player;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class LoginView {
    public LoginView() {
        body();
    }

    private void body() {
        JFrame loginView = new JFrame("Whack-A-Mole Login");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel(new GridLayout(2, 1));
        JLabel usernameLabel = new JLabel("Usuario:");
        JTextField usernameTextField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);

        JButton submitButton = new JButton("Entrar");
        submitButton.addActionListener( e -> {
            validateUsername(usernameTextField.getText());
            loginView.dispose();
        });

        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitPanel.add(submitButton);

        panel.add(usernamePanel);
        panel.add(submitPanel);

        loginView.add(panel);

        loginView.setSize(500,250);
        loginView.setLocationRelativeTo(null);
        loginView.setVisible(true);
    }

    private void validateUsername(String username) {
        String ip = "localhost";
        int serverPort = 50000;

        while (username == null) {}

        Socket s = null;
        try {
            s = new Socket(ip, serverPort);

            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            out.writeUTF(username);

            int moveSocketPort = (int) in.readObject();
            String activeMQURL = (String) in.readObject();
            String monsterQueue = (String) in.readObject();
            String winnerTopic = (String) in.readObject();
            int rows = (int) in.readObject();
            int columns = (int) in.readObject();
            Player player = (Player) in.readObject();

            new GameView(rows, columns, player, moveSocketPort, activeMQURL, monsterQueue, winnerTopic);

            s.close();

        } catch (Exception e) {
            System.out.println("Validate Username Error: " + e.getMessage());
        } finally {
            if (s != null) try {
                s.close();
            } catch (IOException e) {
                System.out.println("close:" + e.getMessage());
            }
        }
    }

    public static void main(String args[]) {
        new LoginView();
        new LoginView();
    }
}

