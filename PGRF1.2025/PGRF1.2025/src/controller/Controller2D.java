package controller;


import model.Line;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerColorTransition;
import rasterize.LineRasterizerGraphics;
import rasterize.LineRasterizerTrivial;
import view.Panel;
import model.Point;
import model.Polygon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

//TODO: code-> reformat code
public class Controller2D {
    private final Panel panel;
    private LineRasterizer lineRasterizer;
    private Polygon polygon;
    int color= 0xff0000;
    private Point currentPoint; // v realnom case
    private Point firstPoint;
    private List<Line> lines;


    public Controller2D(Panel panel) {
        this.panel = panel;
        polygon = new Polygon();


        //lineRasterizer = new LineRasterizerGraphics(panel.getRaster());
        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        lines = new ArrayList<>();

        polygon = new Polygon();

        initListeners();
    }



    private void initListeners(){
       panel.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {


                //panel.getRaster().setPixel(e.getX(), e.getY(), 0xff0000);
                //Point point1 = new Point(e.getX(), e.getY());
                //polygon.addPoint(point1);
                // drawScene();
                // panel.repaint();


               //prvy klik
               if (firstPoint == null) {
                   firstPoint = new Point(e.getX(), e.getY());
                   return;
               }
                //druhy klik
               Line line = new Line(firstPoint, new Point(e.getX(), e.getY()));

               line.setRasterizer(lineRasterizer);

               lines.add(line);
               firstPoint = null;
               currentPoint = null;

               drawScene();

           }
       });

       panel.addMouseMotionListener(new MouseAdapter() {
           @Override
           public void mouseMoved(MouseEvent e) {
               // pohyb myskou RT
               if (firstPoint != null) {
                   currentPoint = new Point(e.getX(), e.getY());
                   drawScene();
               }
           }


           @Override
           public void mouseDragged(MouseEvent e) {

//               int centerX = panel.getRaster().getWidth() / 2;
//               int centerY = panel.getRaster().getHeight() / 2;
//               lineRasterizer.rasterize(centerY,centerX,e.getY(),e.getX());
//               panel.getRaster().clear();
//               panel.getRaster().setPixel(e.getX(), e.getY(), color);
//               panel.repaint();

               if (firstPoint != null) {
                   currentPoint = new Point(e.getX(), e.getY());
                   drawScene();
               }

           }
       });

       panel.addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
                // clear key press-C
               if (e.getKeyCode() == KeyEvent.VK_C) {
                   lines.clear();
                   firstPoint = null;
                   currentPoint = null;
                   drawScene();
               }
               // zrusit momentalne kreslenu linku s ESC
               if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                   firstPoint = null;
                   currentPoint = null;
                   drawScene();
               }
               // Switch to trivial algorithm with '1'
               if (e.getKeyCode() == KeyEvent.VK_1) {
                   lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
                   drawScene();
               }

               // Switch to color transition with '2'
               if (e.getKeyCode() == KeyEvent.VK_2) {
                   lineRasterizer = new LineRasterizerColorTransition(panel.getRaster());
                   drawScene();
               }

               if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                   int centerX = panel.getRaster().getWidth() / 2;
                   int centerY = panel.getRaster().getHeight() / 2;

                   for(int x = centerX; x < panel.getRaster().getWidth(); x++){
                       panel.getRaster().setPixel(x, centerY, color);

                       if (e.getKeyCode() == KeyEvent.VK_O) {
                           color = 0xff00ff;
                       }
                       if (e.getKeyCode() == KeyEvent.VK_P) {
                           color = 0xff0000;
                       }
                   }
                   panel.repaint();
               }
           }
       });
    }
    private void drawScene() {
        panel.getRaster().clear();

        // miesto pouzivania rasterizeru prevsetky lines
        // kazda linka pouziva svoj vlastny rasterizer ktory sa uklada ked sa vytvori

        for (Line line : lines) {

            if (line.getRasterizer() != null) {
                line.getRasterizer().rasterize(line);
            } else {
                // Fallback: ak ziaden rasterizer nebol nastaven, pouzijeme momentalny
                lineRasterizer.rasterize(line);
            }
        }

        if (firstPoint != null && currentPoint != null) {
            lineRasterizer.rasterize(
                    firstPoint.getX(), firstPoint.getY(),
                    currentPoint.getX(), currentPoint.getY()
            );
        }


        panel.repaint();
    }


}
