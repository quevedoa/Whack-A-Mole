package Views;

import Classes.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;

public class LoginView {
    private String user;
    public LoginView() {
        this.user = null;
        body();
    }

    private void body() {
        JFrame loginView = new JFrame("Whack-A-Mole Login");
//        loginView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel(new GridLayout(2, 1));
        JLabel usernameLabel = new JLabel("Usuario:");
        JTextField usernameTextField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);

        JButton submitButton = new JButton("Entrar");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                loginController.validateUsername(usernameTextField.getText());
                validateUsername(usernameTextField.getText());
                loginView.dispose();
            }
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

            System.out.println("Before ins");

            int moveSocketPort = (int) in.readObject();
            String activeMQURL = (String) in.readObject();
            String monsterQueue = (String) in.readObject();
            String winnerTopic = (String) in.readObject();
            int rows = (int) in.readObject();
            int columns = (int) in.readObject();
            Player player = (Player) in.readObject();
            Set<Player> juegoActual = (Set<Player>) in.readObject();

            System.out.println("After ins");

            new GameView(rows, columns, player, moveSocketPort, activeMQURL, monsterQueue, winnerTopic, juegoActual);

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
        LoginView lv = new LoginView();
        LoginView lv2 = new LoginView();
    }

}

