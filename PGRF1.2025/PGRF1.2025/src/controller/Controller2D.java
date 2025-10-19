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

    private boolean polygonMode = false;// polygon mod false= line mod, true polygon
    private List<Point> currentPolygonPoints = new ArrayList<>();// polygon v procese
    private List<Polygon> polygons;// komplet polygon
    private boolean shiftPressed = false;


    public Controller2D(Panel panel) {
        this.panel = panel;
        //lineRasterizer = new LineRasterizerGraphics(panel.getRaster());
        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        lines = new ArrayList<>();


        currentPolygonPoints = new ArrayList<>();
        polygons = new ArrayList<>();

        initListeners();
    }

    private Point snapToAngle(Point start, Point target) {

        int dx = target.getX() - start.getX();
        int dy = target.getY() - start.getY();

        //star to target
        double distance = Math.sqrt(dx * dx + dy * dy);

        // uhol rad
        double angle = Math.atan2(dy, dx);

        // uhol na stupne
        double angleDegrees = Math.toDegrees(angle);

        // snap na nablizsi 45 uhol
        double snappedAngleDegrees = Math.round(angleDegrees / 45.0) * 45.0;

        // spat na rad
        double snappedAngle = Math.toRadians(snappedAngleDegrees);

        // novy endpoint s uhol
        int newX = start.getX() + (int) Math.round(distance * Math.cos(snappedAngle));
        int newY = start.getY() + (int) Math.round(distance * Math.sin(snappedAngle));

        return new Point(newX, newY);
    }



    private void initListeners(){
       panel.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {

               int x = Math.max(0, Math.min(e.getX(), panel.getRaster().getWidth() - 1));
               int y = Math.max(0, Math.min(e.getY(), panel.getRaster().getHeight() - 1));

               if (polygonMode) {
                   // POLYGON MODE
                   Point newPoint = new Point(e.getX(), e.getY());

                   // polygon snap
                   if (shiftPressed && !currentPolygonPoints.isEmpty()) {
                       Point lastPoint = currentPolygonPoints.get(currentPolygonPoints.size() - 1);
                       newPoint = snapToAngle(lastPoint, newPoint);
                   }

                   currentPolygonPoints.add(newPoint);
                   drawScene();
               } else {
                   // LINE MODE
                   //prvy klik
                   if (firstPoint == null) {
                       firstPoint = new Point(e.getX(), e.getY());
                       return;
                   }
                   //druhy klik
                   Point secondPoint = new Point(x, y);
                   if (shiftPressed) {
                       secondPoint = snapToAngle(firstPoint, secondPoint);
                   }
                   Line line = new Line(firstPoint, new Point(e.getX(), e.getY()));
                   line.setRasterizer(lineRasterizer);
                   lines.add(line);
                   firstPoint = null;
                   currentPoint = null;

                   drawScene();
               }
           }
       });

       panel.addMouseMotionListener(new MouseAdapter() {
           @Override
           public void mouseMoved(MouseEvent e) {
               // pohyb myskou RT
               if (polygonMode) {
                   // POLYGON MODE: Preview line from last point to cursor
                   if (!currentPolygonPoints.isEmpty()) {
                       currentPoint = new Point(e.getX(), e.getY());
                       // snap preview
                       if (shiftPressed) {
                           Point lastPoint = currentPolygonPoints.get(currentPolygonPoints.size() - 1);
                           currentPoint = snapToAngle(lastPoint, currentPoint);
                       }
                       drawScene();
                   }
               } else {
                   // LINE MODE: Original line preview
                   if (firstPoint != null) {
                       currentPoint = new Point(e.getX(), e.getY());
                       if (shiftPressed) {
                           currentPoint = snapToAngle(firstPoint, currentPoint);
                       }
                       drawScene();
                   }
               }
           }
           @Override
           public void mouseEntered(MouseEvent e) {
               // Don't clamp - allow tracking outside window
               if ((polygonMode && !currentPolygonPoints.isEmpty()) ||
                       (!polygonMode && firstPoint != null)) {
                   currentPoint = new Point(e.getX(), e.getY());
                   drawScene();
               }
           }

           @Override
           public void mouseExited(MouseEvent e) {
               // Update current point even when leaving window
               // This keeps the preview following the mouse
               if ((polygonMode && !currentPolygonPoints.isEmpty()) ||
                       (!polygonMode && firstPoint != null)) {
                   currentPoint = new Point(e.getX(), e.getY());

                   if (shiftPressed) {
                       Point lastPoint = currentPolygonPoints.get(currentPolygonPoints.size() - 1);
                       currentPoint = snapToAngle(lastPoint, currentPoint);
                   }

                   drawScene();
               }
           }

           @Override
           public void mouseDragged(MouseEvent e) {

               if (polygonMode) {
                   if (!currentPolygonPoints.isEmpty()) {
                       currentPoint = new Point(e.getX(), e.getY());
                       drawScene();
                   }
               } else {
                   if (firstPoint != null) {
                       currentPoint = new Point(e.getX(), e.getY());

                       if (shiftPressed) {
                           currentPoint = snapToAngle(firstPoint, currentPoint);
                       }

                       drawScene();
                   }
               }
           }
       });

       panel.addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
               //shift- snap mod
               if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                   shiftPressed = true;
                   // Redraw to show snapped preview immediately
                   if (currentPoint != null) {
                       drawScene();
                   }
                   System.out.println("Shift ON - Snap to 45° angles");
               }
                // clear key press-C
               if (e.getKeyCode() == KeyEvent.VK_C) {
                   lines.clear();
                   firstPoint = null;
                   currentPoint = null;
                   drawScene();
               }
               // zrusit momentalne kreslenu linku s ESC
               if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                   if (polygonMode) {
                       currentPolygonPoints.clear();
                   } else {
                       firstPoint = null;
                   }
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

               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                   if (polygonMode && currentPolygonPoints.size() >= 3) {
                       // DEBUG: Print how many points we have
                       System.out.println("Creating polygon with " + currentPolygonPoints.size() + " points");

                       // Create polygon from current points
                       Polygon polygon = new Polygon(currentPolygonPoints);
                       polygon.setRasterizer(lineRasterizer); // Save current rasterizer

                       // DEBUG: Verify polygon was created
                       System.out.println("Polygon created with " + polygon.getSize() + " points");
                       System.out.println("Polygon has " + polygon.getEdges().size() + " edges");

                       polygons.add(polygon);

                       // DEBUG: Check total polygons
                       System.out.println("Total polygons: " + polygons.size());


                       currentPolygonPoints = new ArrayList<>(); // novy lin miesto clear
                       currentPoint = null;
                       drawScene();
                   } else if (polygonMode) {
                       System.out.println("Need at least 3 points. Current: " + currentPolygonPoints.size());
                   }
               }
                // P= menenie modu z poly na line a naopak
               if (e.getKeyCode() == KeyEvent.VK_P) {
                   polygonMode = !polygonMode;

                   // vymazat vsetky aktualne body
                   firstPoint = null;
                   currentPoint = null;
                   currentPolygonPoints.clear();

                   drawScene();
                   System.out.println("Mode: " + (polygonMode ? "POLYGON" : "LINE"));
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
           @Override
           public void keyReleased(KeyEvent e) {
               if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                   shiftPressed = false;
                   // Redraw to show free preview immediately
                   if (currentPoint != null) {
                       drawScene();
                   }
                   System.out.println("Shift OFF - Free drawing");
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


        System.out.println("Drawing " + polygons.size() + " polygons"); // DEBUG
        for (Polygon polygon : polygons) {
            // Get all edges of the polygon as Line objects
            List<Line> edges = polygon.getEdges();

            System.out.println("  Polygon has " + polygon.getSize() + " points and " + edges.size() + " edges"); // DEBUG

            // Draw each edge using its assigned rasterizer
            for (Line edge : edges) {
                if (edge.getRasterizer() != null) {
                    edge.getRasterizer().rasterize(edge);
                } else {
                    lineRasterizer.rasterize(edge);
                }
            }
        }

        if (polygonMode) {
            // POLYGON MODE: Draw current polygon being created
            System.out.println("Drawing preview with " + currentPolygonPoints.size() + " points"); // DEBUG

            // linky medzi existujicimi bodmi
            for (int i = 0; i < currentPolygonPoints.size() - 1; i++) {
                Point p1 = currentPolygonPoints.get(i);
                Point p2 = currentPolygonPoints.get(i + 1);
                lineRasterizer.rasterize(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }

            // last point na kurzor
            if (!currentPolygonPoints.isEmpty() && currentPoint != null) {
                Point lastPoint = currentPolygonPoints.get(currentPolygonPoints.size() - 1);
                lineRasterizer.rasterize(
                        lastPoint.getX(), lastPoint.getY(),
                        currentPoint.getX(), currentPoint.getY()
                );

                // od kurzoru k prvemu bodu
                if (currentPolygonPoints.size() >= 2) {
                    Point firstPolygonPoint = currentPolygonPoints.get(0);
                    lineRasterizer.rasterize(
                            currentPoint.getX(), currentPoint.getY(),
                            firstPolygonPoint.getX(), firstPolygonPoint.getY()
                    );
                }
            }
        } else {
            // LINE MODE
            if (firstPoint != null && currentPoint != null) {
                lineRasterizer.rasterize(
                        firstPoint.getX(), firstPoint.getY(),
                        currentPoint.getX(), currentPoint.getY()
                );
            }
        }


        panel.repaint();
    }


}
