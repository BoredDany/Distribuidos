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

    public double calcPromedio (List<Medicion> mediciones){
        double promedio = 0.0;

        for (int i = 0; i < 10; i++) {
            promedio += mediciones.get(i).getMedicion();
        }
        promedio /= 10;

        // Eliminar las primeras 10 mediciones
        for (int i = 0; i < 10; i++) {
            mediciones.remove(0);
        }

        return promedio;
    }
}
