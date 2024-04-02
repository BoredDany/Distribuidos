package org.example.Edge;

import org.example.utils.Ip;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Aspersor {
    private String ipCentralSensores = Ip.CENTRAL_SENSOR;

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://" + Ip.CENTRAL_SENSOR + ":5000");

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] reply = socket.recv(0);

                // Print the message
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");

                // Responder a sensor
                String response = "Activo aspersor";

                // Send the response
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        }
    }
}
