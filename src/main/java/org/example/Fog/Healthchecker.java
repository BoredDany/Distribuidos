package org.example.Fog;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Healthchecker {
    private String ipProxyPrincipal;
    private String ipProxyReplica;
    private String ipCentralSensor;

    public Healthchecker(String ipProxyPrincipal, String ipProxyReplica, String ipCentralSensor) {
        this.ipProxyPrincipal = ipProxyPrincipal;
        this.ipProxyReplica = ipProxyReplica;
        this.ipCentralSensor = ipCentralSensor;
    }

    public boolean verificarServidor() {
        try (ZContext context = new ZContext()) {
            // Conexión al servidor
            ZMQ.Socket servidorSocket = context.createSocket(SocketType.REQ);
            servidorSocket.connect("tcp://" + ipProxyPrincipal + ":5555");

            // Envía una solicitud al servidor
            servidorSocket.send("Verificar servidor".getBytes(ZMQ.CHARSET), 0);

            // Recibe la respuesta del servidor
            byte[] reply = servidorSocket.recv(0);
            String respuesta = new String(reply);
            System.out.println("Respuesta del servidor: " + respuesta);
            return respuesta.equals("Servidor funcionando");
        } catch (Exception e) {
            System.out.println("Error al verificar el servidor: " + e.getMessage());
            return false;
        }
    }
    public static void main(String[] args) {
        Healthchecker healthchecker = new Healthchecker("192.168.20.8", "192.168.20.8", "192.168.20.8");
        while(true){
            if (healthchecker.verificarServidor()) {
                // Si el servidor está sirviendo, enviar ip de proxy principal a seonsores
                System.out.println("El servidor está sirviendo++++++++++++++++++++++++++++");

            } else {
                // Si el servidor no está sirviendo, enviar ip de réplica a sensores
                System.out.println("El servidor no está sirviendo---------------------------");

            }
        }
    }
}
