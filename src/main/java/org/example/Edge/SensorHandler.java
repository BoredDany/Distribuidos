package org.example.Edge;

import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.LocalTime;

public class SensorHandler implements Runnable{
    private String tipoSensor;
    private Integer idSensor;

    private String archivoConfig;
    private String ipSistemaCalidad;
    private String ipChecker = Ip.HEALTH_CHECKER;
    private String ipCentralSensor = Ip.CENTRAL_SENSOR;
    private String ipProxy = Ip.PROXY_PRINCIPAL;

    public SensorHandler(String tipoSensor, Integer id, String archivoConfig) {
        this.idSensor = id;
        this.tipoSensor = tipoSensor;
        this.archivoConfig = archivoConfig;

        //TODO AGREGAR ARCHIVO DE CONFIGURACION COMO PARAMETRO
    }

    @Override
    public void run() {
        Sensor sensor = null;
        Integer dentroRango = 0;
        Integer fueraRango = 0;
        Integer erroreno = 0;

        if(tipoSensor.equals(TipoSensor.HUMEDAD)){
            //enviar al constructor el archivo de config recibido en los argumentos del main
            sensor = new SensorHumedad(idSensor, tipoSensor, archivoConfig);
        }else if(tipoSensor.equals(TipoSensor.HUMO)){
            sensor = new SensorHumo(idSensor, tipoSensor, archivoConfig);
        }else if(tipoSensor.equals(TipoSensor.TEMPERATURA)){
            sensor = new SensorTemperatura(idSensor, tipoSensor, archivoConfig);
        }

        if (sensor != null) {
            sensor.inicializar();

            // Crear un contexto para este hilo
            try (ZContext context = new ZContext()) {

                // Crear socket de envío de mediciones (PUSH)
                ZMQ.Socket socketMedicion = context.createSocket(SocketType.PUSH);
                // Conectar socket a la ip y puerto del proxy
                socketMedicion.connect("tcp://" + ipProxy + ":" + Ip.PORT_SENSOR_PROXY);

                // Crear socket de comunicación aspersor (REQUEST)
                ZMQ.Socket socketAspersor = context.createSocket(SocketType.REQ);
                // Conectar socket a la ip y puerto de la central de sensores
                socketAspersor.connect("tcp://" + ipCentralSensor + ":" + Ip.PORT_SENSOR_ASPERSOR);

                try {
                    while (true){
                        // Generar medición
                        double medicion = sensor.generarMedicion(dentroRango, fueraRango, erroreno);

                        // Obtener la hora actual
                        LocalTime now = LocalTime.now();
                        String hora = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();

                        // Construir mensaje de medición
                        Medicion medicionMensje = new Medicion(sensor.getTipoSensor(), sensor.getId(), medicion, hora, sensor.alerta(medicion), sensor.correcta(medicion));

                        // Mostrar información a enviar
                        System.out.println("Envío medicion a:" + ipProxy + " - " + medicionMensje.medicionStr());

                        if(sensor.getTipoSensor().equals(TipoSensor.HUMO)){
                            if(medicion < 0.0) {
                                erroreno++;
                            } else if(medicion >= sensor.getLimiteInferior() && medicion <= sensor.getLimiteSuperior()){
                                dentroRango++;
                                //activar aspersor en señal de humo
                                if(medicion == sensor.getLimiteSuperior()){
                                    socketAspersor.send(medicionMensje.medicionStr());
                                    byte[] response = socketAspersor.recv(0);
                                    System.out.println("Recibo del aspersor: " + new String(response, ZMQ.CHARSET));
                                }
                                //TODO ENVIAR SEÑAL A SISTEMA DE CALIDAD CON REQUEST REPLY
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

                        // Enviar medición al proxy
                        socketMedicion.send(medicionMensje.toJson());

                        // Esperar para la siguiente medición (opcional)
                        Thread.sleep(sensor.getIntervalo() * 1000);
                    }
                } catch (Exception e){
                    System.out.println("Error al medir: " + e.getMessage());
                    socketMedicion.close();
                    socketAspersor.close();
                }
            } catch (Exception e){
                System.out.println("Error creando contexto ZMQ: " + e.getMessage());
            }
        }
    }

}
