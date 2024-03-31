package org.example.Edge;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.Instant;

public class SensorHandler implements Runnable{
    private String tipoSensor;
    private String ipSistemaCalidad;
    private String ipChecker = Ip.HEALTH_CHECKER;
    private String ipCentralSensor = Ip.CENTRAL_SENSOR;
    private String ipProxy;
    private ZContext context;
    public SensorHandler(String tipoSensor, ZContext context) {
        this.context = context;
        this.tipoSensor = tipoSensor;
    }

    @Override
    public void run() {
        Sensor sensor = null;
        if(tipoSensor.equals(TipoSensor.HUMEDAD)){
            sensor = new SensorHumedad(tipoSensor, TipoSensor.CONFIGHUMEDAD);
        }else if(tipoSensor.equals(TipoSensor.HUMO)){
            sensor = new SensorHumo(tipoSensor, TipoSensor.CONFIGHUMO);
        }else if(tipoSensor.equals(TipoSensor.TEMPERATURA)){
            sensor = new SensorTemperatura(tipoSensor, TipoSensor.CONFIGTEMPERATURA);
        }

        if (sensor != null) {
            sensor.inicializar();
            ZMQ.Socket socket = context.createSocket(SocketType.PULL);
            socket.connect("tcp://" + ipChecker + ":4242");

            ZMQ.Socket socketMedicion = context.createSocket(SocketType.PUSH);

            try {
                while (true){
                    //generar medición cada t unidad de tiempo según el tipo
                    double medicion = sensor.generarMedicion();

                    //obtener la hora
                    Instant instant = Instant.now();
                    long epochMilli = instant.toEpochMilli();
                    long seconds = epochMilli / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    int hour = (int) hours % 24;
                    int minute = (int) minutes % 60;
                    int second = (int) seconds % 60;

                    //obtener ip del servidor

                    String ip = socket.recvStr();

                    String mensaje = sensor.getTipoSensor() + ":" + medicion + " - time:" + hour + ":" + minute + ":" + second;

                    //enviar a proxy
                    socketMedicion.bind("tcp://" + ipCentralSensor + ":5557");
                    socketMedicion.send(mensaje);
                    System.out.println("Envio mensaje");
                    System.out.println("IP:" + ip + " - Mensaje: " + mensaje + " - time:" + hour + ":" + minute + ":" + second);

                    //esperar para siguiente medición

                }
            }catch (Exception e){
                System.out.println("Error al medir: " + e.getMessage());
                socket.close();
                socketMedicion.close();
            }finally {
                socket.close();
                socketMedicion.close();
            }
        }
    }

}
