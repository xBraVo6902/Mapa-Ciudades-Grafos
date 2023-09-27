import java.awt.Point;
import java.util.ArrayList;

public class Grafo {
    private ArrayList<MyPoint> coordenadas;
    private double id_1;
    private double id_2;
    
    private VentanaGrafo ventana;
    private ArrayList<Point> coordenadas2;

    private double minLatitud; // Límites de tu área de interés
    private double maxLatitud;
    private double minLongitud;
    private double maxLongitud;
    private int anchoPlano; // Tamaño de tu plano de dibujo en píxeles
    private int altoPlano;

    public Grafo(ArrayList<MyPoint> coordenadas, double id_1, double id_2,VentanaGrafo ventana) {
        this.coordenadas = coordenadas;
        this.id_1 = id_1;
        this.id_2 = id_2;
        
        this.ventana=ventana;
        
        coordenadas2 = new  ArrayList<Point>();

        
     // Límites geográficos para el mundo
     minLatitud = -90.0; // Latitud mínima (extremo sur)
     maxLatitud = 90.0;  // Latitud máxima (extremo norte)
     minLongitud = -180.0; // Longitud mínima (extremo oeste)
     maxLongitud = 180.0;  // Longitud máxima (extremo este)

     // Tamaño del plano de dibujo en píxeles
     anchoPlano = 1920; // Ancho de tu plano de dibujo en píxeles (ajusta según tu resolución)
     altoPlano = 1080;  // Alto de tu plano de dibujo en píxeles (ajusta según tu resolución)

    }

    public void relacionar() {
        double localx1=0;
        double localy1 = 0;

        double localx2 = 0;
        double localy2 = 0;

        // Encuentra las coordenadas geográficas de id_1 e id_2
        for (MyPoint point : coordenadas) {
            if (point.getId() == id_1) {
                localx1 = point.getX();
                localy1 = point.getY();
            }
            if (point.getId() == id_2) {
                localx2 = point.getX();
                localy2 = point.getY();
            }
        }

        // Convierte las coordenadas geográficas en píxeles
       
        int x1 = convertirCoordenadaX(localx1);
        int y1 = convertirCoordenadaY(localy1);
        
        coordenadas2.add(new Point(x1,y1));
        
        int x2 = convertirCoordenadaX(localx2);
        int y2 = convertirCoordenadaY(localy2);
        coordenadas2.add(new Point(x2,y2));
        
        ventana.setCoordenadas(coordenadas2);
        
    }

 // Convierte una coordenada de latitud en píxeles
    private int convertirCoordenadaX(double longitud) {
        if (longitud < minLongitud) {
            longitud = minLongitud;
            System.out.println("entro al if");
            
        } else if (longitud > maxLongitud) {
            longitud = maxLongitud;
        }
        return (int) ((longitud - minLongitud) / (maxLongitud - minLongitud) * anchoPlano); //Convierte en una posicion entre el rago de minLong y maxLong
    }																						//y el rango de pixeles en el plano para representar long en el eje X


    // Convierte una coordenada de longitud en píxeles
    private int convertirCoordenadaY(double latitud) {
        return (int) ((maxLatitud - latitud) / (maxLatitud - minLatitud) * altoPlano); //Lo mismo para eje Y
    }
    
    public ArrayList<Point> getCoordenadas2() {
  
        return coordenadas2;
    }
}
