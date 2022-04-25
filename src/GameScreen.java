import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GameScreen {

    JFrame frame = new JFrame();
    JPanel buttonPanel = new JPanel();

    GameScreen() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setTitle("Dot Game");
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);

        buttonPanel.setLayout(null);
        buttonPanel.setBounds(0, 0, 800, 120);
        buttonPanel.setBackground(Color.BLACK);

        JButton loadButton = new JButton("Load");
        JButton saveButton = new JButton("Save");
        JButton randomButton = new JButton("Randomize");
        JButton clearButton = new JButton("Clear");
        JButton runButton = new JButton("Run");

        loadButton.setBounds(0, 0, 100, 50);
        saveButton.setBounds(120, 0, 100, 50);
        randomButton.setBounds(0, 60, 150, 50);
        clearButton.setBounds(360, 0, 100, 50);
        runButton.setBounds(480, 0, 100, 50);
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(randomButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(runButton);

        frame.add(buttonPanel, BorderLayout.NORTH);
    }
}