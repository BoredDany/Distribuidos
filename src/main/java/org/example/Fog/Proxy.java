package org.example.Fog;

import org.example.Edge.Ip;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public void start() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://" + ipChecker + ":5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] message = socket.recv(0);
                String request = new String(message, ZMQ.CHARSET);

                if (request.equals("Verificar servidor")) {
                    socket.send("Servidor funcionando".getBytes(ZMQ.CHARSET), 0);
                } else {

                }
            }
        }
    }
    private String recibirMedicion(){
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.PULL);
            socket.connect("tcp://" + ipCentralSensor + ":5557");
            return socket.recvStr();
        } catch (Exception e) {
            System.out.println("Error al enviar ip del servidor: " + e.getMessage());
            return "";
        }
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
