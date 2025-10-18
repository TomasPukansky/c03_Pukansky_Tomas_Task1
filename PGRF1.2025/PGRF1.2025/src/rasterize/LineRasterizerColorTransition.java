package rasterize;

import raster.RasterBufferedImage;

import java.awt.*;

public class LineRasterizerColorTransition extends LineRasterizer {

    public LineRasterizerColorTransition(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        // TODO: pozor na dělení nulou
        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        // TODO: x1 může být větší než x2

        Color c1 = Color.RED;
        Color c2 = Color.GREEN;

        float[] colorComponentsC1 = c1.getColorComponents(null);

        for (int x = x1; x <= x2; x++) {
            // t = odečtu minimum, dělím rozsahem

            for (int i = 0; i < 3; i++) {
                // newColors[i] =
            }

            int y = Math.round(k * x + q);
            raster.setPixel(x, y, 0xff0000);
        }

        // TODO: dokončit algoritmus
    }
}
