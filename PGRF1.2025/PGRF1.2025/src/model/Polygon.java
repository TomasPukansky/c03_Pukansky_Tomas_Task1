package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private final List<Point> points;


    public Polygon(){
        this.points = new ArrayList<>();
    }

    public void addPoint(Point p){
        this.points.add(p);
    }

    public Point getPoint(int index){
        return this.points.get(index);
    }

    public int getSize(){
        return points.size();
    }

}
