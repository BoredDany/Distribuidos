package org.example.Fog;

import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyHandler implements Runnable{
    private String tipoSensor;
    private Proxy proxy;
    private ConcurrentHashMap<String, List<Medicion>> medidasPorTipo;

    public ProxyHandler(String tipoSensor, ConcurrentHashMap<String, List<Medicion>> medidasPorTipo, Proxy proxy) {
        this.tipoSensor = tipoSensor;
        this.medidasPorTipo = medidasPorTipo;
        this.proxy = proxy;
    }

    @Override
    public void run() {
        List<Medicion> mediciones = medidasPorTipo.get(tipoSensor);

        if (mediciones != null && mediciones.size() >= 10 ) {
            try (ZContext context = new ZContext()) {
                // Crear socket de comunicación cloud (REQUEST)
                ZMQ.Socket socketCloud = context.createSocket(SocketType.REQ);
                // Conectar socket a la ip y puerto del cloud
                socketCloud.connect("tcp://" + proxy.getIpCloud() + ":" + Ip.PORT_PROXY_CLOUD);

                for(Medicion medicion : mediciones){
                    socketCloud.send(medicion.toJson());
                    byte[] response = socketCloud.recv(0);
                    System.out.println("Recibo de cloud: " + new String(response, ZMQ.CHARSET));
                }

                // Procesar el promedio
                switch (tipoSensor) {
                    case TipoSensor.HUMEDAD:
                        // Lógica para procesar el promedio de humedad
                        System.out.println(TipoSensor.HUMEDAD + " -> Promedio: " + proxy.calcPromedio(mediciones));
                        // TODO ENVIAR PROMEDIO AL CLOUD CON REQUEST REPLY

                        try {
                            Thread.sleep(proxy.getIntervaloHumedad() * 1000);
                        } catch (InterruptedException e) {
                            System.out.println("Error sleep proxy: " + e.getMessage());
                        }
                        break;
                    case TipoSensor.TEMPERATURA:
                        // Lógica para procesar el promedio de temperatura
                        System.out.println(TipoSensor.TEMPERATURA + " -> Promedio: " + proxy.calcPromedio(mediciones));
                        //TODO ALERTAR SISTEMA DE CALIDAD Y ENVIAR SMS AL CLOUD SI SUPERA VALOR CON REQUEST REPLY AMBOS
                        break;
                }

            }catch (Exception e){
                System.out.println("Error creando contexto ZMQ: " + e.getMessage());
            }
        }
    }



}
