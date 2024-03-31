package org.example.Fog;

import org.example.Edge.Ip;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Healthchecker {
    private String ipProxyPrincipal = Ip.PROXY_PRINCIPAL;
    private String ipProxyReplica = Ip.PROXY_SECUNDARIO;
    private String ipCentralSensor = Ip.CENTRAL_SENSOR;


    public boolean verificarServidor(String ip) {
        try (ZContext context = new ZContext()) {
            // Conexión al servidor
            ZMQ.Socket servidorSocket = context.createSocket(SocketType.REQ);
            servidorSocket.connect("tcp://" + ipProxyPrincipal + ":5555");

            // Envía una solicitud al servidor
            servidorSocket.send("Verificar servidor".getBytes(ZMQ.CHARSET), 0);

            byte[] reply = servidorSocket.recv(0);
            String respuesta = new String(reply);
            System.out.println("Respuesta del servidor: " + respuesta);
            return respuesta.equals("Servidor funcionando");
        } catch (Exception e) {
            System.out.println("Error al verificar el servidor: " + e.getMessage());
            return false;
        }
    }

    public void enviarIPProxy(String ip){
        try (ZContext context = new ZContext(1)) {
            ZMQ.Socket socket = context.createSocket(SocketType.PUSH);
            socket.bind("tcp://" + ipCentralSensor + ":4242");
            socket.send(ip);
        } catch (Exception e) {
            System.out.println("Error al enviar ip del servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Healthchecker healthchecker = new Healthchecker();
        String ip= Ip.PROXY_PRINCIPAL;
        while(true){
            if (healthchecker.verificarServidor(ip)) {
                // Si el servidor está sirviendo, enviar ip de proxy principal a seonsores
                System.out.println("ENVIANDO IP");
                healthchecker.enviarIPProxy(Ip.PROXY_PRINCIPAL);
                System.out.println("El servidor está sirviendo++++++++++++++++++++++++++++");

            } else {
                // Si el servidor no está sirviendo, enviar ip de réplica a sensores
                System.out.println("ENVIANDO IP");
                healthchecker.enviarIPProxy(Ip.PROXY_SECUNDARIO);
                System.out.println("El servidor no está sirviendo---------------------------");

            }

        }
    }
}
