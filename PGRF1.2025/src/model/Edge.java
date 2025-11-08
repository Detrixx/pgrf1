package model;

public class Edge {
    private int x1, y1, x2, y2;

    public Edge(Point p1, Point p2) {
        this.x1 = p1.getX();
        this.y1 = p1.getY();
        this.x2 = p2.getX();
        this.y2 = p2.getY();
    }

    public boolean isHorizontal() {
        return y1 == y2;
    }

    public void orientate() {
        // Prohodím vrcholy (y a x)
        if (y1 > y2) {
            int tmpX = x1;
            int tmpY = y1;
            x1 = x2;
            y1 = y2;
            x2 = tmpX;
            y2 = tmpY;
        }
    }

    public boolean isIntersection(int y) {
        return y1 <= y && y < y2;
    }

    public int getIntersection(int y) {
        // spočítat průsečík
        //if (y2 == y1) return ;
        if (y2 == y1) return x1;
        double t = (double)(y - y1) / (y2 - y1);
        return (int)Math.round(x1 + t * (x2 - x1));
    }

    public int getX1() { return x1; }
    public int getX2() { return x2; }
    public int getY1() { return y1; }
    public int getY2() { return y2; }
}
