package clip;

import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Clipper {

    public Polygon clip(Polygon clipper, Polygon subject) {
        //TODO dodelat - slide 21.
        // umet spocitat tecna vektor, normalu, vektor k bodu o kterem urcuji, jestli je vlevo nebo vpravo, dot produkt slide 28.
        // vysledkem skalarniho soucinu je uhel, podle znameknka urcim kde je kladné = point je na strane jako normála
        // Vstupní body polygonu k ořezání

        List<Point> outputList = new ArrayList<>();

        for (int i = 0; i < subject.getSize(); i++) {
            outputList.add(subject.getPoint(i));
        }

        if (clipper.getSize() < 3 || subject.getSize() < 3) {
            return new Polygon();
        }

        return new Polygon(new ArrayList<>(outputList));
    }

    private boolean isInside(Point A, Point B, Point P) {
        int skalar = (B.getX() - A.getX()) * (P.getY() - A.getY())
                - (B.getY() - A.getY()) * (P.getX() - A.getX());
        return skalar >= 0;
    }

    private Point getIntersection(Point A, Point B, Point S, Point P) {

        double dx1 = B.getX() - A.getX();
        double dy1 = B.getY() - A.getY();
        double dx2 = P.getX() - S.getX();
        double dy2 = P.getY() - S.getY();

        double d = dx1 * dy2 - dy1 * dx2;

        if (d == 0) {
            return P;
        }

        double t = ((S.getX() - A.getX()) * dy2 - (S.getY() - A.getY()) * dx2) / d;
        double x = A.getX() + t * dx1;
        double y = A.getY() + t * dy1;

        return new Point(x, y);
    }
}
