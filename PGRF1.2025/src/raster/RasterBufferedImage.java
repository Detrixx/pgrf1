package raster;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBufferedImage implements Raster{

    private BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        //kontrola pro pixel mimo obrazovku
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return;
        }
        image.setRGB(x, y, color);
    }


    @Override
    public int getPixel(int x, int y) {
        //todo druha uloha
        return 0;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        //g.setColor(new Color(Color));
        g.clearRect(0, 0, image.getWidth(), image.getHeight());

    }

    public BufferedImage getImage() {
        return image;
    }


    public void blurP(int x, int y, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int r2 = r / 2;
        int g2 = g / 2;
        int b2 = b / 2;

        int c2 = (r2 << 16) | (g2 << 8) | b2;

        setPixel(x + 1, y, c2);
        setPixel(x - 1, y, c2);
        setPixel(x, y + 1, c2);
        setPixel(x, y - 1, c2);
    }

}
