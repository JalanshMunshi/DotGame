import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EventListeners extends MouseAdapter {

    JFrame frame;
    Graphics g;
    List<Pair<Integer, Integer>> points;
    Integer yLimit;
    public static final int OVAL_RADIUS = 5;
    private static final Integer GRID_DIM = 600;
    public EventListeners(JFrame _frame, List<Pair<Integer, Integer>> _points, Integer _yLimit) {
        this.frame = _frame;
        this.g = _frame.getGraphics();
        this.points = _points;
        this.yLimit = _yLimit;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        Integer xPos = mouseEvent.getX() - OVAL_RADIUS, yPos = mouseEvent.getY() - OVAL_RADIUS;
//        System.out.println(mouseEvent.getY());
//        System.out.println(yPos);
//        System.out.println(yLimit);
        if(yPos - OVAL_RADIUS <= yLimit || yPos + 2*OVAL_RADIUS > GRID_DIM
            || xPos - OVAL_RADIUS <= 0 || xPos + 2*OVAL_RADIUS > GRID_DIM) {
            System.out.println("Dot not added.");
        } else {
            points.add(new Pair<>(xPos, yPos));
            g.fillOval(xPos, yPos, 2*OVAL_RADIUS, 2*OVAL_RADIUS);
        }
    //    points.forEach(point -> {
    //        System.out.println(point.getX());
    //        System.out.println(point.getY());
    //    });
    }
}