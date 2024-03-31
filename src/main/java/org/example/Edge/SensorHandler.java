package org.example.Edge;

public class SensorHandler implements Runnable{
    private String tipoSensor;
    private String ipSistemaCalidad;
    private String ipProxy;
    public SensorHandler(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    @Override
    public void run() {
        // Crea 10 objetos de la clase Sensor y ejecuta la acción correspondiente
        for (int i = 0; i < 2; i++) {
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
                try {
                    while (true){
                        //generar medición cada t unidad de tiempo según el tipo
                        double medicion = sensor.generarMedicion();
                        System.out.println("Tipo:" + sensor.getTipoSensor() + "\nMedición generada: " + medicion);
                        //obtener ip del servidor
                        //enviar respuesta al servidor
                        //esperar para siguiente medición
                        Thread.sleep(sensor.getIntervalo()*1000); // Pausa la ejecución durante 3 segundos
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
