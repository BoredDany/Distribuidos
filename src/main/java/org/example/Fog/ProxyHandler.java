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

        if (mediciones != null && mediciones.size() >= 10 &&
                (tipoSensor.equals(TipoSensor.HUMEDAD) ||
                        tipoSensor.equals(TipoSensor.TEMPERATURA))) {
            // Calcular el promedio de las primeras 10 mediciones
            double promedio = 0.0;
            for (int i = 0; i < 10; i++) {
                promedio += mediciones.get(i).getMedicion();
            }
            promedio /= 10;

            // Eliminar las primeras 10 mediciones
            for (int i = 0; i < 10; i++) {
                mediciones.remove(0);
            }

            // Procesar el promedio...
            switch (tipoSensor) {
                case TipoSensor.HUMO:
                    // Lógica para procesar el promedio de humo
                    break;
                case TipoSensor.HUMEDAD:
                    // Lógica para procesar el promedio de humedad
                    System.out.println(TipoSensor.HUMEDAD + " -> Promedio: " + promedio);
                    break;
                case TipoSensor.TEMPERATURA:
                    // Lógica para procesar el promedio de temperatura
                    System.out.println(TipoSensor.TEMPERATURA + " -> Promedio: " + promedio);
                    break;
            }
        }
    }

}
