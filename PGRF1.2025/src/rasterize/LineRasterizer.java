package rasterize;

import model.Line;
import model.Point;
import raster.RasterBufferedImage;

import java.awt.*;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;
    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
    }
    public void rasterize(int x1, int y1, int x2, int y2, Color color){

    }

    public void rasterize(model.Point p1, Point p2, Color color){
        rasterize(p1.getX(), p1.getY(), p2.getX(), p2.getY(), color);
    }

    public void rasterize(Line line,Color color){
        rasterize(line.getX1(), line.getY1(),line.getX2(), line.getY2(), color);
    }

}
