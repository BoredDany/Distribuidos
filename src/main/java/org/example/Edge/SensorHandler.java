package org.example.Edge;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SensorHandler implements Runnable{
    private String tipoSensor;
    private String ipSistemaCalidad;
    private String ipChecker = Ip.HEALTH_CHECKER;
    private String ipCentralSensor = Ip.CENTRAL_SENSOR;
    private String ipProxy = Ip.PROXY_PRINCIPAL;

    public SensorHandler(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    @Override
    public void run() {
        Sensor sensor = null;
        Integer dentroRango = 0;
        Integer fueraRango = 0;
        Integer erroreno = 0;

        if(tipoSensor.equals(TipoSensor.HUMEDAD)){
            sensor = new SensorHumedad(tipoSensor, TipoSensor.CONFIGHUMEDAD);
        }else if(tipoSensor.equals(TipoSensor.HUMO)){
            sensor = new SensorHumo(tipoSensor, TipoSensor.CONFIGHUMO);
        }else if(tipoSensor.equals(TipoSensor.TEMPERATURA)){
            sensor = new SensorTemperatura(tipoSensor, TipoSensor.CONFIGTEMPERATURA);
        }

        if (sensor != null) {
            sensor.inicializar();

            // Crear un contexto para este hilo
            try (ZContext context = new ZContext()) {

                // Crear socket de envío de mediciones (PUSH)
                ZMQ.Socket socketMedicion = context.createSocket(SocketType.PUSH);
                socketMedicion.connect("tcp://" + ipProxy + ":5555");

                try {
                    while (true){
                        // Generar medición cada cierto intervalo según el tipo de sensor
                        double medicion = sensor.generarMedicion(dentroRango, fueraRango, erroreno);

                        if(sensor.getTipoSensor().equals(TipoSensor.HUMO)){
                            if(medicion < 0.0) {
                                erroreno++;
                            } else if(medicion >= sensor.getLimiteInferior() && medicion <= sensor.getLimiteSuperior()){
                                dentroRango++;
                            }else if (medicion > sensor.getLimiteSuperior()){
                                fueraRango++;
                            }
                        }else{
                            if(medicion < 0.0) {
                                erroreno++;
                            } else if(medicion >= sensor.getLimiteInferior() && medicion <= sensor.getLimiteSuperior()){
                                dentroRango++;
                            }else if (medicion < sensor.getLimiteInferior() || medicion > sensor.getLimiteSuperior()){
                                fueraRango++;
                            }
                        }

                        // Obtener la hora actual
                        Instant instant = Instant.now();
                        long epochMilli = instant.toEpochMilli();
                        long seconds = epochMilli / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        int hour = (int) hours % 24;
                        int minute = (int) minutes % 60;
                        int second = (int) seconds % 60;

                        // Construir mensaje de medición
                        String mensaje = sensor.getTipoSensor() + ":" + medicion + " - time:" + hour + ":" + minute + ":" + second;

                        // Enviar medición al proxy
                        socketMedicion.send(mensaje);

                        // Mostrar información
                        System.out.println("Envío mensaje a la IP:" + ipProxy + " - Mensaje: " + mensaje);

                        // Esperar para la siguiente medición (opcional)
                        Thread.sleep(sensor.getIntervalo() * 1000);
                    }
                } catch (Exception e){
                    System.out.println("Error al medir: " + e.getMessage());
                    socketMedicion.close();
                }
            }
        }
    }

}
