package org.example.Edge;

import java.util.*;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;


public class CentralSensor {
    //main donde se instancian los sensores y se envian mensajes al proxy
    //prueba para enviar mensaje a proxy

    public static void main(String[] args){

        List<SensorHumo> sensoresHumo = new ArrayList<>();
        List<SensorHumedad> sensoresHumedad = new ArrayList<>();
        List<SensorTemperatura> sensoresTemperatura = new ArrayList<>();

        SensorHumo sh1 = new SensorHumo();
        SensorHumo sh2 = new SensorHumo();
        SensorHumo sh3 = new SensorHumo();
        SensorHumo sh4 = new SensorHumo();
        SensorHumo sh5 = new SensorHumo();
        SensorHumo sh6 = new SensorHumo();
        SensorHumo sh7 = new SensorHumo();
        SensorHumo sh8 = new SensorHumo();
        SensorHumo sh9 = new SensorHumo();
        SensorHumo sh10 = new SensorHumo();

        sensoresHumo.add(sh1);
        sensoresHumo.add(sh2);
        sensoresHumo.add(sh3);
        sensoresHumo.add(sh4);
        sensoresHumo.add(sh5);
        sensoresHumo.add(sh6);
        sensoresHumo.add(sh7);
        sensoresHumo.add(sh8);
        sensoresHumo.add(sh9);
        sensoresHumo.add(sh10);


        for(SensorHumo sh : sensoresHumo){
            double medicion = sh.generarMedicion();
            System.out.println("Medicion: " + medicion);
        }

        /*
        try (ZContext context = new ZContext()) {
            // Crear un socket para comunicarse con el proxy
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("192.168.1.13"); // Cambia la dirección según la ubicación de tu proxy

            // Enviar un mensaje al proxy
            String mensaje = "¡Hola, proxy!";
            socket.send(mensaje.getBytes(ZMQ.CHARSET), 0);

            // Esperar la respuesta del proxy
            byte[] respuesta = socket.recv(0);
            System.out.println("Respuesta del proxy: " + new String(respuesta, ZMQ.CHARSET));
        }

         */
    }
}
