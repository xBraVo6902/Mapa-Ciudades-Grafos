package aplicacion;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

public class App {

    public static void cargarArchivo(String filePath1, String filePath2) {

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

                for (int i = 0; i < rowElements.getLength(); i++) {
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
                }
            }

            // Load the second XML file
            try (FileInputStream fis = new FileInputStream(new File(filePath2));
                 InputStreamReader isr = new InputStreamReader(fis)) {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new org.xml.sax.InputSource(isr));
                NodeList rowElements = document.getElementsByTagName("edge");

                for (int i = 0; i < rowElements.getLength(); i++) {
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
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        DibujarGrafo dibujarGrafo = new DibujarGrafo(conexiones, coordenadas, minX, minY, maxX, maxY, nombreCalles);
        dibujarGrafo.dibujar();
    }
}
