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

public class CentralProxy {
    public static void main(String[] args) throws Exception {
        Proxy proxy = new Proxy(Ip.PROXY_PRINCIPAL, 5, Ip.SC_FOG, Ip.HEALTH_CHECKER, Ip.CENTRAL_SENSOR, Ip.CLOUD);
        try (ZContext context = new ZContext()) {
            // Crear socket para recibir mediciones (PULL)
            ZMQ.Socket socketMedicion = context.createSocket(SocketType.PULL);
            socketMedicion.bind("tcp://" + proxy.getIp() + ":" + Ip.PORT_SENSOR_PROXY);
            // Inicializar el mapa de medidas
            ConcurrentHashMap<String, List<Medicion>> medidasPorTipo = new ConcurrentHashMap<>();

            while (true) {
                try {
                    // Recibir un mensaje del sensor
                    String mensaje = socketMedicion.recvStr();
                    Medicion medicion = Medicion.fromJson(mensaje);

                    // Agregar la medida al mapa
                    if(medicion.isCorrecta()){
                        medidasPorTipo.computeIfAbsent(medicion.getTipoSensor(), k -> new ArrayList<>()).add(medicion);
                    }

                    // Crea y ejecuta 3 hilo para manejar las mediciones de cada tipo de sensor
                    Thread[] threads = new Thread[3];
                    String[] tipoSensor = {TipoSensor.HUMO, TipoSensor.HUMEDAD, TipoSensor.TEMPERATURA};

                    for (int i = 0; i < tipoSensor.length; i++) {
                        threads[i] = new Thread(new ProxyHandler(tipoSensor[i], medidasPorTipo, proxy));
                        threads[i].start();
                    }

                    // Espera a que todos los hilos terminen
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error al recibir mensaje: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error en el contexto ZeroMQ: " + e.getMessage());
        }


    }
}
