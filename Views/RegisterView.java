package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterView {
    public RegisterView() {
        body();
    }

    private void body() {
        JFrame registerView = new JFrame("Whack-A-Mole Register");
        registerView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel(new GridLayout(2, 1));
        JLabel usernameLabel = new JLabel("Porfavor ingrese el nombre de usuario deseado:");
        JTextField usernameTextField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);

        JPanel responsePanel = new JPanel(new GridLayout(2, 1));
        JLabel responseLabel = new JLabel();
        responsePanel.add(responseLabel);

        JButton submitButton = new JButton("Registrar");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                responseLabel.setText("Hello");
            }
        });

        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitPanel.add(submitButton);

        panel.add(usernamePanel);
        panel.add(responsePanel);
        panel.add(submitPanel);

        registerView.add(panel);

        registerView.setSize(500,250);
        registerView.setLocationRelativeTo(null);
        registerView.setVisible(true);
    }

    public static void main(String args[]) {
        RegisterView rv = new RegisterView();
    }
}
