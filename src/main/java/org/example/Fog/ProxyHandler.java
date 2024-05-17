package org.example.Fog;

import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyHandler implements Runnable {
    private final String tipoSensor;
    private final List<Medicion> mediciones = new ArrayList<>();

    public ProxyHandler(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    public void addMedicion(Medicion medicion) {
        synchronized (mediciones) {
            mediciones.add(medicion);
            if (mediciones.size() == 10) {
                double promedio = mediciones.stream()
                        .mapToDouble(Medicion::getMedicion)
                        .average()
                        .orElse(0.0);
                System.out.println("Promedio -> " + medicion.getTipoSensor() + ": " + promedio);
                // TODO Enviar el promedio de humedad y la alerta de temperatura a cloud con request reply
                // TODO Alertar sistema de calidad si la temoeratura supera el valor permitido
                mediciones.clear();
            }
        }
    }

    @Override
    public void run() {

    }
}