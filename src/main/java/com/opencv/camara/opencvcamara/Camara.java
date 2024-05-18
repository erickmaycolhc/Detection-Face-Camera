    package com.opencv.camara.opencvcamara;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import javax.swing.*;

    import org.opencv.core.*;
    import org.opencv.core.Point;
    import org.opencv.imgcodecs.Imgcodecs;
    import org.opencv.imgproc.Imgproc;
    import org.opencv.objdetect.CascadeClassifier;
    import org.opencv.videoio.VideoCapture;

    public class Camara extends JFrame {
        // Componentes de la GUI
        private JLabel camaraScreen; // Para mostrar la salida de la cámara
        private JButton btnCapture;  // Para capturar imágenes
        private VideoCapture capture;// Para acceder a la cámara
        private Mat image;           // Para almacenar imágenes de la cámara

        private boolean clicked = false; // Indica si se hizo clic en el botón de captura


        // Constructor
        public Camara(){

            // Diseño de la interfaz de usuario
            setLayout(null); // Desactiva el diseño automático para poder posicionar los componentes manualmente

            /// Agregar la pantalla de la cámara
            camaraScreen = new JLabel();
            camaraScreen.setBounds(0,0, 650, 480); // Establece las dimensiones y la posición de la pantalla de la cámara
            add(camaraScreen); // Agrega la pantalla de la cámara al marco

            // Agregar el botón de captura
            btnCapture = new JButton("capture");
            btnCapture.setBounds(300, 480,80, 40); // Establece las dimensiones y la posición del botón de captura
            add(btnCapture);  // Agrega el botón de captura al marco

            // Acción del botón de captura
            btnCapture.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e){
                    clicked = true; // Marca que se ha hecho clic en el botón de captura
                }
            });

            // Configuración de la ventana
            setSize(new Dimension(640,560)); // Establece el tamaño de la ventana
            setLocationRelativeTo(null); // Centra la ventana en la pantalla
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Establece la operación de cierre
            setVisible(true);   // Hace visible la ventana

        }

        // Método para iniciar la cámara
        public void StartCamara(){
            // Carga el clasificador de cascada para detectar rostros
            CascadeClassifier faceCascade = new CascadeClassifier();
            faceCascade.load("data/haarcascade_frontalface_alt2.xml");

            // Inicia la captura de vídeo desde la cámara
            capture = new VideoCapture(0); // El argumento 0 indica la cámara predeterminada (generalmente la webcam)
            image  = new Mat(); // Crea una matriz para almacenar las imágenes de la cámara



            // Bucle para capturar y mostrar imágenes de la cámara
            while(true){
                // Lee una imagen de la cámara
                capture.read(image);
                // para poner el filtro de cámara en griss
                //Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);

                // Detecta rostros en la imagen y dibuja rectángulos alrededor de ellos
                MatOfRect faces = new MatOfRect();
                faceCascade.detectMultiScale(image, faces);
                for(Rect rect: faces.toArray()){
                    Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);
                }

                // Convierte la imagen en una representación de bytes y la muestra en la pantalla de la cámara
                final MatOfByte buf = new MatOfByte();
                Imgcodecs.imencode(".jpg", image,buf);
                byte[] imageData = buf.toArray();
                ImageIcon icon = new ImageIcon(imageData);
                camaraScreen.setIcon(icon);

                // Si se hizo clic en el botón de captura, guarda la imagen en un archivo
                if(clicked){
                    //solicitar ingresar el nombre de la imagen
                    String name = JOptionPane.showInputDialog("Enter image name");
                    if(name == null){
                        name = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date());
                    }

                    //escribir en el archivo
                    Imgcodecs.imwrite("images/" + name + ".jpg", image);
                    clicked = false;
                }
            }
        }
        // Método principal
        public static void main(String[] args){
            // Carga la biblioteca nativa de OpenCV
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            // Crea una instancia de la clase Camara y la ejecuta en un hilo separado
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Camara camara = new Camara();

                    //start camara in thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            camara.StartCamara();

                        }
                    }).start();
                }
            });
        }
    }

    /*package com.opencv.camara.opencvcamara;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

public class Camara extends JFrame {
    //camara  screen
    private JLabel camaraScreen;
    private JButton btnCapture;
    private VideoCapture capture;
    private Mat image;

    private boolean clicked = false;


    public Camara(){

        //desing ui
        setLayout(null);

        //agregando el boton
        camaraScreen = new JLabel();
        camaraScreen.setBounds(0,0, 650, 480);
        add(camaraScreen);

        //agregando cuerpo del boton
        btnCapture = new JButton("capture");
        btnCapture.setBounds(300, 480,80, 40);
        add(btnCapture);

        //acción del boton
        btnCapture.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                clicked = true;
            }
        });

        //dimensiones de la camara
        setSize(new Dimension(640,560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    //create camara
    public void StartCamara(){
        capture = new VideoCapture(0);
        image  = new Mat();
        byte[] imageData;
        ImageIcon icon;

        // si es verdad capture lee imagen
        while(true){

            //lee image a matrix
            capture.read(image);

            //convert matrix to byte
            final MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image,buf);

            imageData = buf.toArray();
            //agregar a JLabel
            icon = new ImageIcon(imageData);
            camaraScreen.setIcon(icon);

            //captura y guardar el documento
            if(clicked){
                //solicitar ingresar el nombre de la imagen
                String name = JOptionPane.showInputDialog("Enter image name");
                if(name == null){
                    name = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date());
                }

                //escribir en el archivo
                Imgcodecs.imwrite("images/" + name + ".jpg", image);
                clicked = false;
            }
        }



    }

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Camara camara = new Camara();

                //start camara in thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camara.StartCamara();

                    }
                }).start();
            }
        });
    }
}*/
