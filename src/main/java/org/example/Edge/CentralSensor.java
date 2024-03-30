package org.example.Edge;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;

public class CentralSensor {
    public static void main(String[] args) {
        List<SensorHumo> sensoresHumo = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            SensorHumo sh = new SensorHumo();
            sensoresHumo.add(sh);

        }

        List<SensorHumedad> sensoresHumedad = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SensorHumedad shu = new SensorHumedad();
            sensoresHumedad.add(shu);
        }

        List<SensorTemperatura> sensoresTemperatura = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SensorTemperatura st = new SensorTemperatura();
            sensoresTemperatura.add(st);
        }

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
