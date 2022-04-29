import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class GameScreen {

    JFrame frame = new JFrame();
    JPanel buttonPanel = new JPanel();
    JButton loadButton, saveButton, randomButton, clearButton, runButton;
    List<Pair<Integer, Integer>> points = new ArrayList<>();
    Map<Pair<Integer, Integer>, Boolean> randomPoints = new HashMap<>();
    Integer yLimit;
    private static final Integer GRID_DIM = 600;
    private final static Integer RANDOM_POINTS = 100;

    GameScreen() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(GRID_DIM, GRID_DIM);
        frame.setTitle("Dot Game");
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);

        buttonPanel.setLayout(null);
        buttonPanel.setBounds(0, 0, GRID_DIM, 120);
        yLimit = 150;
        buttonPanel.setBackground(Color.BLACK);

        loadButton = new JButton("Load");
        saveButton = new JButton("Save");
        randomButton = new JButton("Randomize");
        clearButton = new JButton("Clear");
        runButton = new JButton("Run");

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
        
 //        Action for load button
        loadButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
                FileFilter filter = new FileNameExtensionFilter("CSV files","csv");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
//                    System.out.println("You chose to open this file: " + fileChooser.getSelectedFile().getName());
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        Scanner fileScanner =  new Scanner(selectedFile);
                        fileScanner.useDelimiter("\n");
                        while (fileScanner.hasNext()) {
                            String line = fileScanner.next();
                            String[] stringCoordinates = line.split(",");
                            Integer xPos = Integer.valueOf(stringCoordinates[0]);
                            Integer yPos = Integer.valueOf(stringCoordinates[1]);
                            points.add(new Pair<>(xPos, yPos));
                            addPointToCanvas(frame.getGraphics(), new Pair<>(xPos, yPos));
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        
 //        Action for save button
        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent clickEvent) {
//                System.out.println("Save button clicked.");
                File pointsCsv = new File("points.csv");
                try {
                    PrintWriter outfile = new PrintWriter(pointsCsv);
                    points.forEach(point -> {
                        outfile.write(point.getX().toString() + ',' + point.getY().toString());
                        outfile.write('\n');
                    });
                    outfile.close();
//                    System.out.println("File saved.");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

//        Random points generator
        randomButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Integer pointsAdded = 0;
                while (pointsAdded < RANDOM_POINTS) {
                    Random random = new Random();
                    Integer xPos = random.ints(0, GRID_DIM).findFirst().getAsInt();
                    Integer yPos = random.ints(yLimit + 1, GRID_DIM).findFirst().getAsInt();
                    Pair<Integer, Integer> randomPoint = new Pair<>(xPos, yPos);
                    if(yPos - EventListeners.OVAL_RADIUS <= yLimit || yPos + 2* EventListeners.OVAL_RADIUS > GRID_DIM
                        || xPos - EventListeners.OVAL_RADIUS <= 0 || xPos + 2* EventListeners.OVAL_RADIUS > GRID_DIM
                        || randomPoints.containsKey(randomPoint)) {
                        continue;
                    } else {
                        addPointToCanvas(frame.getGraphics(), randomPoint);
                        randomPoints.put(randomPoint, true);
                        points.add(randomPoint);
                        pointsAdded += 1;
                    }
                }
            }
        });

//        Clear the canvas
        clearButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.getGraphics().clearRect(0, yLimit + 2*EventListeners.OVAL_RADIUS, GRID_DIM, GRID_DIM - yLimit);
                points.clear();
                randomPoints.clear();
            }
        });

        frame.addMouseListener(new EventListeners(frame, points, yLimit));
        frame.add(buttonPanel, BorderLayout.NORTH);
    }

    public void addPointToCanvas(Graphics g, Pair<Integer, Integer> point) {
        g.fillOval(point.getX(), point.getY(), 2*EventListeners.OVAL_RADIUS, 2*EventListeners.OVAL_RADIUS);
    }
}
