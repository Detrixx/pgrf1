package controller;

import clip.Clipper;
import fill.Filler;
import fill.ScanLineFiller;
import fill.SeedFiller;
import model.Line;
import model.Point;
import model.Polygon;
import model.RectanglePolygon;
import rasterize.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller2D {

    private final Panel panel;
    private final LineRasterizer lineRasterizer;

    private final ArrayList<Line> lines = new ArrayList<>();
    private Polygon polygon = new Polygon();

    private Polygon polygonClipper = new Polygon();
    private Polygon clippedPolygon = null;

    private Point start;
    private Point end;

    private Color currentColor = Color.RED;
    private boolean drawLineMode = true;
    private boolean drawPolygonMode = false;
    private boolean straightLineMode = false;

    private boolean drawClipperMode = false;

    private boolean drawRectangleMode = false;
    private Point rectA = null;
    private Point rectB = null;
    private RectanglePolygon previewRect = null;

    private List<Polygon> rectPolygons = new ArrayList<>();

    private Filler filler;
    private Point seedFillStart;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        initListeners();
    }

    private void initListeners() {

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int x = e.getX();
                int y = e.getY();
                if (!isInside(x, y)) return;

                if (drawRectangleMode) {
                    if (rectA == null) {
                        rectA = new Point(x, y);
                    } else if (rectB == null) {
                        rectB = new Point(x, y);
                    }
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {

                    if (drawClipperMode) {
                        polygonClipper.addPoint(new Point(x, y));
                    }
                    else if (drawPolygonMode) {
                        polygon.addPoint(new Point(x, y));
                    }
                    else if (drawLineMode) {
                        start = new Point(x, y);
                    }

                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    seedFillStart = new Point(x, y);
                }

                redraw();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (drawRectangleMode && rectA != null && rectB != null) {

                    int x = e.getX();
                    int y = e.getY();
                    Point third = new Point(x, y);

                    RectanglePolygon rp = new RectanglePolygon(rectA, rectB, third);
                    for (Point p : rp.getPoints()) polygon.addPoint(p);

                    rectPolygons.add(polygon);
                    polygon = new Polygon();
                    //polygon.clear();

                    rectA = null;
                    rectB = null;
                    previewRect = null;

                    redraw();
                    return;
                }

                if (!SwingUtilities.isLeftMouseButton(e)) return;
                int x = e.getX();
                int y = e.getY();

                if (!drawLineMode || start == null || !isInside(x, y)) return;

                if (straightLineMode) {
                    end = makeAlignedPoint(start.getX(), start.getY(), x, y);
                } else {
                    end = new Point(x, y);
                }

                lines.add(new Line(start, end, currentColor.getRGB()));
                redraw();
            }
        });


        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                if (drawRectangleMode && rectA != null && rectB != null) {

                    int x = e.getX();
                    int y = e.getY();

                    previewRect = new RectanglePolygon(rectA, rectB, new Point(x, y));

                    redraw();
                    return;
                }

                if (!drawLineMode || start == null || !SwingUtilities.isLeftMouseButton(e)){
                    return;
                }

                int x = e.getX();
                int y = e.getY();

                if (!isInside(x, y)){
                    return;
                }

                panel.getRaster().clear();
                drawAll();

                if (straightLineMode) {
                    end = makeAlignedPoint(start.getX(), start.getY(), x, y);
                } else {
                    end = new Point(x, y);
                }

                lineRasterizer.rasterize(start, end, currentColor);
                panel.repaint();
            }
        });

        //listener na klávesy
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_Q -> switchMode("line");
                    case KeyEvent.VK_W -> switchMode("polygon");
                    case KeyEvent.VK_E -> chooseColor();
                    case KeyEvent.VK_C -> clear();
                    case KeyEvent.VK_F -> fillPolygon();
                    case KeyEvent.VK_SHIFT -> straightLineMode = true;

                    case KeyEvent.VK_X -> {
                        drawClipperMode = !drawClipperMode;
                        drawPolygonMode = false;
                        drawLineMode = false;
                        drawRectangleMode = false;
                    }

                    case KeyEvent.VK_R -> {
                        drawRectangleMode = true;
                        drawClipperMode = false;
                        drawPolygonMode = false;
                        drawLineMode = false;
                        rectA = null;
                        rectB = null;
                        previewRect = null;
                    }

                    case KeyEvent.VK_T -> {
                        doClip();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    straightLineMode = false;
                }
            }
        });
    }


    private void doClip() {
        Clipper clipper = new Clipper();
        clippedPolygon = clipper.clip(polygonClipper, polygon);
        redraw();
    }


    //zmena promene
    private void switchMode(String mode) {
        drawLineMode = mode.equals("line");
        drawPolygonMode = mode.equals("polygon");

        drawRectangleMode = false;
        drawClipperMode = false;
    }


    //vyber barvy
    private void chooseColor() {
        Color s = JColorChooser.showDialog(panel, "Vyber barvu", currentColor);
        if (s != null) {
            currentColor = s;
        }
    }


    private Point makeAlignedPoint(int x1, int y1, int x2, int y2) {

        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);

        if (dx < dy) {
            return new Point(x1, y2);
        } else if (dy < dx) {
            return new Point(x2, y1);
        }
        else{
            return null;}
    }

    private boolean isInside(int x, int y) {
        return x >= 0 && y >= 0 && x < panel.getWidth() && y < panel.getHeight();
    }


    //mazani platna
    private void clear(){
        panel.repaint();
        lines.clear();
        polygonClipper = new Polygon();
        polygon.getPoints().clear();
        clippedPolygon = null;

        rectA = null;
        rectB = null;
        previewRect = null;
        drawRectangleMode = false;
        rectPolygons.clear();

        redraw();
    }


    private void fillPolygon() {
        Filler filler = new ScanLineFiller(
                new LineRasterizerTrivial(panel.getRaster()),
                new PolygonRasterizer(new LineRasterizerTrivial(panel.getRaster())),
                polygon
        );
        filler.fill();
        panel.repaint();
    }


    private void redraw() {
        panel.getRaster().clear();
        drawAll();

        if (previewRect != null) {
            drawPolygon(previewRect, Color.BLUE);
        }

        if(seedFillStart != null){
            filler = new SeedFiller(panel.getRaster(),seedFillStart.getX(),seedFillStart.getY(),0x00ff00);
            filler.fill();
        }

        panel.repaint();
    }


    private void drawAll() {

        for (Line l : lines) {
            lineRasterizer.rasterize(l, currentColor);
        }
        for (Polygon p: rectPolygons){
            drawPolygon(p, Color.RED);
        }


        drawPolygon(polygon, Color.RED);
        drawPolygon(polygonClipper, Color.MAGENTA);

        if (clippedPolygon != null)
            drawPolygon(clippedPolygon, Color.GREEN);
    }


    private void drawPolygon(Polygon poly, Color color) {
        if (poly.getSize() < 2) return;

        for (int i = 0; i < poly.getSize() - 1; i++) {
            Point p1 = poly.getPoint(i);
            Point p2 = poly.getPoint(i + 1);
            lineRasterizer.rasterize(p1, p2, color);
        }

        Point first = poly.getPoint(0);
        Point last = poly.getPoint(poly.getSize() - 1);
        lineRasterizer.rasterize(last, first, color);
    }
}
