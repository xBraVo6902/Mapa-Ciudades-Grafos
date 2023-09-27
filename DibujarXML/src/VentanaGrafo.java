import java.awt.Graphics;
import java.awt.Point;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VentanaGrafo extends JFrame {
    private JPanel contentPane;
    private ArrayList<Point> coordenadas;

    public VentanaGrafo() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        contentPane = new MiPanel();
        setContentPane(contentPane);
    }

    class MiPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            dibujarCoordenadas((Graphics2D) g);
        }

        private void dibujarCoordenadas(Graphics2D g) {
            if (coordenadas != null && coordenadas.size() >= 2) {
                Point puntoAnterior = coordenadas.get(0);

                for (int i = 1; i < coordenadas.size(); i++) {
                    Point puntoActual = coordenadas.get(i);
                    g.drawLine(puntoAnterior.x, puntoAnterior.y, puntoActual.x, puntoActual.y);
                    puntoAnterior = puntoActual; // Actualizar el punto anterior
                }
            }
        }
    }

    public void setCoordenadas(ArrayList<Point> coordenadas) {
        this.coordenadas = coordenadas;
      
        contentPane.repaint();
    }
}
