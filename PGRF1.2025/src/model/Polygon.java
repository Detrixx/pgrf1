package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private final ArrayList<Point> points;

    public Polygon() {
        this.points = new ArrayList<>();
    }

    public Polygon(ArrayList<Point> points) {
        this.points = points;
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public Point getPoint(int index) {
        return points.get(index);
    }

    public int lastIndex() {
        return points.size() - 1;
    }

    public int getSize() {
        return points.size();
    }


    public List<Point> getPoints() {
        return points;
    }

    public void clear() {
        points.clear();
    }
}
