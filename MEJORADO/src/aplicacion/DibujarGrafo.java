package aplicacion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DibujarGrafo extends JPanel {
    private ArrayList<Line2D.Double> conexiones;
    private ArrayList<MyPoint> coordenadas;
    private ArrayList<String> nombreCaminos;
    private double minX, minY, maxX, maxY;
    private double zoom = 0.20;
    private double escalaX;
    private double escalaY;
    private Point2D.Double vistaCentro;
    private MyPoint nodoMasCercano = null;
    
    private static final int TOP = 1;
    private static final int BOTTOM = 2;
    private static final int RIGHT = 4;  
    private static final int LEFT = 8;

    private MyPoint primerNodo = null; //-->dibujar nodo rojo
    private MyPoint segundoNodo = null; //-->primerNodo!= null ---> dibujar nodo Azul
    
    private double vistaX;
    private double vistaY;

    // Coordenadas de referencia (Latitud y Longitud)
    private double referenciaLatitud; // Establece la latitud de referencia aquí
    private double referenciaLongitud; // Establece la longitud de referencia aquí

    private Point lastPoint;

    public DibujarGrafo(ArrayList<Line2D.Double> conexiones, ArrayList<MyPoint> coordenadas, double minX, double minY, double maxX, double maxY,ArrayList<String> nombreCaminos) {
        this.conexiones = conexiones;
        this.coordenadas = coordenadas;
        this.nombreCaminos=nombreCaminos;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        vistaCentro = new Point2D.Double((maxX + minX) / 2, (maxY + minY) / 2);

        setFocusable(true);
        requestFocusInWindow();
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastPoint != null) {
                    int deltaX = lastPoint.x - e.getX();
                    int deltaY = lastPoint.y - e.getY();

                    vistaCentro.setLocation(
                        vistaCentro.getX() + deltaX / escalaX,
                        vistaCentro.getY() + deltaY / escalaY
                    );
                }
                lastPoint = e.getPoint();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point puntoClic = e.getPoint();
                encontrarNodoMasCercano(puntoClic);
                calcularCentroEnPantalla();
                repaint();
            }
        });


        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                if (notches < 0) {
                    zoomOut(e.getPoint());  // Pasa la posición del mouse como argumento
                } else {
                    zoomIn(e.getPoint());  // Pasa la posición del mouse como argumento
                    
                }
            }
        });
        
        
    
     // Crear el botón y agregar un ActionListener para manejar su acción
	    JButton closeButton = new JButton("Cerrar");
	    closeButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            // Cerrar la ventana al hacer clic en el botón
	            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(DibujarGrafo.this);
	            frame.dispose();
	        }
        });

        // Crear un panel para contener el botón y agregarlo al panel principal
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH); // Puedes ajustar la posición del botón según tus necesidades
    }

    private void encontrarNodoMasCercano(Point puntoClic) {
        double distanciaMinima = Double.MAX_VALUE;
        nodoMasCercano = null;

        for (MyPoint punto : coordenadas) {
            double x = (punto.getX() - minX) * escalaX - vistaX;
            double y = (punto.getY() - minY) * escalaY - vistaY;
            double distancia = puntoClic.distance(x, y); 

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                nodoMasCercano = punto;
            }
        }

        if (distanciaMinima < 1080) { // se define que si el mouse está a menos de 5 unidades de píxeles de un NODO este se selecciona
            if (primerNodo == null) {
                primerNodo = nodoMasCercano;
                System.out.println("Coordenada x " + primerNodo.getX() + " Coordenada Y " + primerNodo.getY() + " ID Nodo " + primerNodo.getId());
            } else if (segundoNodo == null) {
                segundoNodo = nodoMasCercano;
                System.out.println("Coordenada x " + segundoNodo.getX() + " Coordenada Y " + segundoNodo.getY() + " ID Nodo " + segundoNodo.getId());
            }
        }
    }

    private Point2D.Double calcularCentroEnPantalla() {
        double xCentro = (vistaCentro.getX() - minX) * escalaX - vistaX;
        double yCentro = (vistaCentro.getY() - minY) * escalaY - vistaY;
        return new Point2D.Double(xCentro, yCentro);
    }

    private double distanciaHaversine(MyPoint punto1, MyPoint punto2) {
        double radioTierra = 6371.0; // Radio de la Tierra en kilómetros

        // Convertir las coordenadas de los nodos a latitud y longitud
        double latitud1 = referenciaLatitud + punto1.getY();
        double longitud1 = referenciaLongitud + punto1.getX();

        double latitud2 = referenciaLatitud + punto2.getY();
        double longitud2 = referenciaLongitud + punto2.getX();

        // Calcular la diferencia en latitud y longitud en radianes
        double dLatitud = Math.toRadians(latitud2 - latitud1);
        double dLongitud = Math.toRadians(longitud2 - longitud1);

        // Aplicar la fórmula de Haversine
        double a = Math.sin(dLatitud / 2) * Math.sin(dLatitud / 2) +
                       Math.cos(Math.toRadians(latitud1)) * Math.cos(Math.toRadians(latitud2)) *
                       Math.sin(dLongitud / 2) * Math.sin(dLongitud / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radioTierra * c; // Distancia en kilómetros
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        escalaX = getWidth() / ((maxX - minX) * zoom);
        escalaY = getHeight() / ((maxY - minY) * zoom);

        vistaX = (vistaCentro.getX() - minX) * escalaX;
        vistaY = (vistaCentro.getY() - minY) * escalaY;
        Line2D.Double conexion;
        String nombreCamino;
        for(int i =0; i <conexiones.size();i++)
        {
            conexion = conexiones.get(i);
            nombreCamino = nombreCaminos.get(i);
            double x1 = (conexion.getX1() - minX) * escalaX - vistaX;
            double y1 = (conexion.getY1() - minY) * escalaY - vistaY;
            double x2 = (conexion.getX2() - minX) * escalaX - vistaX;
            double y2 = (conexion.getY2() - minY) * escalaY - vistaY;
            if(clipLine(x1,y1,x2,y2))
            {
                if (nombreCamino.compareToIgnoreCase("nan")==0) 
                {
                        g2d.setColor(Color.DARK_GRAY);
                }
                else if(nombreCamino.contains("Ruta"))
                {
                    g2d.setColor(Color.GREEN);
                }
                else
                {
                    g2d.setColor(Color.BLUE);
                }
                g2d.draw(new Line2D.Double(x1, y1, x2, y2));
            }
        }

        // nodo mas cercano actual-.
        if (nodoMasCercano != null) {
            g2d.setColor(Color.RED);
            //g2d.drawOval((int) (x - radio), (int) (y - radio), 2 * radio, 2 * radio); //NO DIBUJAR EL NODO CLICK MAS RECIENTE
        }

        // primer nodo
        if (primerNodo != null) {
            double x = (primerNodo.getX() - minX) * escalaX - vistaX;
            double y = (primerNodo.getY() - minY) * escalaY - vistaY;

            int radio = 5;
            g2d.setColor(Color.RED);
            g2d.drawOval((int) (x - radio), (int) (y - radio), 2 * radio, 2 * radio);
        }

        // Dibujar nodo 2
        if (segundoNodo != null) {
            double x = (segundoNodo.getX() - minX) * escalaX - vistaX;
            double y = (segundoNodo.getY() - minY) * escalaY - vistaY;

            int radio = 5;
            g2d.setColor(Color.BLUE);
            g2d.drawOval((int) (x - radio), (int) (y - radio), 2 * radio, 2 * radio);
        }

        if (primerNodo != null && segundoNodo != null) {
            double x = (primerNodo.getX() - minX) * escalaX - vistaX;
            double y = (primerNodo.getY() - minY) * escalaY - vistaY;

            double x2 = (segundoNodo.getX() - minX) * escalaX - vistaX;
            double y2 = (segundoNodo.getY() - minY) * escalaY - vistaY;

            g2d.setColor(Color.ORANGE);
            g2d.draw(new Line2D.Double(x, y, x2, y2));

            double distancia = distanciaHaversine(primerNodo, segundoNodo);
            System.out.println("Distancia entre dos nodos: " + distancia + " km");
        }
    }

    private boolean clipLine(double x1, double y1, double x2, double y2) {
        double clipMinX = 0;
        double clipMinY = 0;
        double clipMaxX = getWidth();
        double clipMaxY = getHeight();
    
        // Cohen-Sutherland line clipping algorithm
        int outCode1 = calculateOutCode(x1, y1, clipMinX, clipMinY, clipMaxX, clipMaxY);
        int outCode2 = calculateOutCode(x2, y2, clipMinX, clipMinY, clipMaxX, clipMaxY);
    
        while ((outCode1 | outCode2) != 0) {
            if ((outCode1 & outCode2) != 0) {
                // Ambos puntos están fuera de la ventana de visualización, la línea está completamente fuera
                return false;
            }
    
            // Escoge un punto exterior
            int outCode = (outCode1 != 0) ? outCode1 : outCode2;
    
            double x, y;
    
            // Encuentra la intersección de la línea con la ventana de visualización
            if ((outCode & TOP) != 0) {
                x = x1 + (x2 - x1) * (clipMaxY - y1) / (y2 - y1);
                y = clipMaxY;
            } else if ((outCode & BOTTOM) != 0) {
                x = x1 + (x2 - x1) * (clipMinY - y1) / (y2 - y1);
                y = clipMinY;
            } else if ((outCode & RIGHT) != 0) {
                y = y1 + (y2 - y1) * (clipMaxX - x1) / (x2 - x1);
                x = clipMaxX;
            } else if ((outCode & LEFT) != 0) {
                y = y1 + (y2 - y1) * (clipMinX - x1) / (x2 - x1);
                x = clipMinX;
            } else {
                // Este caso no debería ocurrir
                return false;
            }
    
            if (outCode == outCode1) {
                x1 = x;
                y1 = y;
                outCode1 = calculateOutCode(x1, y1, clipMinX, clipMinY, clipMaxX, clipMaxY);
            } else {
                x2 = x;
                y2 = y;
                outCode2 = calculateOutCode(x2, y2, clipMinX, clipMinY, clipMaxX, clipMaxY);
            }
        }
    
        // La línea o parte de ella está dentro de la ventana de visualización
        return true;
    }
    
    // Método para calcular el código de salida en el algoritmo de clipping
    private int calculateOutCode(double x, double y, double clipMinX, double clipMinY, double clipMaxX, double clipMaxY) {
        int code = 0;
    
        if (x < clipMinX) {
            code |= LEFT;
        } else if (x > clipMaxX) {
            code |= RIGHT;
        }
    
        if (y < clipMinY) {
            code |= TOP;
        } else if (y > clipMaxY) {
            code |= BOTTOM;
        }
    
        return code;
    }

    public void dibujar() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public void zoomIn(Point mousePoint) {
        double prevZoom = zoom;
        zoom *= 1.2;
        ajustarCentroZoom(mousePoint, prevZoom, zoom);
        repaint();
    }

    public void zoomOut(Point mousePoint) {
        double prevZoom = zoom;
        zoom /= 1.2;
        ajustarCentroZoom(mousePoint, prevZoom, zoom);
        repaint();
    }

    private void ajustarCentroZoom(Point mousePoint, double prevZoom, double newZoom) {
        double factor = newZoom / prevZoom;
        double dx = (mousePoint.x - getWidth() / 2) / -escalaX;
        double dy = (mousePoint.y - getHeight() / 2) / -escalaY;
        vistaCentro.setLocation(vistaCentro.getX() - dx * factor, vistaCentro.getY() - dy * factor);
        

    }

    public void moveLeft() {
        vistaCentro.setLocation(vistaCentro.getX() - (maxX - minX) * 0.05, vistaCentro.getY());
        repaint();
    }

    public void moveRight() {
        vistaCentro.setLocation(vistaCentro.getX() + (maxX - minX) * 0.05, vistaCentro.getY());
        repaint();
    }

    public void moveUp() {
        vistaCentro.setLocation(vistaCentro.getX(), vistaCentro.getY() - (maxY - minY) * 0.05);
        repaint();
    }

    public void moveDown() {
        vistaCentro.setLocation(vistaCentro.getX(), vistaCentro.getY() + (maxY - minY) * 0.05);
        repaint();
    }
}  