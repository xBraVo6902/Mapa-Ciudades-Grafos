package xxGrafoNuevo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DibujarGrafo extends JPanel {
    private ArrayList<Line2D.Double> conexiones;
    private double minX, minY, maxX, maxY;
    private double zoom = 1.0;
    private double escalaX;
    private double escalaY;
    private Point2D.Double vistaCentro; // Punto en el centro de la vista

    public DibujarGrafo(ArrayList<Line2D.Double> conexiones, double minX, double minY, double maxX, double maxY) {
        this.conexiones = conexiones;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        vistaCentro = new Point2D.Double((maxX + minX) / 2, (maxY + minY) / 2);

        setFocusable(true); // Permitir que el panel tenga el foco
        requestFocusInWindow(); // Solicitar el foco para recibir eventos de teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) {
                    zoomIn(); // Aumenta el zoom
                } else if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
                    zoomOut(); // Disminuye el zoom
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Calcular el factor de escala para convertir coordenadas geográficas en píxeles
        escalaX = getWidth() / ((maxX - minX) * zoom);
        escalaY = getHeight() / ((maxY - minY) * zoom);

        // Calcular las coordenadas del punto de vista en píxeles
        double vistaX = (vistaCentro.getX() - minX) * escalaX;
        double vistaY = (vistaCentro.getY() - minY) * escalaY;

        // Dibujar las conexiones
        for (Line2D.Double conexion : conexiones) {
            double x1 = (conexion.getX1() - minX) * escalaX - vistaX;
            double y1 = (conexion.getY1() - minY) * escalaY - vistaY;
            double x2 = (conexion.getX2() - minX) * escalaX - vistaX;
            double y2 = (conexion.getY2() - minY) * escalaY - vistaY;

            g2d.draw(new Line2D.Double(x1, y1, x2, y2));
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
        zoom *= 1.2; // Aumenta el zoom en un 20%
        repaint(); // Vuelve a dibujar con el nuevo zoom
    }

    public void zoomOut() {
        zoom /= 1.2; // Disminuye el zoom en un 20%
        repaint(); // Vuelve a dibujar con el nuevo zoom
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
