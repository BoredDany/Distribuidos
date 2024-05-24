package org.example.Cloud;

import org.example.utils.Checkeo;
import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class HealthChecker {
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            // Crear un socket para recibir mensajes de PROXY
            ZMQ.Socket socketProxy = context.createSocket(ZMQ.REQ);
            socketProxy.connect("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_PROXY_CHECKER);
            // Establecer timeout de 3 segundos para recibir respuesta
            socketProxy.setReceiveTimeOut(3000);

            // Socket para comunicación con edge (PUB)
            ZMQ.Socket publisher = context.createSocket(ZMQ.PUB);
            publisher.bind("tcp://" + Ip.IP_EDGE + ":" + Ip.PORT_EDGE_CHECKER_HUMO);

            // Socket para comunicación con cloud (PUB)
            ZMQ.Socket publisherCloud = context.createSocket(ZMQ.PUB);
            publisherCloud.bind("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_CLOUD_CHECKER);

            while (true) {
                // Enviar una solicitud al Proxy
                String request = "CHECK";
                socketProxy.send(request.getBytes(ZMQ.CHARSET), 0);

                // Intentar recibir una respuesta del Proxy
                byte[] response = socketProxy.recv(0);
                if (response == null) {
                    // Si no se recibe ninguna respuesta en 3 segundos, imprimir un mensaje
                    System.out.println("ALERT: No response received from Proxy within 3 seconds.");

                    // Cambiar ip del proxy
                    String anterior = Ip.IP_FOG;
                    Ip.IP_FOG = Ip.IP_FOG_SECUNDARIO;
                    Ip.IP_FOG_SECUNDARIO = anterior;
                    Checkeo checkeo = new Checkeo(Ip.IP_FOG, false);

                    publisherCloud.send(checkeo.toJson());
                    System.out.println("ENVIE AL CLOUD PUB++++++");

                    // Enviar un mensaje al edge con la nueva IP del proxy
                    publisher.send(checkeo.toJson());
                    System.out.println("ENVIE AL EDGE PUB++++++");

                    // Close and reopen the socket to reset its state
                    socketProxy.close();
                    socketProxy = context.createSocket(ZMQ.REQ);
                    socketProxy.connect("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_PROXY_CHECKER);
                    socketProxy.setReceiveTimeOut(3000); // Set the receive timeout on the new socket
                } else {
                    // Si se recibe una respuesta, imprimir la respuesta
                    System.out.println("Response from Proxy: " + new String(response, ZMQ.CHARSET));
                }

                // Esperar 2 segundos antes de enviar la próxima solicitud
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
