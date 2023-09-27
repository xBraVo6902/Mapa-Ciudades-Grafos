import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Ventana extends JFrame {
	private JPanel contentPane;
	private ArrayList<MyPoint> coordenadas;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ventana frame = new Ventana();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
    public Ventana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        contentPane = new MiPanel();
        setContentPane(contentPane);
        setVisible(true);
    }
    
    class MiPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            dibujarCoordenadas(g);
        }
        
        private void dibujarCoordenadas(Graphics g) {
            if (coordenadas != null && coordenadas.size() >= 2) {
                MyPoint puntoAnterior = coordenadas.get(0);
                for (int i = 1; i < coordenadas.size(); i++) {
                    MyPoint puntoActual = coordenadas.get(i);
                    //g.drawLine( puntoAnterior.getX(), puntoAnterior.getY(), puntoActual.getX(), puntoActual.getY());
                    puntoAnterior = puntoActual;
                }
            }
        }
    }
	public void setCoordenadas(ArrayList<MyPoint> coordenadas2) {
		this.coordenadas=coordenadas2;
		contentPane.repaint();
	
		
	}

}
