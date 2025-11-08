package fill;

import model.Edge;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.PolygonRasterizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class ScanLineFiller implements Filler {
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;
    private final Polygon polygon;

    public ScanLineFiller(LineRasterizer lineRasterizer, PolygonRasterizer polygonRasterizer, Polygon polygon) {
        this.lineRasterizer = lineRasterizer;
        this.polygonRasterizer = polygonRasterizer;
        this.polygon = polygon;
    }

    @Override
    public void fill() {
        // nechci vyplnit polygon, který má méně, jak 3 vrcholy
        if (polygon.getSize() < 3) return;

        // Potřebujeme vytvořit seznam hran
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < polygon.getSize(); i++) {

            int indexA = i;
            int indexB = i + 1;

            if (indexB == polygon.getSize())
                indexB = 0;

            Point a = polygon.getPoint(indexA);
            Point b = polygon.getPoint(indexB);

            Edge edge = new Edge(a, b);
            // Nechceme přidat horizontální hrany
            if (!edge.isHorizontal()) {
                // Nastavím spárvnou orientaci hrany
                edge.orientate();
                // Přidám
                edges.add(edge);
            }
        }

        // Najít yMin a yMax
        int yMin = edges.get(0).getY1();
        int yMax = edges.get(0).getY2();
        // projít všechny pointy polygonu a najít min a amx
        for (Edge edge : edges) {
            if (edge.getY1() < yMin) yMin = edge.getY1();
            if (edge.getY2() > yMax) yMax = edge.getY2();
        }

        for (int y = yMin; y <= yMax; y++) {
            // vytvořím seznam průsečíků
            ArrayList<Integer> intersections = new ArrayList<>();

            // Prokaždou hranu:
            for(Edge edge : edges) {
                // zeptám se, jestli existuje průsečík
                if(!edge.isIntersection(y))
                    continue;
                // pokud ano, tak ho spočítám
                int x = edge.getIntersection(y);
                // uložím do seznamu průsečíků
                intersections.add(x);
            }

            // Seřadit průsečíky od min po max
            Collections.sort(intersections);

            // Spojím (obarvím) průsečíky, 0 - 1, 2 - 3, 4 - 5, 6 - 7
            for (int i = 0; i < intersections.size() - 1; i += 2) {
                int x1 = intersections.get(i);
                int x2 = intersections.get(i + 1);
                lineRasterizer.rasterize(
                        new Point(x1, y),
                        new Point(x2, y),
                        Color.GREEN
                );
            }
        }

        // Vykreslím hranici polygonu
        polygonRasterizer.rasterize(polygon);
    }
}
