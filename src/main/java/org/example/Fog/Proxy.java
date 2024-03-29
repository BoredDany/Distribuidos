package org.example.Fog;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Proxy {
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://localhost:5000");

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] reply = socket.recv(0);

                // Print the message
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");

                // Parse the message and send an appropriate response
                String response;
                if (mensaje.contains("Medicion Humo")) {
                    double medicion = Double.parseDouble(mensaje.split(":")[1].trim());
                    response = "Medicion de humo ha sido recibida. Resultado: " + medicion;
                } else if (mensaje.contains("Medicion Temperatura")) {
                    double medicion = Double.parseDouble(mensaje.split(":")[1].trim());
                    response = "Medicion de temperatura ha sido recibida. Resultado: " + medicion + " °C";
                } else if (mensaje.contains("Medicion Humedad")) {
                    double medicion = Double.parseDouble(mensaje.split(":")[1].trim());
                    response = "Medicion de humedad ha sido recibida. Resultado: " + medicion + " %";
                } else {
                    response = "Tipo de medicion no reconocido";
                }

                // Send the response
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        }
    }
}
