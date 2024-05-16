package org.example.Fog;

import org.example.utils.Medicion;
import org.example.utils.TipoSensor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyHandler implements Runnable{
    private String tipoSensor;
    private ConcurrentHashMap<String, List<Medicion>> medidasPorTipo;

    public ProxyHandler(String tipoSensor, ConcurrentHashMap<String, List<Medicion>> medidasPorTipo) {
        this.tipoSensor = tipoSensor;
        this.medidasPorTipo = medidasPorTipo;
    }

    @Override
    public void run() {
        List<Medicion> mediciones = medidasPorTipo.get(tipoSensor);

        if (mediciones != null) {
            // Procesar las mediciones...
            switch (tipoSensor) {
                case TipoSensor.HUMO:
                    // Lógica para procesar las mediciones de humo
                    for (Medicion medicion : mediciones) {
                        System.out.println(TipoSensor.HUMO + " -> " + medicion.medicionStr());
                    }
                    break;
                case TipoSensor.HUMEDAD:
                    // Lógica para procesar las mediciones de humedad
                    break;
                case TipoSensor.TEMPERATURA:
                    // Lógica para procesar las mediciones de temperatura
                    break;
            }
        }
    }
}
