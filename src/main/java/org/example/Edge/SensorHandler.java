package org.example.Edge;

import org.example.utils.Checkeo;
import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.LocalTime;

public class SensorHandler implements Runnable {
    private String tipoSensor;
    private Integer idSensor;
    private String archivoConfig;
    private static int cantidadMensajes = 0;

    public SensorHandler(String tipoSensor, Integer id, String archivoConfig) {
        this.idSensor = id;
        this.tipoSensor = tipoSensor;
        this.archivoConfig = archivoConfig;
    }

    @Override
    public void run() {
        Sensor sensor = null;
        Integer dentroRango = 0;
        Integer fueraRango = 0;
        Integer erroreno = 0;

        if (tipoSensor.equals(TipoSensor.HUMEDAD)) {
            sensor = new SensorHumedad(idSensor, tipoSensor, archivoConfig);
        } else if (tipoSensor.equals(TipoSensor.HUMO)) {
            sensor = new SensorHumo(idSensor, tipoSensor, archivoConfig);
        } else if (tipoSensor.equals(TipoSensor.TEMPERATURA)) {
            sensor = new SensorTemperatura(idSensor, tipoSensor, archivoConfig);
        }

        if (sensor != null) {
            sensor.inicializar();

            try (ZContext context = new ZContext()) {
                ZMQ.Socket socketMedicion = context.createSocket(SocketType.PUSH);
                socketMedicion.connect("tcp://" + Ip.IP_FOG + ":" + Ip.PORT_SENSOR_PROXY);

                ZMQ.Socket socketAspersor = context.createSocket(SocketType.REQ);
                socketAspersor.connect("tcp://" + Ip.IP_EDGE + ":" + Ip.PORT_SENSOR_ASPERSOR);

                ZMQ.Socket socketSistemaCalidad = context.createSocket(SocketType.REQ);
                socketSistemaCalidad.connect("tcp://" + Ip.IP_EDGE + ":" + Ip.PORT_SC_EDGE);

                try {
                    while (true) {
                        double medicion = sensor.generarMedicion(dentroRango, fueraRango, erroreno);

                        LocalTime now = LocalTime.now();
                        String hora = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();

                        Medicion medicionMensaje = new Medicion(sensor.getTipoSensor(), sensor.getId(), medicion, hora, sensor.alerta(medicion), sensor.correcta(medicion));

                        System.out.println("Envío medicion a:" + Ip.IP_FOG + " - " + medicionMensaje.medicionStr());

                        synchronized (SensorHandler.class) {
                            if (sensor.getTipoSensor().equals(TipoSensor.HUMO)) {
                                if (medicion < 0.0) {
                                    erroreno++;
                                } else if (medicion >= sensor.getLimiteInferior() && medicion <= sensor.getLimiteSuperior()) {
                                    dentroRango++;
                                    if (medicion == sensor.getLimiteSuperior()) {
                                        socketAspersor.send(medicionMensaje.medicionStr());
                                        byte[] responseAspersor = socketAspersor.recv(0);
                                        System.out.println("Recibo del aspersor: " + new String(responseAspersor, ZMQ.CHARSET));
                                        cantidadMensajes++;
                                        System.out.println("Cantidad mensajes: " + cantidadMensajes);

                                        socketSistemaCalidad.send(medicionMensaje.medicionStr());
                                        byte[] responseSC = socketSistemaCalidad.recv(0);
                                        System.out.println("Respuesta del sistema de calidad: " + new String(responseSC, ZMQ.CHARSET));
                                        cantidadMensajes++;
                                        System.out.println("Cantidad mensajes: " + cantidadMensajes);
                                    }
                                } else if (medicion > sensor.getLimiteSuperior()) {
                                    fueraRango++;
                                }
                            } else {
                                if (medicion < 0.0) {
                                    erroreno++;
                                } else if (medicion >= sensor.getLimiteInferior() && medicion <= sensor.getLimiteSuperior()) {
                                    dentroRango++;
                                } else if (medicion < sensor.getLimiteInferior() || medicion > sensor.getLimiteSuperior()) {
                                    fueraRango++;
                                }
                            }

                            socketMedicion.send(medicionMensaje.toJson());
                            cantidadMensajes++;
                            System.out.println("Cantidad mensajes: " + cantidadMensajes);
                        }

                        Thread.sleep(sensor.getIntervalo() * 1000);
                    }
                } catch (Exception e) {
                    System.out.println("Error al medir: " + e.getMessage());
                    socketMedicion.close();
                }
            } catch (Exception e) {
                System.out.println("Error creando contexto ZMQ: " + e.getMessage());
            }
        }
    }
}
