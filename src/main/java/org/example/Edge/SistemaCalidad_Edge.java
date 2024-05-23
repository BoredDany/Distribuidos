package org.example.Edge;

import org.example.utils.Ip;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class SistemaCalidad_Edge {


    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            //TODO TOLERANCIA A FALLAS DE PROXY
            //Recibir ip del checker y revisar si es true o false para terminar o no el proceso

            // Socket para comunicación con sensores de humo (REPLY)
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://" + Ip.IP_EDGE + ":" + Ip.PORT_SC_EDGE);

            while (!Thread.currentThread().isInterrupted()) {

                // Bloqueo hasta que se reciba un mensaje
                byte[] reply = socket.recv(0);

                // Mostrar mensaje
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");

                // Responder a sensor
                String responseSC = "Sistema de calidad - CAPA EDGE: Activo";

                // Enviar respuesta
                socket.send(responseSC.getBytes(ZMQ.CHARSET), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
