package aplicacion;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.awt.geom.Line2D;
public class App {

    public static void cargarArchivo(String filePath1,String filePath2) {
        
        ArrayList<MyPoint> coordenadas = new ArrayList<>();
        ArrayList<String> nombreCalles = new ArrayList<>();
        ArrayList<Line2D.Double> conexiones= new  ArrayList<>();
        
        
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        
        
        try {
            File file = new File(filePath1); // Se abre XML
            		
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

                // Agrega las coordenadas como un objeto MyPoint al ArrayList
                coordenadas.add(new MyPoint(id, x, y));
             // Actualiza los valores máximos y mínimos
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File(filePath2); // Se abre XML edge
            		
            // Crea un DocumentBuilder para analizar el archivo XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // Permite trabajar con los elementos del NXL
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            // Obtiene todos los elementos <row> del documento
            NodeList rowElements = document.getElementsByTagName("edge");  //getElementByTagName = para getear por palabra clave;
            

            for (int i = 0; i < rowElements.getLength(); i++) {
                Element rowElement = (Element) rowElements.item(i);
                double id_1 = Double.parseDouble(rowElement.getElementsByTagName("u").item(0).getTextContent());
                double id_2 = Double.parseDouble(rowElement.getElementsByTagName("v").item(0).getTextContent());
                String nombreCamino = rowElement.getElementsByTagName("name").item(0).getTextContent();
                
                MyPoint punto1 = null;
                MyPoint punto2 = null;
                
                for (MyPoint punto : coordenadas) {
                    if (punto.getId() == id_1) {
                        punto1 = punto;
                    }
                    if (punto.getId() == id_2) {
                        punto2 = punto;
                    }
                    if (punto1 != null && punto2 != null) {
                        // Crear una línea y agregarla a la lista de conexiones
                        Line2D.Double conexion = new Line2D.Double(punto1.getX(), punto1.getY(), punto2.getX(), punto2.getY());
                        
                        conexiones.add(conexion);
                        nombreCalles.add(nombreCamino);
                        break;
                    }
                
                }
                
              
            } } catch (Exception e) {
            e.printStackTrace();
        }
        DibujarGrafo dibujarGrafo = new DibujarGrafo(conexiones, coordenadas, minX, minY, maxX, maxY,nombreCalles);

        dibujarGrafo.dibujar();
    }

    
	/*private static void verConexion(ArrayList<java.awt.geom.Line2D.Double> conexiones) {
		for (Line2D.Double conexion : conexiones) {
		    double x1 = conexion.getX1();
		    double y1 = conexion.getY1();
		    double x2 = conexion.getX2();
		    double y2 = conexion.getY2();

		    System.out.println("Conexión: (" + x1 + ", " + y1 + ") -> (" + x2 + ", " + y2 + ")");
		}
		
	}
    */
    
}