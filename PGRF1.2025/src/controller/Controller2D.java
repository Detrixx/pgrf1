package controller;

import model.Line;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizeGraphics;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerColorTransition;
import rasterize.LineRasterizerTrivial;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controller2D {

    private final Panel panel;
    private final LineRasterizer lineRasterizer;

    private final ArrayList<Line> lines = new ArrayList<>();
    private final Polygon polygon = new Polygon();

    private Point start;
    private Point end;

    private Color currentColor = Color.RED;
    private boolean drawLineMode = true;
    private boolean drawPolygonMode = false;
    private boolean straightLineMode = false;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        //this.lineRasterizer = new LineRasterizerColorTransition(panel.getRaster());

        //this.lineRasterizer = new LineRasterizeGraphics(panel.getRaster());

        initListeners();
    }

    private void initListeners() {

        //listener pro mys
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (!isInside(x, y)) return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (drawPolygonMode) {
                        polygon.addPoint(new Point(x, y));
                    } else if (drawLineMode) {
                        start = new Point(x, y);
                    }
                }

                redraw();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (!drawLineMode || start == null || !isInside(x, y)) return;

                if (straightLineMode) {
                    end = makeAlignedPoint(start.getX(), start.getY(), x, y);
                } else {
                    end = new Point(x, y);
                }


                //lines.add(start,end,color);
                lines.add(new Line(start, end, currentColor.getRGB()));
                redraw();
            }
        });


        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!drawLineMode || start == null || !SwingUtilities.isLeftMouseButton(e)){
                return;
                }

                int x = e.getX();
                int y = e.getY();

                if (!isInside(x, y)){
                    return;
                };

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
                    case KeyEvent.VK_SHIFT -> straightLineMode = true;
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

    private void switchMode(String mode) {
        drawLineMode = mode.equals("line");
        drawPolygonMode = mode.equals("polygon");
    }

    private void chooseColor() {
        Color s = JColorChooser.showDialog(panel, "Vyber barvu", currentColor);
        if (s != null) {
            currentColor = s;
        }
    }

    //zarovna primky
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


    //smazání
    private void clear(){
        panel.repaint();
        lines.clear();
        redraw();
    }

    //Vykreslení
    private void redraw() {
        panel.getRaster().clear();
        drawAll();
        panel.repaint();
    }

    private void drawAll() {
        //primky
        for (Line l : lines) {
            lineRasterizer.rasterize(l, currentColor);
        }

        //polygon
        if (polygon.lastIndex() > 0) {

            for (int i = 0; i < polygon.lastIndex(); i++) {
                Point p1 = polygon.getPoint(i);
                Point p2 = polygon.getPoint(i + 1);
                lineRasterizer.rasterize(p1, p2, currentColor);
            }

            Point last = polygon.getPoint(polygon.lastIndex());
            Point first = polygon.getPoint(0);
            lineRasterizer.rasterize(last, first, currentColor);
        }

    }
}
