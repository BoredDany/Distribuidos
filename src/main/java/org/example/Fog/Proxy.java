package org.example.Fog;

import org.example.utils.Checkeo;
import org.example.utils.TipoSensor;
import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class Proxy {

    private String ip;
    private Integer intervaloHumedad;
    private String ipSistemaCalidad;
    private String ipChecker;
    private String ipCentralSensor;
    private String ipCloud;
    private final ConcurrentHashMap<String, ProxyHandler> handlers = new ConcurrentHashMap<>();


    public Proxy(String ip, Integer intervaloHumedad, String ipSistemaCalidad, String ipChecker, String ipCentralSensor, String ipCloud) {
        this.ip = ip;
        this.intervaloHumedad = intervaloHumedad;
        this.ipSistemaCalidad = ipSistemaCalidad;
        this.ipChecker = ipChecker;
        this.ipCentralSensor = ipCentralSensor;
        this.ipCloud = ipCloud;
    }

    public String getIp() {
        return ip;
    }

    public String getIpCloud() {
        return ipCloud;
    }

    public Integer getIntervaloHumedad() {
        return intervaloHumedad;
    }

    public String getIpSistemaCalidad() {
        return ipSistemaCalidad;
    }

    public String getIpChecker() {
        return ipChecker;
    }

    public String getIpCentralSensor() {
        return ipCentralSensor;
    }

    public void start() {
        
        // Crear y iniciar los handlers
        handlers.put(TipoSensor.TEMPERATURA, new ProxyHandler(TipoSensor.TEMPERATURA));
        handlers.put(TipoSensor.HUMEDAD, new ProxyHandler(TipoSensor.HUMEDAD));

        handlers.values().forEach(handler -> new Thread(handler).start());

        try (ZContext context = new ZContext()) {
            // Crear socket para recibir mediciones (PULL)
            ZMQ.Socket socketMedicion = context.createSocket(SocketType.PULL);
            socketMedicion.bind("tcp://" + Ip.IP_FOG + ":" + Ip.PORT_SENSOR_PROXY);

            // Crear socket de comunicación con cloud (REQUEST)
            ZMQ.Socket socketCloud = context.createSocket(SocketType.REQ);
            socketCloud.connect("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_PROXY_CLOUD);


            // Crear un hilo para manejar las solicitudes del HealthChecker
            /*new Thread(() -> {
                try (ZContext context2 = new ZContext()) {
                    // Crear un socket para comunicarse con CHECKER
                    ZMQ.Socket socketChecker = context2.createSocket(ZMQ.REP);
                    socketChecker.bind("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_PROXY_CHECKER);

                    while (true) {
                        // Esperar una solicitud del HealthChecker
                        byte[] request = socketChecker.recv(0);
                        if (request != null) {
                            // Si se recibe una solicitud, enviar una respuesta
                            String response = "PROXY OK: " + Ip.IP_FOG;
                            socketChecker.send(response.getBytes(ZMQ.CHARSET), 0);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error handling HealthChecker requests: " + e.getMessage());
                }
            }).start();*/


            while (true) {
                try {

                    // Recibir un mensaje del sensor
                    String mensaje = socketMedicion.recvStr();
                    Medicion medicion = Medicion.fromJson(mensaje);

                    if(medicion.isAlerta() && medicion.isCorrecta()){

                        socketCloud.send(medicion.toJson());
                        System.out.println("ENVIO: " + medicion.medicionStr());
                        byte[] response = socketCloud.recv(0);
                        System.out.println("Recibo del cloud: " + new String(response, ZMQ.CHARSET));

                    }

                    // Enviar la medición al handler correspondiente
                    ProxyHandler handler = handlers.get(medicion.getTipoSensor());
                    if (handler != null && medicion.isCorrecta()) {
                        handler.addMedicion(medicion);
                    }
                } catch (Exception e) {
                    System.out.println("Error al recibir mensaje: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error creando contexto ZMQ: " + e.getMessage());
        }
    }
}
