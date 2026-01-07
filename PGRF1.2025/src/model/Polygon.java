package model;


import java.util.ArrayList;

public class Polygon {
    ArrayList<Point> points;
    public Polygon(){
        this.points = new ArrayList<>();
    }


    public void addPoint(Point p){
        points.add(p);
    }
    public Point getPoint(int index){
        return points.get(index);
    }


    public int lastIndex() {
        return points.size() - 1;
    }
    public void setPoint(int index, Point p) {
        points.set(index, p);
    }

    public void clear() {
        points.clear();
    }

    public void removePoint(int index) {
        points.remove(index);
    }



}
