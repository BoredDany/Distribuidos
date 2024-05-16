package org.example.Edge.Cloud;

import org.example.Fog.Proxy;
import org.example.Fog.ProxyHandler;
import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CentralCloud {
    public static void main(String[] args) throws Exception {
        Cloud cloud = new Cloud(Ip.CLOUD, Ip.PROXY_PRINCIPAL, Ip.SC_CLOUD, 20);
        try (ZContext context = new ZContext()) {
            // Socket para comunicación con proxy (REPLY)
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://" + cloud.getIpProxy() + ":" + Ip.PORT_PROXY_CLOUD);

            while (!Thread.currentThread().isInterrupted()) {
                // Bloqueo hasta que se reciba un mensaje
                byte[] reply = socket.recv(0);

                // Mostrar mensaje
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");

                // Responder a sensor
                String response = "Recibido en nube";

                // Enviar respuesta
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
