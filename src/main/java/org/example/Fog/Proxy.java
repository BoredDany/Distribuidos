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
    private Integer intervaloTemperatura;
    private Integer intervaloHumedad;
    private Integer ipSistemaCalidad;
    private String ipChecker;
    private String ipCentralSensor = Ip.CENTRAL_SENSOR;

    public Proxy(Integer intervaloTemperatura, Integer intervaloHumedad) {
        this.intervaloTemperatura = intervaloTemperatura;
        this.intervaloHumedad = intervaloHumedad;
    }

    public static void main(String[] args) throws Exception {
        Proxy proxy = new Proxy(5, 5);
        try (ZContext context = new ZContext()) {
            // Crear socket para recibir mediciones (PULL)
            ZMQ.Socket socketMedicion = context.createSocket(SocketType.PULL);
            socketMedicion.bind("tcp://" + proxy.ip + ":5555");
            // Inicializar el mapa de medidas
            ConcurrentHashMap<String, List<Medicion>> medidasPorTipo = new ConcurrentHashMap<>();

            while (true) {
                try {
                    // Recibir un mensaje del sensor
                    String mensaje = socketMedicion.recvStr();
                    Medicion medicion = Medicion.fromJson(mensaje);
                    //System.out.println("Mensaje recibido: " + medicion.medicionStr());

                    // Agregar la medida al mapa
                    medidasPorTipo.computeIfAbsent(medicion.getTipoSensor(), k -> new ArrayList<>()).add(medicion);

                    // Crea y ejecuta 3 hilos: 1 para enviar las alertas de los sensores,
                    // otro para calcular y enviar los promedios de temperatura y otro para los de humedad
                    Thread[] threads = new Thread[3];
                    String[] tipoSensor = {TipoSensor.HUMO, TipoSensor.HUMEDAD, TipoSensor.TEMPERATURA};

                    for (int i = 0; i < tipoSensor.length; i++) {
                        threads[i] = new Thread(new ProxyHandler(tipoSensor[i], medidasPorTipo));
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
