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
    Map<Pair<Integer, Integer>, Boolean> visited = new HashMap<>();
    Integer yLimit;
    Integer distanceParameter;
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
        clearButton.setBounds(240, 0, 100, 50);
        randomButton.setBounds(360, 0, 150, 50);
        runButton.setBounds(0, 60, 100, 50);
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
        //        Run the game - DBSCAN
        runButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String distanceString = JOptionPane.showInputDialog(frame, "Enter the distance parameter.", null);
                distanceParameter = Integer.valueOf(distanceString);
                try {
                    connectPoints();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        frame.addMouseListener(new EventListeners(frame, points, yLimit));
        frame.add(buttonPanel, BorderLayout.NORTH);
    }
    
        private void connectPoints() throws InterruptedException {
        Graphics g = frame.getGraphics();
        Random random = new Random();
        Integer numberOfPoints = points.size();
        while (numberOfPoints != visited.size() && points.size() > 0) {
            Integer startIndex = random.nextInt(points.size());
            Pair<Integer, Integer> startPoint = points.get(startIndex);
            Queue<Pair<Integer, Integer>> q = new LinkedList<>();
            q.add(startPoint);
            visited.put(startPoint, true);
            while (!q.isEmpty()) {
                Pair<Integer, Integer> front = q.remove();
                points.remove(front);
                points.forEach(point -> {
                    if(!visited.containsKey(point)) {
                        Double euclideanDist = findEuclideanDistance(front, point);
                        System.out.println(euclideanDist);
                        if(euclideanDist <= distanceParameter) {
                            q.add(point);
                            visited.put(point, true);
//                            System.out.println("Dist Param");
//                            System.out.println(distanceParameter);
                            //Adding OVAL_RADIUS to make the line look like it is originating from center
                            g.drawLine(front.getX() + EventListeners.OVAL_RADIUS, front.getY() + EventListeners.OVAL_RADIUS,
                                    point.getX() + EventListeners.OVAL_RADIUS, point.getY() + EventListeners.OVAL_RADIUS);
                        }
                    }
                });
            }
            Thread.sleep(1000);
        }
    }

    private Double findEuclideanDistance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        Integer t1 = p1.getX() - p2.getX(), t2 = p1.getY() - p2.getY();
        return Math.sqrt(t1*t1 + t2 * t2);
    }

    public void addPointToCanvas(Graphics g, Pair<Integer, Integer> point) {
        g.fillOval(point.getX(), point.getY(), 2*EventListeners.OVAL_RADIUS, 2*EventListeners.OVAL_RADIUS);
    }
}
