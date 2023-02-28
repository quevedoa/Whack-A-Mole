package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginView {
    private String user;
    public LoginView() {
        this.user = null;
        body();
    }

    private void body() {
        JFrame loginView = new JFrame("Whack-A-Mole Login");
        loginView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
            out.writeUTF(username);

            s.close();

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
    }

    public static void main(String args[]) {
        LoginView lv = new LoginView();
    }

}

