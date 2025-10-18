package rasterize;

import model.Polygon;
import model.Point;

public class PolygonRasterizer {

    int size = 0;
    private LineRasterizer  lineRasterizer;
    private PolygonRasterizer(LineRasterizer lineRasterizer){
        this.lineRasterizer = lineRasterizer;
    };

    public void rasterize(Polygon polygon){
       //kontrola ci mame aspon 3 pointy,,,,, ak na 2 pointy osetrit ze usecku nevykresli dvakrat

       //forcyklus for i=0,i<size,i++
        // i=0  p0 -> i+1=1 p1

        for (int i = 0; i < polygon.getSize(); i++){
            int indexA =1;
            int indexB= i+1;


            // If indexB se rovná polygon.getSize
            // pokud ano, tak indexB = 0

            Point pA = polygon.getPoint(indexA);
            Point pB = polygon.getPoint(indexB);
            if(i==4){


                //lineRasterizer.rasterize

                // TODO: dodělat
            }
        }



    }

    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }
}
