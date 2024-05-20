package org.example.Fog;

import org.example.Edge.SensorHumedad;
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
    private Medicion medicionRecibida = null;
    private  Double promedioHumedad = 0.0;
    private final ZContext context;
    private final ZMQ.Socket socketCloud;
    private final ZMQ.Socket socketSistemaCalidad;

    public ProxyHandler(String tipoSensor) {
        this.tipoSensor = tipoSensor;
        this.context = new ZContext();
        this.socketCloud = context.createSocket(SocketType.REQ);
        this.socketCloud.connect("tcp://" + Ip.PROXY_PRINCIPAL + ":" + Ip.PORT_PROXY_CLOUD);
        this.socketSistemaCalidad = context.createSocket(SocketType.REQ);
        this.socketSistemaCalidad.connect("tcp://" + Ip.PROXY_PRINCIPAL + ":" + Ip.PORT_SC_FOG);
    }

    public void addMedicion(Medicion medicion) {
        synchronized (mediciones) {
            mediciones.add(medicion);
            this.medicionRecibida = medicion;
            if (mediciones.size() == 10) {
                double promedio = mediciones.stream()
                        .mapToDouble(Medicion::getMedicion)
                        .average()
                        .orElse(0.0);
                System.out.println("Promedio -> " + medicion.getTipoSensor() + ": " + promedio);

                if(this.tipoSensor.equals(TipoSensor.HUMEDAD)) {
                    this.promedioHumedad = promedio;
                }

                try{
                    // Enviar al cloud mediciones
                    if(this.medicionRecibida.getTipoSensor().equals(TipoSensor.TEMPERATURA)){
                        if(promedio < TipoSensor.TEMPERATURA_INFERIOR || promedio > TipoSensor.TEMPERATURA_SUPERIOR){
                            System.out.println("ALERTA TEMPERATURA PROMEDIO -> " + medicion.getTipoSensor() + ": " + promedio);
                            socketCloud.send("ALERTA TEMPERATURA: " + promedio);
                            String respuesta = socketCloud.recvStr();
                            System.out.println("Respuesta del cloud: " + respuesta);

                            // Notificar al sistema de calidad
                            socketSistemaCalidad.send(medicion.medicionStr());
                            byte[] responseSC = socketSistemaCalidad.recv(0);
                            System.out.println("Respuesta del sistema de calidad: " + new String(responseSC, ZMQ.CHARSET));

                            //TODO ENVIAR SEÑAL A SISTEMA DE CALIDAD CON REQUEST REPLY

                        }
                    } else{
                        // enviar promedio al cloud si es humedad
                        socketCloud.send(new Medicion(TipoSensor.PROMEDIO_HUMEDAD, 0, this.promedioHumedad, "hora", true, true).toJson());
                        String respuesta = socketCloud.recvStr();
                        System.out.println("Respuesta del cloud: " + respuesta);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                mediciones.clear();
            }
        }
    }

    @Override
    public void run() {

    }

}