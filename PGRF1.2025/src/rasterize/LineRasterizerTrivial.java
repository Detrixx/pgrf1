package rasterize;

import model.Line;
import raster.RasterBufferedImage;

import java.awt.*;

public class LineRasterizerTrivial extends LineRasterizer {
    public LineRasterizerTrivial(RasterBufferedImage image) {
        super(image);
    }

    /**
     * Rasterizace úsečky pomocí rovnice přímky.
     * Podle toho, jestli je úsečka víc vodorovná nebo svislá,
     * iteruju po ose X nebo Y a dopočítává druhou souřadnici.
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
    public void rasterize(int x1, int y1, int x2, int y2, Color color) {

        //ošetření aby nešla udělat úsečka ve stejném pixelu na začátku a konci
        if (x1 == x2 && y1 == y2) {
            return;
        }

        float k,q;

        if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
            k = (y2-y1)/ (float)(x2-x1);
            q = y1 - k * x1;

            if (x1 > x2) {
                int tmpX = x1;
                x1 = x2;
                x2 = tmpX;
            }

            for (int i=x1;i<=x2;i++){
                int y = Math.round( k * i + q);
                raster.setPixel(i, y, color.getRGB());

            }

        }
        else{

            k = (x2-x1)/ (float)(y2-y1);
            q = x1 - k * y1;

            if (y1 > y2) {
                int tmpY = y1;
                y1 = y2;
                y2 = tmpY;
            }


            for (int i=y1;i<=y2;i++){
                int x = Math.round(k * i + q);

                raster.setPixel(x, i, color.getRGB());
            }
        }

    }
}
