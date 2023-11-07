package aplicacion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DibujarGrafo extends JPanel {
    private ArrayList<Line2D.Double> conexiones;
    private ArrayList<MyPoint> coordenadas;
    private double minX, minY, maxX, maxY;
    private double zoom = 1.0;
    private double escalaX;
    private double escalaY;
    private Point2D.Double vistaCentro;
    private MyPoint nodoMasCercano = null;

    private Point2D.Double puntoAntesZoom;//para implementar zoom respecto del mouse
    private Point2D.Double puntoDespuesZoom;

    
    private MyPoint primerNodo = null; //-->dibujar nodo rojo
    private MyPoint segundoNodo = null; //-->primerNodo!= null ---> dibujar nodo Azul
    
    private Point2D.Double nodoResultante=null;
    private double vistaX;
    private double vistaY;

    // Coordenadas de referencia (Latitud y Longitud)
    private double referenciaLatitud; // Establece la latitud de referencia aquí
    private double referenciaLongitud; // Establece la longitud de referencia aquí

    public DibujarGrafo(ArrayList<Line2D.Double> conexiones, ArrayList<MyPoint> coordenadas, double minX, double minY, double maxX, double maxY) {
        this.conexiones = conexiones;
        this.coordenadas = coordenadas;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        vistaCentro = new Point2D.Double((maxX + minX) / 2, (maxY + minY) / 2);

        setFocusable(true);
        requestFocusInWindow();
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {//Este trozo de codigo sirve para la rueda del raton (Se supone)
                int notches = e.getWheelRotation();
                Point puntoRaton = e.getPoint();

                puntoAntesZoom = new Point2D.Double((puntoRaton.getX() + vistaX) / escalaX + minX, (puntoRaton.getY() + vistaY) / escalaY + minY);

                if (notches > 0) {
                    zoomOut();
                } else {
                    zoomIn();
                }

                puntoDespuesZoom = new Point2D.Double((puntoRaton.getX() + vistaX) / escalaX + minX, (puntoRaton.getY() + vistaY) / escalaY + minY);

                double diferenciaX = puntoAntesZoom.getX() - puntoDespuesZoom.getX();
                double diferenciaY = puntoAntesZoom.getY() - puntoDespuesZoom.getY();

                vistaCentro.setLocation(vistaCentro.getX() + diferenciaX, vistaCentro.getY() + diferenciaY);

                repaint();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) {
                    zoomOut();
                } else if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
                    zoomIn();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    moveLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    moveRight();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    moveUp();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    moveDown();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point puntoClic = e.getPoint();
                encontrarNodoMasCercano(puntoClic);
                calcularCentroEnPantalla();
                repaint();
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

        // Convertir las coordenadas de los puntos a latitud y longitud
        double latitud1 = referenciaLatitud + (punto1.getY() - vistaCentro.getY()) / escalaY;
        double longitud1 = referenciaLongitud + (punto1.getX() - vistaCentro.getX()) / escalaX;

        double latitud2 = referenciaLatitud + (punto2.getY() - vistaCentro.getY()) / escalaY;
        double longitud2 = referenciaLongitud + (punto2.getX() - vistaCentro.getX()) / escalaX;

        // Calcular las diferencias en latitud y longitud en radianes
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

        for (Line2D.Double conexion : conexiones) {
            double x1 = (conexion.getX1() - minX) * escalaX - vistaX;
            double y1 = (conexion.getY1() - minY) * escalaY - vistaY;
            double x2 = (conexion.getX2() - minX) * escalaX - vistaX;
            double y2 = (conexion.getY2() - minY) * escalaY - vistaY;

            g2d.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        // nodo mas cercano actual-.
        if (nodoMasCercano != null) {
            double x = (nodoMasCercano.getX() - minX) * escalaX - vistaX;
            double y = (nodoMasCercano.getY() - minY) * escalaY - vistaY;

            int radio = 5;
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

    public void dibujar() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public void zoomIn() {
        zoom *= 1.2;
        repaint();
    }

    public void zoomOut() {
        zoom /= 1.2;
        repaint();
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