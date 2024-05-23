package org.example.Cloud;

import org.example.utils.Ip;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class SistemaCalidadCloud {
    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            // Socket para comunicación con cloud (REPLY)
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_SC_CLOUD);

            while (!Thread.currentThread().isInterrupted()) {

                // Bloqueo hasta que se reciba un mensaje
                byte[] reply = socket.recv(0);

                // Mostrar mensaje
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");

                // Responder a sensor
                String responseSC = "Sistema de calidad - CAPA CLOUD: Activo";

                // Enviar respuesta
                socket.send(responseSC.getBytes(ZMQ.CHARSET), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
