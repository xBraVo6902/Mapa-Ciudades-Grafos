package ventanas;

import aplicacion.App;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class probando extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField textFieldFile1;
    private JTextField textFieldFile2;
    private JProgressBar progressBar;

    public probando() {
        // Crear los componentes
        textFieldFile1 = new JTextField(20);
        textFieldFile2 = new JTextField(20);
        JButton buttonChooseFile1 = new JButton("Seleccionar Archivo 1");
        JButton buttonChooseFile2 = new JButton("Seleccionar Archivo 2");

        // Configurar el layout del JPanel
        SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.WEST, textFieldFile2, 0, SpringLayout.WEST, textFieldFile1);
        springLayout.putConstraint(SpringLayout.EAST, textFieldFile2, -18, SpringLayout.WEST, buttonChooseFile2);
        springLayout.putConstraint(SpringLayout.WEST, buttonChooseFile2, 249, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, buttonChooseFile2, -66, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.EAST, textFieldFile1, -18, SpringLayout.WEST, buttonChooseFile1);
        springLayout.putConstraint(SpringLayout.WEST, buttonChooseFile1, 249, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, buttonChooseFile1, 0, SpringLayout.EAST, buttonChooseFile2);
        setLayout(springLayout);

        // Añadir los componentes al JPanel
        JLabel label = new JLabel("Archivo 1:");
        springLayout.putConstraint(SpringLayout.WEST, textFieldFile1, 6, SpringLayout.EAST, label);
        springLayout.putConstraint(SpringLayout.NORTH, buttonChooseFile1, -4, SpringLayout.NORTH, label);
        springLayout.putConstraint(SpringLayout.NORTH, label, 96, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.NORTH, textFieldFile1, -3, SpringLayout.NORTH, label);
        add(label);
        add(textFieldFile1);
        add(buttonChooseFile1);
        JLabel label2 = new JLabel("Archivo 2:");
        springLayout.putConstraint(SpringLayout.NORTH, textFieldFile2, -3, SpringLayout.NORTH, label2);
        springLayout.putConstraint(SpringLayout.NORTH, buttonChooseFile2, -4, SpringLayout.NORTH, label2);
        springLayout.putConstraint(SpringLayout.NORTH, label2, 16, SpringLayout.SOUTH, label);
        springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, label2);
        springLayout.putConstraint(SpringLayout.WEST, label2, 50, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, label2, -351, SpringLayout.EAST, this);
        add(label2);
        add(textFieldFile2);
        add(buttonChooseFile2);

        JButton btnNewButton = new JButton("Cargar");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarArchivo();
            }
        });
        springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 25, SpringLayout.SOUTH, buttonChooseFile2);
        springLayout.putConstraint(SpringLayout.EAST, btnNewButton, -170, SpringLayout.EAST, this);
        add(btnNewButton);

        progressBar = new JProgressBar();
        springLayout.putConstraint(SpringLayout.NORTH, progressBar, 10, SpringLayout.SOUTH, btnNewButton);
        springLayout.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, textFieldFile1);
        springLayout.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, buttonChooseFile1);
        add(progressBar);

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

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Tareas en segundo plano
                // Llamar a la función de cargar archivo de la aplicación principal
                int totalProgress = 100; // Define el progreso total
                for (int i = 0; i <= totalProgress; i++) {
                    // Simula el progreso del trabajo real
                    Thread.sleep(50); // Simula una tarea que toma tiempo
                    publish(i * 100 / totalProgress); // Notifica el progreso
                }
                App.cargarArchivo(selectedFilePath1, selectedFilePath2);
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                // Este método se llama cuando se invoca el método publish
                // Puedes actualizar la interfaz de usuario aquí
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
            }

            @Override
            protected void done() {
                // Tareas después de completar el trabajo en segundo plano
                // Por ejemplo, ocultar la barra de progreso
                progressBar.setIndeterminate(false);
            }
        };

        progressBar.setIndeterminate(false); // Detén la barra de progreso indeterminada
        progressBar.setValue(0); // Reinicia el valor de la barra de progreso

        // Ejecuta el SwingWorker en segundo plano
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Seleccionar Archivo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 200);

                // Crear una instancia del JPanel y agregarlo al JFrame
                probando fileChooserPanel = new probando();
                frame.getContentPane().add(fileChooserPanel);

                // Hacer visible el JFrame
                frame.setVisible(true);
            }
        });
    }
}
