package ventanas;
import javax.swing.*;

import coquimbo.App;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Principal extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private JProgressBar progressBar;
    private JButton btnCancelar; // Botón Cancelar
    private SwingWorker<Void, Integer> worker; // Referencia al SwingWorker

    public Principal() {
        setLayout(null);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBounds(90, 292, 301, 14);
        add(progressBar);

        JLabel lblNewLabel = new JLabel("Nombre del Archivo:");
        lblNewLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblNewLabel.setBounds(90, 190, 110, 14);
        add(lblNewLabel);

        textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textField.setBounds(210, 187, 180, 20);
        add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("Abrir");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nameField = textField.getText();
                if(!nameField.equalsIgnoreCase("nodes") && !nameField.equalsIgnoreCase("edges")) {
                	File field = new File(nameField + ".txt");
                	if (!field.exists()) {
                		// Mostrar un mensaje si el archivo no existe
                		JOptionPane.showMessageDialog(null, "El archivo deseado no se encontró.", "Error",
                				JOptionPane.ERROR_MESSAGE);
                		textField.requestFocus(); // Volver a enfocar el campo de texto
                		return; // Salir del método sin crear el SwingWorker
                	}
                	
                	if (nameField.equalsIgnoreCase("cordenadas")) {
                		CruzMilenioPanel p2 = new CruzMilenioPanel();
                		loadPage(p2);
                	} else {
                		cargarArchivoEnSegundoPlano(nameField);
                	}
                }else {
                	App.main(null);
                }
            }
        });
        btnNewButton.setBounds(197, 218, 89, 23);
        add(btnNewButton);

        btnCancelar = new JButton("Cancelar"); // Inicialmente invisible
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (worker != null && !worker.isDone()) {
                    worker.cancel(true); // Cancelar el trabajo en segundo plano
                    progressBar.setValue(0); // Restablecer la barra de progreso
                    btnCancelar.setVisible(false); // Ocultar el botón Cancelar
                }
            }
        });
        btnCancelar.setBounds(197, 312, 89, 23);
        add(btnCancelar);
        btnCancelar.setVisible(false); // Inicialmente oculto
    }

    private void cargarArchivoEnSegundoPlano(String fileName) {
        worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                btnCancelar.setVisible(true); // Mostrar el botón Cancelar al comenzar el proceso
                int blockSize = 4096; // Tamaño del bloque en bytes
                File field=null;
                if(!fileName.equalsIgnoreCase("nodes")) {
                	field = new File(fileName + ".txt");
                	if (!field.exists()) {
                		// Mostrar un mensaje si el archivo no existe
                		JOptionPane.showMessageDialog(null, "El archivo deseado no se encontró.", "Error",
                				JOptionPane.ERROR_MESSAGE);
                		textField.requestFocus(); // Volver a enfocar el campo de texto
                		return null;
                	}
                }else {
                	field= new File(fileName + ".xml");
                }

                long fileSize = field.length();
                FileInputStream fileInputStream = new FileInputStream(field);
                byte[] buffer = new byte[blockSize];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    if (isCancelled()) {
                        break; // Salir del ciclo si se solicita la cancelación
                    }

                    totalBytesRead += bytesRead;
                    int charger = (int) ((totalBytesRead * 100) / fileSize);
                    publish(charger);
                }

                fileInputStream.close();
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                // No es necesario llamar a dispose() aquí
                btnCancelar.setVisible(false); // Ocultar el botón Cancelar al finalizar el proceso
                if (isCancelled()) {
                    JOptionPane.showMessageDialog(null, "Carga Cancelada", "",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Cargado Exitosamente", "",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Aquí puedes realizar alguna acción o notificar al JFrame que el proceso ha terminado.
                    String nameField = textField.getText();
                    if (nameField.equalsIgnoreCase("cordenadas")) {
                        CruzMilenioPanel p2 = new CruzMilenioPanel();
                        loadPage(p2);
                    } else if (nameField.equalsIgnoreCase("edges") || nameField.equalsIgnoreCase("nodes")) {
                        // Cargar el archivo de aristas
                        try {
                            // Realizar el trabajo en segundo plano para la carga de App.main(null)
                            SwingWorker<Void, Void> appWorker = new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    // Llamar a App.main(null) aquí
                                    App.main(null);
                                    return null;
                                }

                                @Override
                                protected void done() {
                                    // Acciones después de que App.main(null) ha terminado
                                    // Puedes agregar cualquier lógica adicional aquí
                                }
                            };
                            
                            // Ejecutar el trabajo en segundo plano para la carga de App.main(null)
                            appWorker.execute();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            // Manejar cualquier excepción que pueda ocurrir durante la carga de App.main(null)
                        }
                    }
                }
            }

        };

        worker.execute(); // Iniciar el trabajo en segundo plano
    }

    private void loadPage(JPanel page) {
        page.setSize(470, 536);
        page.setLocation(0, 0);
        removeAll();
        add(page, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
