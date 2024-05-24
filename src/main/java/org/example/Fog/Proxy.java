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
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public class Proxy {

    private String ip;
    private Integer intervaloHumedad;
    private String ipSistemaCalidad;
    private String ipChecker;
    private String ipCentralSensor;
    private String ipCloud;
    private final ConcurrentHashMap<String, ProxyHandler> handlers = new ConcurrentHashMap<>();
    private static int cantidadMensajes = 0;

    private static final List<Long> promedioTiempoComunicacion = new ArrayList<>();
    private long promedio = 0;
    private double varianza;
    private double desviacion;
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

            // Socket para comunicación con checker (REPLY)
            /*ZMQ.Socket socketChecker = context.createSocket(ZMQ.REP);
            socketChecker.bind("tcp://" + Ip.IP_FOG + ":" + Ip.PORT_PROXY_CHECKER);*/

            while (true) {
                try {
                    /*//TODO MANEJO DE FALLAS PROXY
                    //Reportarse con checker
                    byte[] reply = socketChecker.recv(0);
                    String jsonMessage = new String(reply, ZMQ.CHARSET);
                    Checkeo checkeo = Checkeo.fromJson(jsonMessage);
                    System.out.println("Confirmo funcionamiento: " + checkeo.toString());
                    socketChecker.send(checkeo.toJson());*/

                    // Recibir un mensaje del sensor
                    String mensaje = socketMedicion.recvStr();
                    Medicion medicion = Medicion.fromJson(mensaje);

                    if (medicion.isAlerta() && medicion.isCorrecta()) {
                        // Registra el tiempo antes de enviar el mensaje
                        long startTime = System.currentTimeMillis();
                        socketCloud.send(medicion.toJson());
                        byte[] response = socketCloud.recv(0);
                        // Registra el tiempo después de recibir la respuesta
                        long endTime = System.currentTimeMillis();
                        // Calcula y muestra la duración de la comunicación
                        long duration = endTime - startTime;
                        promedioTiempoComunicacion.add(duration);
                        promedio = promedioTiempoComunicacion.stream().mapToLong(Long::longValue).sum() / promedioTiempoComunicacion.size();
                        varianza = promedioTiempoComunicacion.stream()
                                .mapToDouble(num -> Math.pow(num - promedio, 2))
                                .average()
                                .orElse(0.0);
                        desviacion = round(sqrt(varianza));

                        System.out.println("Tiempo de comunicación: " + duration + " ms");
                        System.out.println("Desviacion: " + varianza);
                        System.out.println("Recibo del cloud: " + new String(response, ZMQ.CHARSET));
                        synchronized (Proxy.class) {
                            cantidadMensajes++;
                            System.out.println("Cantidad de mensajes: " + cantidadMensajes);
                        }
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