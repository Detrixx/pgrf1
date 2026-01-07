package rasterize;

import raster.RasterBufferedImage;
import java.awt.*;

public class LineRasterizerColorTransition extends LineRasterizer {

    private final Color colorStart = Color.RED;
    private final Color colorEnd = Color.BLUE;

    public LineRasterizerColorTransition(RasterBufferedImage image) {
        super(image);
    }

    /**
     * Rasterizace úsečky pomocí rovnice přímky.
     * Podle toho, jestli je úsečka víc vodorovná nebo svislá,
     * iteruju po ose X nebo Y a dopočítává druhou souřadnici.
     * + obsahuje barevný přechod
     *
     * Výhody:
     * - je jednoduchý na implementaci
     * - funguje pro všechny sklony
     *
     * Nevýhody:
     * - používá float, takže není tak přesný ani rychlý jako Bresenham
     * - má menší nepřesnosti kvůli zaokrouhlování
     */

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, Color ignored) {

        //ošetření aby nešla udělat úsečka ve stejném pixelu na začátku a konci
        if (x1 == x2 && y1 == y2) {
            return;
        }

        float dx = x2 - x1;
        float dy = y2 - y1;

        if (Math.abs(dx) > Math.abs(dy)) {

            if (x1 > x2) {
                int tmpX = x1; x1 = x2; x2 = tmpX;
                int tmpY = y1; y1 = y2; y2 = tmpY;
            }

            float k = dy / dx;
            float q = y1 - k * x1;

            for (int x = x1; x <= x2; x++) {
                float t = (float)(x - x1) / (float)(x2 - x1);
                int y = Math.round(k * x + q);

                Color c = interpolate(colorStart, colorEnd, t);
                raster.setPixel(x, y, c.getRGB());
                raster.blurP(x, y, c.getRGB());
            }

        } else {

            if (y1 > y2) {
                int tmpX = x1; x1 = x2; x2 = tmpX;
                int tmpY = y1; y1 = y2; y2 = tmpY;
            }

            float k = dx / dy;
            float q = x1 - k * y1;

            for (int y = y1; y <= y2; y++) {
                float t = (float)(y - y1) / (float)(y2 - y1);
                int x = Math.round(k * y + q);

                Color c = interpolate(colorStart, colorEnd, t);
                raster.setPixel(x, y, c.getRGB());
                raster.blurP(x, y, c.getRGB());
            }
        }
    }

    //dve barvy
    private Color interpolate(Color c1, Color c2, float t) {
        int r = (int)((1 - t) * c1.getRed()   + t * c2.getRed());
        int g = (int)((1 - t) * c1.getGreen() + t * c2.getGreen());
        int b = (int)((1 - t) * c1.getBlue()  + t * c2.getBlue());
        return new Color(r, g, b);
    }
}
