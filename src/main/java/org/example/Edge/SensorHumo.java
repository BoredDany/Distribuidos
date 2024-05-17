package org.example.Edge;
import java.util.Random;
import zmq.socket.Pair;

public class SensorHumo extends Sensor {
    public SensorHumo(Integer id, String tipoSensor, String archivoConfig) {
        super(id, tipoSensor, archivoConfig);
        this.setIntervalo(3);
        this.setLimiteInferior(0.0);
        this.setLimiteSuperior(1.0);
    }

    @Override
    public double generarMedicion(Integer dentroRango, Integer fueraRango, Integer erroreno){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public boolean alerta(double medicion){
        return medicion == 1.0;
    }

    @Override
    public  boolean correcta(double medicion){
        return true;
    }

}