package raster;

public interface Raster {
    void setPixel(int x, int y , int color);
    void getPixel(int x, int y);
    int getWidth();
    int getHeight();
    void clear();
}
