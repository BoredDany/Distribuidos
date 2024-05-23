package org.example.Edge;

import org.example.utils.Ip;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Aspersor {

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            // Socket para comunicación con sensores de humo (REPLY)
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://" + Ip.IP_EDGE + ":" + Ip.PORT_SENSOR_ASPERSOR);

            while (!Thread.currentThread().isInterrupted()) {
                // Bloqueo hasta que se reciba un mensaje
                byte[] reply = socket.recv(0);

                // Mostrar mensaje
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");

                // Responder a sensor
                String response = "Activo aspersor";

                // Enviar respuesta
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
