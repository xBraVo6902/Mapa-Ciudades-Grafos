package paneles;

import aplicacion.App;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;

public class panelLocal extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField textFieldFile1;
    private JTextField textFieldFile2;

    public panelLocal() {
        // Crear los componentes
        textFieldFile1 = new JTextField(20);
        textFieldFile2 = new JTextField(20);
        JButton buttonChooseFile1 = new JButton("Archivo Nodos");
        JButton buttonChooseFile2 = new JButton("Archivo Edges");

        // Configurar el layout del JPanel
        SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.WEST, buttonChooseFile2, 248, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, buttonChooseFile2, -67, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.EAST, textFieldFile2, -19, SpringLayout.WEST, buttonChooseFile2);
        springLayout.putConstraint(SpringLayout.EAST, buttonChooseFile1, -1, SpringLayout.EAST, buttonChooseFile2);
        springLayout.putConstraint(SpringLayout.EAST, textFieldFile1, -19, SpringLayout.WEST, buttonChooseFile1);
        springLayout.putConstraint(SpringLayout.WEST, buttonChooseFile1, 0, SpringLayout.WEST, buttonChooseFile2);
        springLayout.putConstraint(SpringLayout.WEST, textFieldFile2, 0, SpringLayout.WEST, textFieldFile1);
        setLayout(springLayout);

        // Añadir los componentes al JPanel
        JLabel lblNodos = new JLabel("Nodos:");
        springLayout.putConstraint(SpringLayout.NORTH, lblNodos, 141, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, textFieldFile1, 6, SpringLayout.EAST, lblNodos);
        springLayout.putConstraint(SpringLayout.NORTH, buttonChooseFile1, -4, SpringLayout.NORTH, lblNodos);
        springLayout.putConstraint(SpringLayout.NORTH, textFieldFile1, -3, SpringLayout.NORTH, lblNodos);
        add(lblNodos);
        add(textFieldFile1);
        add(buttonChooseFile1);
        JLabel label2 = new JLabel("Edges:");
        springLayout.putConstraint(SpringLayout.WEST, label2, 49, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, label2, -352, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.NORTH, textFieldFile2, -3, SpringLayout.NORTH, label2);
        springLayout.putConstraint(SpringLayout.NORTH, buttonChooseFile2, -4, SpringLayout.NORTH, label2);
        springLayout.putConstraint(SpringLayout.NORTH, label2, 16, SpringLayout.SOUTH, lblNodos);
        springLayout.putConstraint(SpringLayout.WEST, lblNodos, 0, SpringLayout.WEST, label2);
        add(label2);
        add(textFieldFile2);
        add(buttonChooseFile2);

        JButton btnNewButton = new JButton("Cargar");
        springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 26, SpringLayout.SOUTH, buttonChooseFile2);
        springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 195, SpringLayout.WEST, this);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarArchivo();
            }
        });
        add(btnNewButton);
        
        JLabel lblNewLabel = new JLabel("ARCHIVOS LOCALES");
        springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 100, SpringLayout.WEST, this);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 25));
        add(lblNewLabel);

        // Configurar el manejador de eventos para los botones
        buttonChooseFile1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile(textFieldFile1);
            }
        });

        buttonChooseFile2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile(textFieldFile2);
            }
        });
    }

    private void chooseFile(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            textField.setText(selectedFilePath);
        }
    }

    private void cargarArchivo() {
        String selectedFilePath1 = textFieldFile1.getText();  // Obtén la ruta completa del campo de texto
        String selectedFilePath2 = textFieldFile2.getText();

        // Llamar a la función de cargar archivo de la aplicación principal
        App.cargarArchivo(selectedFilePath1, selectedFilePath2);
        //App(selectedFilePath1, selectedFilePath2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Seleccionar Archivo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 200);

                // Crear una instancia del JPanel y agregarlo al JFrame
                panelLocal fileChooserPanel = new panelLocal();
                frame.getContentPane().add(fileChooserPanel);

                // Hacer visible el JFrame
                frame.setVisible(true);
            }
        });
    }
}
