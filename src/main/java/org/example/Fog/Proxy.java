package org.example.Fog;

import org.example.utils.Ip;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;


public class Proxy {

    private String ip = Ip.PROXY_PRINCIPAL;
    private Integer intervaloTemperatura;
    private Integer intervaloHumedad;
    private String ipChecker = Ip.HEALTH_CHECKER;
    private String ipCentralSensor = Ip.CENTRAL_SENSOR;

    public Proxy(Integer intervaloTemperatura, Integer intervaloHumedad) {
        this.intervaloTemperatura = intervaloTemperatura;
        this.intervaloHumedad = intervaloHumedad;
    }

    public static void main(String[] args) throws Exception {
        Proxy proxy = new Proxy(5,5);
        try (ZContext context = new ZContext()) {
            // Crear socket para recibir mediciones (PULL)
            ZMQ.Socket socketMedicion = context.createSocket(SocketType.PULL);
            socketMedicion.bind("tcp://" + proxy.ip + ":5555");

            while (true) {
                try {
                    // Recibir un mensaje del sensor
                    String mensaje = socketMedicion.recvStr();
                    System.out.println("Mensaje recibido: " + mensaje);
                } catch (Exception e) {
                    System.out.println("Error al recibir mensaje: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error en el contexto ZeroMQ: " + e.getMessage());
        }


    }
}
