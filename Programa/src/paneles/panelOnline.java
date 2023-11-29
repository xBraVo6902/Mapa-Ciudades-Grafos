package paneles;

import aplicacion.CiudadesProvider;
import aplicacion.App;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class panelOnline extends JPanel {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> comboBox;

    public panelOnline() {
        // Configurar el layout del JPanel
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);

        JLabel lblOnline = new JLabel("ONLINE");
        springLayout.putConstraint(SpringLayout.NORTH, lblOnline, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblOnline, 179, SpringLayout.WEST, this);
        lblOnline.setFont(new Font("Tahoma", Font.BOLD, 25));
        add(lblOnline);

        // Obtener la lista de ciudades desde CiudadesProvider.list()
        List<String> ciudadesList;
        try {
            ciudadesList = CiudadesProvider.instance().list();
        } catch (IOException e) {
            e.printStackTrace();
            ciudadesList = new ArrayList<>(); // Manejar la excepción según tus necesidades
        }

        // Convertir la lista a un array para usar en el JComboBox
        String[] opcionesCiudades = ciudadesList.toArray(new String[0]);

        comboBox = new JComboBox<>(opcionesCiudades);
        springLayout.putConstraint(SpringLayout.NORTH, comboBox, 45, SpringLayout.SOUTH, lblOnline);
        add(comboBox);

        JLabel lblNewLabel = new JLabel("Ciudad:");
        springLayout.putConstraint(SpringLayout.WEST, comboBox, 21, SpringLayout.EAST, lblNewLabel);
        springLayout.putConstraint(SpringLayout.EAST, comboBox, 218, SpringLayout.EAST, lblNewLabel);
        springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 83, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 44, SpringLayout.WEST, this);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        add(lblNewLabel);

        JButton btnMostrarMapa = new JButton("Mostrar Mapa");
        springLayout.putConstraint(SpringLayout.NORTH, btnMostrarMapa, 20, SpringLayout.SOUTH, comboBox);
        springLayout.putConstraint(SpringLayout.WEST, btnMostrarMapa, 0, SpringLayout.WEST, comboBox);
        springLayout.putConstraint(SpringLayout.EAST, btnMostrarMapa, 0, SpringLayout.EAST, comboBox);
        add(btnMostrarMapa);

        // Agregar ActionListener al botón "Mostrar Mapa"
        btnMostrarMapa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ciudadSeleccionada = (String) comboBox.getSelectedItem();
                if (!ciudadSeleccionada.equals("---SELECCIONE UNA CIUDAD---")) {
                    try {
                        // Obtener instancia de CiudadesProvider
                        CiudadesProvider ciudadesProvider = CiudadesProvider.instance();

                        // Obtener ubicaciones de los archivos XML para la ciudad seleccionada
                        CiudadesProvider.Ciudad ciudad = ciudadesProvider.ciudad(ciudadSeleccionada);
                        String xmlNodes = ciudad.getXmlNodes();
                        String xmlEdges = ciudad.getXmlEdges();

                        // Llamar a la función cargarOnline de la clase App
                        App.cargarOnline(xmlNodes, xmlEdges);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Manejar la excepción según tus necesidades.
                    }
                } else {
                    // Manejar el caso en que no se ha seleccionado ninguna ciudad
                    System.out.println("Por favor, seleccione una ciudad antes de mostrar el mapa.");
                }
            }
        });
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
