package org.example.Fog;

import org.example.utils.Ip;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class SistemaCalidad_Fog {

    private static String ipProxy = Ip.PROXY_PRINCIPAL;
    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            // Socket para comunicación con sensores de humo (REPLY)
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://" + ipProxy + ":" + Ip.PORT_SC_FOG);

            while (!Thread.currentThread().isInterrupted()) {

                // Bloqueo hasta que se reciba un mensaje
                byte[] reply = socket.recv(0);

                // Mostrar mensaje
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");

                // Responder a sensor
                String responseSC = "Sistema de calidad - CAPA FOG: Activo";

                // Enviar respuesta
                socket.send(responseSC.getBytes(ZMQ.CHARSET), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
