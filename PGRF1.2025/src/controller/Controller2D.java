package controller;

import model.Line;
import model.LineAligner;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerColorTransition;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controller2D {

    private final Panel panel;
    private final LineRasterizer lineRasterizer;

    private final LineAligner lineAligner = new LineAligner();

    private final ArrayList<Line> lines = new ArrayList<>();
    private final Polygon polygon = new Polygon();

    private Point start;
    private Point end;

    private Color currentColor = Color.RED;
    private boolean drawLineMode = true;
    private boolean drawPolygonMode = false;
    private boolean straightLineMode = false;

    private int selectedVertex = -1;

    private Line selectedLine = null;
    private boolean editingStart = false;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.lineRasterizer = new LineRasterizerColorTransition(panel.getRaster());
        initListeners();
    }

    private void initListeners() {

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int x = e.getX();
                int y = e.getY();

                //smazání vrcholu (rightclick)
                if (drawPolygonMode && SwingUtilities.isRightMouseButton(e)) {
                    int idx = findVertex(x, y);
                    if (idx != -1) {
                        polygon.removePoint(idx);
                        redraw();
                        return;
                    }
                }


                //editace polygonu
                if (drawPolygonMode && SwingUtilities.isLeftMouseButton(e)) {
                    int idx = findVertex(x, y);
                    if (idx != -1) {
                        selectedVertex = idx;
                        return;
                    }
                }

                //editace přímek
                if (drawLineMode && SwingUtilities.isLeftMouseButton(e)) {
                    Line l = findLineEndpoint(x, y);
                    if (l != null) {
                        selectedLine = l;
                        return;
                    }
                }

                if (!isInside(x, y)) return;


                //kreslení polygonu a přímek
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (drawPolygonMode) {
                        if (selectedVertex == -1) {
                            polygon.addPoint(new Point(x, y));
                        }
                    }
                    else if (drawLineMode) {
                        start = new Point(x, y);
                    }
                }

                redraw();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedVertex = -1;
                selectedLine = null;

                int x = e.getX();
                int y = e.getY();

                if (!drawLineMode || start == null || !isInside(x, y)) return;

                if (straightLineMode) {
                    end = lineAligner.align(start, new Point(x, y));
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
                int x = e.getX();
                int y = e.getY();


                if (drawPolygonMode && selectedVertex != -1) {
                    polygon.setPoint(selectedVertex, new Point(x, y));
                    redraw();
                    return;
                }

                if (drawLineMode && selectedLine != null) {
                    if (editingStart) {
                        selectedLine.setX1(x);
                        selectedLine.setY1(y);
                    } else {
                        selectedLine.setX2(x);
                        selectedLine.setY2(y);
                    }
                    redraw();
                    return;
                }

                if (!drawLineMode || start == null || !SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                if (!isInside(x, y)) {
                    return;
                }

                panel.getRaster().clear();
                drawAll();

                if (straightLineMode) {
                    end = lineAligner.align(start, new Point(x, y));
                } else {
                    end = new Point(x, y);
                }

                lineRasterizer.rasterize(start, end, currentColor);
                panel.repaint();
            }
        });

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


    //najde vrchol polygonu (v okolí 6 pixelů)
    private int findVertex(int x, int y) {
        for (int i = 0; i <= polygon.lastIndex(); i++) {
            Point p = polygon.getPoint(i);
            if (Math.abs(p.getX() - x) < 6 && Math.abs(p.getY() - y) < 6) {
                return i;
            }
        }
        return -1;
    }


    //najde endpoint přímky (v okolí 6 pixelů)
    private Line findLineEndpoint(int x, int y) {
        for (Line l : lines) {
            if (Math.abs(l.getX1() - x) < 6 && Math.abs(l.getY1() - y) < 6) {
                editingStart = true;
                return l;
            }
            if (Math.abs(l.getX2() - x) < 6 && Math.abs(l.getY2() - y) < 6) {
                editingStart = false;
                return l;
            }
        }
        return null;
    }

    private void switchMode(String mode) {
        drawLineMode = mode.equals("line");
        drawPolygonMode = mode.equals("polygon");
    }

    private void chooseColor() {
        Color s = JColorChooser.showDialog(panel, "Vyber barvu", currentColor);
        if (s != null) currentColor = s;
    }

    private boolean isInside(int x, int y) {
        return x >= 0 && y >= 0 && x < panel.getWidth() && y < panel.getHeight();
    }

    private void clear() {
        lines.clear();
        polygon.clear();
        redraw();
    }


    private void redraw() {
        panel.getRaster().clear();
        drawAll();
        panel.repaint();
    }

    private void drawAll() {
        for (Line l : lines) {
            lineRasterizer.rasterize(l, currentColor);
        }

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
