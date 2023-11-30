package aplicacion;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
public class App {

    private static volatile boolean cancelado = false;
    private static JDialog progressDialog; // Declarar progressDialog como un campo de clase

    public static void cargarArchivo(String filePath1, String filePath2) {
    	cancelado = false; // Reiniciar la variable cancelado

        progressDialog = new JDialog((Frame) null, "Cargando Archivo", true);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setLayout(new BorderLayout());

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelado = true; // Cambiar la variable estática cancelado en lugar de la local canceladoLocal
                progressDialog.dispose();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.SOUTH);

        progressDialog.add(panel, BorderLayout.CENTER);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(null);

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() {
                ArrayList<MyPoint> coordenadas = new ArrayList<>();
                ArrayList<String> nombreCalles = new ArrayList<>();
                ArrayList<Line2D.Double> conexiones = new ArrayList<>();

                double maxX = Double.NEGATIVE_INFINITY;
                double maxY = Double.NEGATIVE_INFINITY;
                double minX = Double.POSITIVE_INFINITY;
                double minY = Double.POSITIVE_INFINITY;

                // HashMap to store points by ID
                HashMap<Double, MyPoint> puntosPorId = new HashMap<>();

                try {
                    // Load the first XML file
                    try (FileInputStream fis = new FileInputStream(new File(filePath1));
                         InputStreamReader isr = new InputStreamReader(fis)) {

                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(new org.xml.sax.InputSource(isr));
                        NodeList rowElements = document.getElementsByTagName("row");

                        int totalRows = rowElements.getLength();
                        for (int i = 0; i < totalRows; i++) {
                            if (cancelado) { // Verificar la variable estática cancelado en lugar de la local canceladoLocal
                                break;
                            }

                            Element rowElement = (Element) rowElements.item(i);
                            double id = Double.parseDouble(rowElement.getElementsByTagName("osmid").item(0).getTextContent());
                            double x = Double.parseDouble(rowElement.getElementsByTagName("x").item(0).getTextContent());
                            double y = Double.parseDouble(rowElement.getElementsByTagName("y").item(0).getTextContent());

                            MyPoint punto = new MyPoint(id, x, y);
                            coordenadas.add(punto);
                            puntosPorId.put(id, punto);

                            maxX = Math.max(maxX, x);
                            maxY = Math.max(maxY, y);
                            minX = Math.min(minX, x);
                            minY = Math.min(minY, y);

                            int progress = (int) ((double) i / totalRows * 50);
                            setProgress(progress);
                        }
                    }

                    // Load the second XML file
                    try (FileInputStream fis = new FileInputStream(new File(filePath2));
                         InputStreamReader isr = new InputStreamReader(fis)) {

                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(new org.xml.sax.InputSource(isr));
                        NodeList rowElements = document.getElementsByTagName("edge");

                        int totalEdges = rowElements.getLength();
                        for (int i = 0; i < totalEdges; i++) {
                            if (cancelado) { // Verificar la variable estática cancelado en lugar de la local canceladoLocal
                                break;
                            }

                            Element rowElement = (Element) rowElements.item(i);
                            double id_1 = Double.parseDouble(rowElement.getElementsByTagName("u").item(0).getTextContent());
                            double id_2 = Double.parseDouble(rowElement.getElementsByTagName("v").item(0).getTextContent());
                            String nombreCamino = rowElement.getElementsByTagName("name").item(0).getTextContent();

                            MyPoint punto1 = puntosPorId.get(id_1);
                            MyPoint punto2 = puntosPorId.get(id_2);

                            if (punto1 != null && punto2 != null) {
                                Line2D.Double conexion = new Line2D.Double(punto1.getX(), punto1.getY(), punto2.getX(), punto2.getY());
                                conexiones.add(conexion);
                                nombreCalles.add(nombreCamino);
                            }

                            int progress = 51 + (int) ((double) i / totalEdges * 50);
                            setProgress(progress);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!cancelado) {
                    DibujarGrafo dibujarGrafo = new DibujarGrafo(conexiones, coordenadas, minX, minY, maxX, maxY, nombreCalles);
                    dibujarGrafo.dibujar();
                }
                return null;
            }

            @Override
            protected void done() {
                if (!cancelado) {
                    progressDialog.dispose();
                }
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(() -> progressBar.setValue((Integer) evt.getNewValue()));
            } else if (StateValue.DONE.equals(evt.getNewValue())) {
                if (!cancelado) {
                    progressDialog.dispose();
                }
            }
        });

