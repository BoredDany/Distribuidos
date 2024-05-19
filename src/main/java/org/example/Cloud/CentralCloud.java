package org.example.Cloud;

import org.example.utils.Ip;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

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
