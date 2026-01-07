package model;

import java.awt.*;

public class Line {
    private int x1,x2;
    private int y1,y2;
    private int color;

    public Line(int x1, int x2, int y1, int y2, int color) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    public Line(Point p1, Point p2,int color) {
        this.x1 = p1.getX();
        this.x2 = p2.getX();
        this.y1 = p1.getY();
        this.y2 = p2.getY();
    }

    public void setX1(int x) { this.x1 = x; }
    public void setY1(int y) { this.y1 = y; }
    public void setX2(int x) { this.x2 = x; }
    public void setY2(int y) { this.y2 = y; }


    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }
}
