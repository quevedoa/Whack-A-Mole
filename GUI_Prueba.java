
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class GUI_Prueba extends JFrame {

    public GUI_Prueba() {
        // Set the title and size of the window
        setTitle("Checkbox Grid");
        setSize(500, 500);

        // Create a panel to hold the checkboxes
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

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
                    } else {
                        System.out.println(checkBox.getText() + " is not checked");
                    }
                }
            });

            JPanel checkboxPanel = new JPanel();
            checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
            checkboxPanel.add(checkboxes[i]);
            checkboxPanel.add(new JLabel());

            panel.add(checkboxPanel);
        }

        // Create a panel for the label
        JPanel labelPanel = new JPanel();
        labelPanel.add(new JLabel("Label Text"));

        // Add the panels to the window with a BorderLayout
        add(panel, BorderLayout.CENTER);
        add(labelPanel, BorderLayout.SOUTH);

        // Show the window
        setVisible(true);
    }

    public static void main(String[] args) {
        GUI_Prueba checkboxGrid = new GUI_Prueba();
    }
}

