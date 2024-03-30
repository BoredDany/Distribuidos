package org.example.Edge;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CentralSensor {
    public static void main(String[] args) {

        Random random = new Random();

        List<SensorHumo> sensoresHumo = new ArrayList<>();
        String archivoConfigSH = "sensoresHumo.txt";
        List<Double> probabilidadesSH ; // Falta asignar método para leer el archivo de configuracion

        // Crear instancias de Sensores de Humo
        for (int i = 0; i < 10; i++) {
            SensorHumo sh = new SensorHumo();
            sensoresHumo.add(sh);

        }

        // Crear instancias de Sensores de Humedad
        List<SensorHumedad> sensoresHumedad = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SensorHumedad shu = new SensorHumedad();
            sensoresHumedad.add(shu);
        }

        // Crear instancias de Sensores de Temperatura
        List<SensorTemperatura> sensoresTemperatura = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SensorTemperatura st = new SensorTemperatura();
            sensoresTemperatura.add(st);
        }

        // Aquí se realiza la comunicación con el proxy
        try (ZContext context = new ZContext()) {
            // Crear un socket para comunicarse con el proxy
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://localhost:5000");

            // Enviar las mediciones de los sensores de humo al proxy
            for (SensorHumo sh : sensoresHumo) {
                double medicion = sh.generarMedicion();
                String mensaje = "Medicion Humo: " + medicion;
                socket.send(mensaje.getBytes(ZMQ.CHARSET), 0);
                byte[] respuesta = socket.recv(0);
                System.out.println("Respuesta del proxy: " + new String(respuesta, ZMQ.CHARSET));
            }

            // Enviar las mediciones de los sensores de humedad al proxy
            for (SensorHumedad shu : sensoresHumedad) {
                double medicion = shu.generarMedicion();
                String mensaje = "Medicion Humedad: " + medicion;
                socket.send(mensaje.getBytes(ZMQ.CHARSET), 0);
                byte[] respuesta = socket.recv(0);
                System.out.println("Respuesta del proxy: " + new String(respuesta, ZMQ.CHARSET));
            }

            // Enviar las mediciones de los sensores de temperatura al proxy
            for (SensorTemperatura st : sensoresTemperatura) {
                double medicion = st.generarMedicion();
                String mensaje = "Medicion Temperatura: " + medicion;
                socket.send(mensaje.getBytes(ZMQ.CHARSET), 0);
                byte[] respuesta = socket.recv(0);
                System.out.println("Respuesta del proxy: " + new String(respuesta, ZMQ.CHARSET));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
