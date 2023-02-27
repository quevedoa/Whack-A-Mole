package Views;

import Controllers.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView {
    private String user;
    public LoginView() {
        this.user = null;
        body();
    }

    private void body() {
        LoginController loginController = new LoginController();

        JFrame loginView = new JFrame("Whack-A-Mole Login");
        loginView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel(new GridLayout(2, 1));
        JLabel usernameLabel = new JLabel("Usuario:");
        JTextField usernameTextField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);

        JPanel responsePanel = new JPanel(new GridLayout(2, 1));
        JLabel responseLabel = new JLabel();
        responsePanel.add(responseLabel);

//        JPanel ipPanel = new JPanel(new GridLayout(2, 1));
//        JLabel ipLabel = new JLabel("IP:");
//        JTextField ipTextField = new JTextField(20);
//        ipPanel.add(ipLabel);
//        ipPanel.add(ipTextField);

        JButton submitButton = new JButton("Entrar");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitButton.setEnabled(false);
                responseLabel.setText("");
                String response = loginController.validateUsername(usernameTextField.getText());
                responseLabel.setText(response);
                submitButton.setEnabled(true);
            }
        });

        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitPanel.add(submitButton);

        panel.add(usernamePanel);
//        panel.add(ipPanel);
        panel.add(submitPanel);

        loginView.add(panel);

        loginView.setSize(500,250);
        loginView.setLocationRelativeTo(null);
        loginView.setVisible(true);
    }

//    public static void main(String args[]) {
//        Login lv = new Login();
//    }

}

