package rasterize;

import raster.RasterBufferedImage;

import java.awt.*;

public class LineRasterizerColorTransition extends LineRasterizer {
    public LineRasterizerColorTransition(RasterBufferedImage image) {
        super(image);
    }



    @Override
    public void rasterize(int x1, int y1, int x2, int y2, Color color) {

//        for(int x = x1; x <= x2 ; x++) {
//            //t = odečtu min, dělím rozsahem
//            for(int i=0;k<3;i++){
//                // newColors[i] = (1-t) * c1 + t * c2;
//            }
//
//            int y = Math.round( k * x + q);
//            raster.setPixel(x,y, 0xff0000);
//        }

        float k,q;
        Color c1 = Color.RED;
        Color c2 = Color.BLUE;

        float[] colorComponentsC1= c1.getColorComponents(null);

        if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
            k = (y2-y1)/ (float)(x2-x1);
            q = y1 - k * x1;

            if (x1 > x2) {
                int tmpX = x1;
                x1 = x2;
                x2 = tmpX;
            }

            for (int i=x1;i<=x2;i++){
//                //t = odečtu min, dělím rozsahem
//            for(int i=0;k<3;i++){
//                //newColors[i] = (1-t) * c1 + t * c2;
//            }
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
