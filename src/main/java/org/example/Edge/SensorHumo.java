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
    public double generarCorrecta(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public double generarFueraDeRango(){
        Random random = new Random();
        double minValue = 1.01;
        double randomValue = minValue + random.nextDouble() * (Double.POSITIVE_INFINITY - minValue);
        return randomValue;
    }

    @Override
    public boolean alerta(double medicion){
        return medicion == 1.0;
    }

}