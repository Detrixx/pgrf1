package rasterize;

import model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon){
        for (int i = 0; polygon.getPoint(i) ==null ; i++) {
            int x1 = polygon.getPoint(i).getX();
            int y1 = polygon.getPoint(i).getY();
            int x2 = polygon.getPoint(i+1).getX();
            int y2 = polygon.getPoint(i+1).getY();

            //lineRasterizer.rasterize();



            //lineRasterizer.rasterize(polygon.getPoint(i).getX(),);
        }

    }
}
