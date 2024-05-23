package org.example.Cloud;

import org.example.utils.Checkeo;
import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class HealthChecker {
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            // Socket para comunicación con proxy (REPLY)
            ZMQ.Socket socketProxy = context.createSocket(ZMQ.REQ);
            socketProxy.connect("tcp://" + Ip.IP_FOG + ":" + Ip.PORT_PROXY_CHECKER);

            // Socket para comunicación con cloud (REPLY)
            /*ZMQ.Socket socketCloud = context.createSocket(ZMQ.REQ);
            socketCloud.connect("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_CLOUD_CHECKER);*/

            /*// Socket para comunicación con edge (REPLY)
            ZMQ.Socket socketEdge = context.createSocket(ZMQ.REQ);
            socketEdge.connect("tcp://" + Ip.IP_EDGE + ":" + Ip.PORT_EDGE_CHECKER);*/

            while (!Thread.currentThread().isInterrupted()) {
                // Enviar checkeo a proxy
                Checkeo check = new Checkeo(Ip.IP_FOG, true);
                socketProxy.send(check.toJson());

                //TODO MANEJAR CAIDA DEL PROXY
                //Esperar respuesta (timeout)

                byte[] responseProxy = socketProxy.recv(0);
                System.out.println("Recibo del proxy: " + new String(responseProxy, ZMQ.CHARSET));

                //Si no responde el proxy cambiarlo
                boolean noSirve = false;
                if(noSirve){
                    //Cambiar proxy
                    String anterior = Ip.IP_FOG;
                    Ip.IP_FOG = Ip.IP_FOG_SECUNDARIO;
                    Ip.IP_FOG_SECUNDARIO = anterior;

                    check.setIp(Ip.IP_FOG);
                    check.setWorks(false);
                }

                // Enviar mensaje a cloud
                /*socketCloud.send(check.toJson());
                byte[] responseCloud = socketCloud.recv(0);
                System.out.println("CLOUD RECIBIO: " + new String(responseCloud, ZMQ.CHARSET));*/

                /*// Enviar mensaje a central sensor para cambiar de proxy
                socketEdge.send(check.toJson());
                byte[] responseEdge = socketEdge.recv(0);
                System.out.println("EDGE RECIBIO: " + new String(responseEdge, ZMQ.CHARSET));*/

                //Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
