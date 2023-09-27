import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class App {

    public static void main(String[] args) {
        VentanaGrafo ventana = new VentanaGrafo();
        ventana.setVisible(true);

        ArrayList<MyPoint> coordenadas = new ArrayList<>();

        try {
            File file = new File("nodes.xml"); // Se abre XML
            		
            // Crea un DocumentBuilder para analizar el archivo XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // Permite trabajar con los elementos del NXL
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            // Obtiene todos los elementos <row> del documento
            NodeList rowElements = document.getElementsByTagName("row");  //getElementByTagName = para getear por palabra clave;
            

            for (int i = 0; i < rowElements.getLength(); i++) {
                Element rowElement = (Element) rowElements.item(i);

               // System.out.println(rowElement.getElementsByTagName("x").item(0).getTextContent());
                // Obtiene los elementos <x> y <y>
                double id = Double.parseDouble(rowElement.getElementsByTagName("osmid").item(0).getTextContent());
                double x = Double.parseDouble(rowElement.getElementsByTagName("x").item(0).getTextContent());
                double y = Double.parseDouble(rowElement.getElementsByTagName("y").item(0).getTextContent());

                // Agrega las coordenadas como un objeto Point al ArrayList
                coordenadas.add(new MyPoint(id,x, y));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File("edges.xml"); // Se abre XML edge
            		
            // Crea un DocumentBuilder para analizar el archivo XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // Permite trabajar con los elementos del NXL
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            // Obtiene todos los elementos <row> del documento
            NodeList rowElements = document.getElementsByTagName("edge");  //getElementByTagName = para getear por palabra clave;
            

            for (int i = 0; i < rowElements.getLength(); i++) {
                Element rowElement = (Element) rowElements.item(i);

                // Obtiene los elementos <x> y <y>
                double id_1 = Double.parseDouble(rowElement.getElementsByTagName("u").item(0).getTextContent());
                double id_2 = Double.parseDouble(rowElement.getElementsByTagName("v").item(0).getTextContent());
                
       
                Grafo nuevoGrafo = new Grafo(coordenadas,id_1,id_2,ventana);
                
                nuevoGrafo.relacionar();
                //System.out.println(nuevoGrafo.getCoordenadas2());
                ventana.setCoordenadas(nuevoGrafo.getCoordenadas2());
                ventana.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
