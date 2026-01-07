package model;

public class LineAligner {

    public Point align(Point start, Point current) {

        int dx = Math.abs(start.getX() - current.getX());
        int dy = Math.abs(start.getY() - current.getY());

        if (dx < dy) {
            return new Point(start.getX(), current.getY());
        } else if (dy < dx) {
            return new Point(current.getX(), start.getY());
        } else {
            return current;
        }
    }
}
