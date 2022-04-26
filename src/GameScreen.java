import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GameScreen {

    JFrame frame = new JFrame();
    JPanel buttonPanel = new JPanel();
    List<Pair<Integer, Integer>> points = new ArrayList<>();
    Integer yLimit;

    GameScreen() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setTitle("Dot Game");
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);

        buttonPanel.setLayout(null);
        buttonPanel.setBounds(0, 0, 800, 120);
        yLimit = 150;
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

        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent clickEvent) {
                System.out.println("Save button clicked.");
                File pointsCsv = new File("points.csv");
                try {
                    PrintWriter outfile = new PrintWriter(pointsCsv);
                    points.forEach(point -> {
                        outfile.write(point.getX().toString() + ',' + point.getY().toString());
                        outfile.write('\n');
                    });
                    outfile.close();
                    System.out.println("File saved.");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        frame.addMouseListener(new EventListeners(frame, points, yLimit));

        frame.add(buttonPanel, BorderLayout.NORTH);
    }
}
