package com.camerateste;

import java.awt.Dimension; //controlador das dimensões do GUI
import java.awt.EventQueue; //controlador de eventos do GUI(click, capture...)
import java.awt.event.ActionEvent; //responsavel por lidar com ações do usuário
import java.awt.event.ActionListener; //interface para chamar o actionPerformed que é usado para ações
import java.awt.event.WindowAdapter; //fechamento de janela
import java.awt.event.WindowEvent; //abertura ou fechamento de janela
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon; //inserir imagens no GUI
import javax.swing.JButton; //botao
import org.opencv.core.Core; //operações matriciais essenciais do OpenCV, de extrema importancia
import javax.swing.JFrame; //janela
import javax.swing.JLabel; //informações no geral
import javax.swing.JOptionPane; //input do usuario
import org.opencv.core.Mat; //representa uma imagem atraves de matriz
import org.opencv.core.MatOfByte; //transformador de matriz para bytes(como o compilador lê)
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs; //lê e grava imagens
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture; //captura de vídeo

//projeto teste com auxilio dos docs OpenCV e videos no YT
//ta bagunçado mas ta honesto :)

public class Camera extends JFrame {

    //var
    private JLabel cameraScreen;
    private JButton btnCapture;
    private VideoCapture capture;
    private Mat image;
    private boolean clicked = false;

    private CascadeClassifier faceDetector;

//UI camera
    public Camera() {

        setLayout(null);

        cameraScreen = new JLabel();
        cameraScreen.setBounds(300, 0, 640, 480);
        add(cameraScreen);

        btnCapture = new JButton("Capture");
        btnCapture.setBounds(550, 480, 80, 40);
        add(btnCapture);

        //vincula o botao capture a uma açao que leva o clicked
        //a ser true
        btnCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clicked = true;
            }
        });
//UI camera

        //instancia uma classe para armazernar o rosto
        faceDetector = new CascadeClassifier("C:\\Users\\favul\\OneDrive\\Documentos\\NetBeansProjects\\cameraTeste\\haarcascades\\haarcascade_frontalface_default.xml");

        //FECHAR A JANELA
        addWindowFocusListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                capture.release();
                image.release();
                System.exit(0);
            }
        });
        //FECHAR A JANELA

        //dimensão do GUI
        setSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //camera
    public void startCamera() {
        capture = new VideoCapture(0);
        image = new Mat();
        byte[] imageData;
        ImageIcon icon;

        while (true) {
            //transforma a iamgem para matriz
            capture.read(image);

            //detecta o rosto
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(image, faceDetections);

            // desenha retangulo nos rostos
            for (Rect rect : faceDetections.toArray()) {
                Imgproc.rectangle(image, rect, new Scalar(0, 255, 0), 1,5);
            }
            //inverter a imagem
            Core.flip(image, image, 1);

            //converte de matriz para byte
            final MatOfByte buff = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buff);
            imageData = buff.toArray();

            //add byte para JLabel
            icon = new ImageIcon(imageData);
            cameraScreen.setIcon(icon);

            //tira foto e salva
            if (clicked) {
                //prompt for enter image name
                String name = JOptionPane.showInputDialog(this, "Enter image name");
                if (name == null) {
                    name = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date());
                }
                //salva o arquivo
                Imgcodecs.imwrite("images/" + name + ".jpg", image);
                clicked = false;
            }

        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                Camera camera = new Camera();
                //start camera in thread
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        camera.startCamera();
                    }
                }).start();
            }
        });
    }
}
