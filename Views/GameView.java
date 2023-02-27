package Views;

import Controllers.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class GameView {
    public GameView() {
        GameController controller = new GameController();

        JFrame frame = new JFrame();
        // Set the title and size of the window
        frame.setTitle("Whack-A-Mole");
        frame.setSize(500, 500);

        // Create a panel to hold the checkboxes
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        // Create a panel for the label
        JPanel labelPanel = new JPanel();
        JLabel resultLabel = new JLabel("");
        labelPanel.add(resultLabel);

        // Create the checkboxes and add them to the panel
        JCheckBox[] checkboxes = new JCheckBox[9];
        for (int i = 0; i < 9; i++) {
            checkboxes[i] = new JCheckBox("Hole" + (i+1));
            checkboxes[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    JCheckBox checkBox = (JCheckBox) e.getItem();
                    if (checkBox.isSelected()) {
                        System.out.println(checkBox.getText() + " is checked");
                        controller.sendMove();
                        checkBox.setSelected(false);
                        resultLabel.setText("Sent Move");
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
        frame.add(panel, BorderLayout.CENTER);
        frame.add(labelPanel, BorderLayout.SOUTH);

        // Show the window
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        GameView checkboxGrid = new GameView();
    }
}
