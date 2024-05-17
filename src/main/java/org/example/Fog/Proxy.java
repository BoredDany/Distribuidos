package org.example.Fog;

import org.example.utils.TipoSensor;
import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class Proxy {

    private String ip = Ip.PROXY_PRINCIPAL;
    private Integer intervaloHumedad;
    private String ipSistemaCalidad;
    private String ipChecker;
    private String ipCentralSensor = Ip.CENTRAL_SENSOR;
    private String ipCloud = Ip.CENTRAL_SENSOR;
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
        handlers.put(TipoSensor.HUMO, new ProxyHandler(TipoSensor.HUMO));
        handlers.put(TipoSensor.HUMEDAD, new ProxyHandler(TipoSensor.HUMEDAD));

        handlers.values().forEach(handler -> new Thread(handler).start());

        try (ZContext context = new ZContext()) {
            // Crear socket para recibir mediciones (PULL)
            ZMQ.Socket socketMedicion = context.createSocket(SocketType.PULL);
            socketMedicion.bind("tcp://" + ip + ":" + Ip.PORT_SENSOR_PROXY);

            while (true) {
                try {
                    // Recibir un mensaje del sensor
                    String mensaje = socketMedicion.recvStr();
                    Medicion medicion = Medicion.fromJson(mensaje);

                    // Enviar la medición al handler correspondiente
                    ProxyHandler handler = handlers.get(medicion.getTipoSensor());
                    if (handler != null && medicion.isCorrecta()) {
                        handler.addMedicion(medicion);
                        //TODO Enviar todas las mediciones correctas y con alerta al cloud
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