        worker.execute();
        progressDialog.setVisible(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        progressDialog.dispose();
    }
    
    public static void cargarOnline(String nodesXML, String edgesXML) {
        cancelado = false;

        progressDialog = new JDialog((Frame) null, "Cargando Archivo", true);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setLayout(new BorderLayout());

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelado = true;
                progressDialog.dispose();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.SOUTH);

        progressDialog.add(panel, BorderLayout.CENTER);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(null);

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() {
                ArrayList<MyPoint> coordenadas = new ArrayList<>();
                ArrayList<String> nombreCalles = new ArrayList<>();
                ArrayList<Line2D.Double> conexiones = new ArrayList<>();

                double maxX = Double.NEGATIVE_INFINITY;
                double maxY = Double.NEGATIVE_INFINITY;
                double minX = Double.POSITIVE_INFINITY;
                double minY = Double.POSITIVE_INFINITY;

                HashMap<Double, MyPoint> puntosPorId = new HashMap<>();

                try {
                    // Load nodes from XML string
                    try (InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(nodesXML.getBytes()))) {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(new org.xml.sax.InputSource(isr));
                        NodeList rowElements = document.getElementsByTagName("row");

                        int totalRows = rowElements.getLength();
                        for (int i = 0; i < totalRows; i++) {
                            if (cancelado) {
                                break;
                            }

                            Element rowElement = (Element) rowElements.item(i);
                            double id = Double.parseDouble(rowElement.getElementsByTagName("osmid").item(0).getTextContent());
                            double x = Double.parseDouble(rowElement.getElementsByTagName("x").item(0).getTextContent());
                            double y = Double.parseDouble(rowElement.getElementsByTagName("y").item(0).getTextContent());

                            MyPoint punto = new MyPoint(id, x, y);
                            coordenadas.add(punto);
                            puntosPorId.put(id, punto);

                            maxX = Math.max(maxX, x);
                            maxY = Math.max(maxY, y);
                            minX = Math.min(minX, x);
                            minY = Math.min(minY, y);

                            int progress = (int) ((double) i / totalRows * 50);
                            setProgress(progress);
                        }
                    }

                    // Load edges from XML string
                    try (InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(edgesXML.getBytes()))) {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(new org.xml.sax.InputSource(isr));
                        NodeList rowElements = document.getElementsByTagName("edge");

                        int totalEdges = rowElements.getLength();
                        for (int i = 0; i < totalEdges; i++) {
                            if (cancelado) {
                                break;
                            }

                            Element rowElement = (Element) rowElements.item(i);
                            double id_1 = Double.parseDouble(rowElement.getElementsByTagName("u").item(0).getTextContent());
                            double id_2 = Double.parseDouble(rowElement.getElementsByTagName("v").item(0).getTextContent());
                            String nombreCamino = rowElement.getElementsByTagName("name").item(0).getTextContent();

                            MyPoint punto1 = puntosPorId.get(id_1);
                            MyPoint punto2 = puntosPorId.get(id_2);

                            if (punto1 != null && punto2 != null) {
                                Line2D.Double conexion = new Line2D.Double(punto1.getX(), punto1.getY(), punto2.getX(), punto2.getY());
                                conexiones.add(conexion);
                                nombreCalles.add(nombreCamino);
                            }

                            int progress = 51 + (int) ((double) i / totalEdges * 50);
                            setProgress(progress);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!cancelado) {
                    DibujarGrafo dibujarGrafo = new DibujarGrafo(conexiones, coordenadas, minX, minY, maxX, maxY, nombreCalles);
                    dibujarGrafo.dibujar();
                }
                return null;
            }

            @Override
            protected void done() {
                if (!cancelado) {
                    progressDialog.dispose();
                }
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(() -> progressBar.setValue((Integer) evt.getNewValue()));
            } else if (StateValue.DONE.equals(evt.getNewValue())) {
                if (!cancelado) {
                    progressDialog.dispose();
                }
            }
        });

        worker.execute();
        progressDialog.setVisible(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        progressDialog.dispose();
    }
}
