package model;

public class RectanglePolygon extends Polygon {

    public RectanglePolygon(Point base1, Point base2, Point thirdPoint) {
        super();
        createRectangle(base1, base2, thirdPoint);
    }

    private void createRectangle(Point A, Point B, Point T) {

        double vx = B.getX() - A.getX();
        double vy = B.getY() - A.getY();

        //x2 + y2
        double delka = Math.sqrt(vx * vx + vy * vy);
        double ux = vx / delka;
        double uy = vy / delka;

        double wx = T.getX() - A.getX();
        double wy = T.getY() - A.getY();


        double vyska = (-uy * wx + ux * wy);
        double px = -uy;
        double py = ux;


        px *= vyska;
        py *= vyska;

        Point C = new Point((int) Math.round(B.getX() + px), (int) Math.round(B.getY() + py));
        Point D = new Point((int) Math.round(A.getX() + px), (int) Math.round(A.getY() + py));

        addPoint(A);
        addPoint(B);
        addPoint(C);
        addPoint(D);
    }
}
