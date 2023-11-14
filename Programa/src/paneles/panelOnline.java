package paneles;


import javax.swing.*;
import java.awt.Font;

public class panelOnline extends JPanel {
    private static final long serialVersionUID = 1L;

    public panelOnline() {
        JButton buttonChooseFile1 = new JButton("Archivo Nodos");
        JButton buttonChooseFile2 = new JButton("Archivo Edges");

        // Configurar el layout del JPanel
        SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.NORTH, buttonChooseFile2, 7, SpringLayout.SOUTH, buttonChooseFile1);
        springLayout.putConstraint(SpringLayout.NORTH, buttonChooseFile1, 66, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, buttonChooseFile2, 248, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, buttonChooseFile2, -67, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.EAST, buttonChooseFile1, -1, SpringLayout.EAST, buttonChooseFile2);
        springLayout.putConstraint(SpringLayout.WEST, buttonChooseFile1, 0, SpringLayout.WEST, buttonChooseFile2);
        setLayout(springLayout);
        add(buttonChooseFile1);
        add(buttonChooseFile2);
        
        JLabel lblOnline = new JLabel("ONLINE");
        springLayout.putConstraint(SpringLayout.NORTH, lblOnline, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblOnline, 179, SpringLayout.WEST, this);
        lblOnline.setFont(new Font("Tahoma", Font.BOLD, 25));
        add(lblOnline);


     
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Seleccionar Archivo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 200);

                // Crear una instancia del JPanel y agregarlo al JFrame
                panelOnline fileChooserPanel = new panelOnline();
                frame.getContentPane().add(fileChooserPanel);

                // Hacer visible el JFrame
                frame.setVisible(true);
            }
        });
    }
}
